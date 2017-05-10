package com.sky.projects.analysis.service;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sky.projects.analysis.config.Config;
import com.sky.projects.analysis.config.DBConfig;
import com.sky.projects.analysis.entity.EquipLocation;
import com.sky.projects.analysis.entity.Location;
import com.sky.projects.analysis.entity.Record;
import com.sky.projects.analysis.entity.Region;
import com.sky.projects.analysis.util.JDBCUtil;
import com.sky.projects.analysis.util.Services;

import scala.Tuple2;

@SuppressWarnings("serial")
public class SZDataAnalysis implements Config, DBConfig, Serializable {

	private static final Log LOG = LogFactory.getLog(DataAnalysis.class);

	private static final String HEAT_SQL = "REPLACE INTO devicenumhistoryinfo(devicenum,start_time,number,countinto,countleave) VALUES(?,?,?,?,?)";

	private static final String TRACK_SQL = "REPLACE INTO tbl_person_location(insert_time,latitude,longitude,trajectory,person_id) VALUES(?,?,?,?,?)";

	private static final String DISTRIBUTE_QUERY = "SELECT id,insert_time,service_code,equipment_num,latitude,longitude FROM tbl_person_distribute WHERE person_id=?";

	private static final String DISTRIBUTE_SQL = "REPLACE INTO tbl_person_distribute(insert_time,service_code,equipment_num,latitude,longitude,person_id) VALUES(?,?,?,?,?,?)";

	private static final String WARN_SQL = "REPLACE INTO tbl_area_warn(id,area_id,person_id,latitude,longitude,create_time) VALUES(?,?,?,?,?,?)";

	public static void execute(JavaPairDStream<String, String> kafkaStream) {
		Services m_serviceCodes = new Services();

		JavaPairDStream<String, String> windowStream = kafkaStream
				.window(Durations.seconds(WINDOW_CAPACITY * EXECUTE_INTERVAL), Durations.seconds(EXECUTE_INTERVAL));

		FlowRouteStatistics.execute(windowStream, EXECUTE_INTERVAL);

		// 应该使用 flatMap
		JavaDStream<Record> cachStream = kafkaStream.map(new Function<Tuple2<String, String>, Record>() {
			@Override
			public Record call(Tuple2<String, String> tuple) {
				try {
					String[] sp = tuple._2.split("[|]");

					return new Record(sp[17], sp[18], Integer.parseInt(sp[1].equals("MULL") ? "2" : sp[1]), sp[0],
							sp[15], Integer.parseInt(sp[4].equals("MULL") ? "0" : sp[4]), Long.parseLong(sp[2]),
							Long.parseLong(sp[3].equals("MULL") ? "0"
									: sp[3]));
				} catch (Exception e) {
					LOG.error("parse exception when parse message:" + tuple._2);
					LOG.error(e);
					return null;
				}
			}
		}).filter(new Function<Record, Boolean>() {
			@Override
			public Boolean call(Record kafkaRecord) throws Exception {
				boolean res = kafkaRecord != null && kafkaRecord.getType() == 2;

				if (res && m_serviceCodes.getM_servics().contains(kafkaRecord.getServiceCode())) {
					res = false;
				}

				return res;
			}
		});

		cachStream.cache();

		cachStream.mapToPair(new PairFunction<Record, String, Record>() {
			@Override
			public Tuple2<String, Record> call(Record kafkaRecord) throws Exception {
				return new Tuple2<>(kafkaRecord.getDeviceNum(), kafkaRecord);
			}
		}).groupByKey().foreachRDD(new VoidFunction2<JavaPairRDD<String, Iterable<Record>>, Time>() {
			@Override
			public void call(JavaPairRDD<String, Iterable<Record>> javaPairRDD, Time time) {
				javaPairRDD.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Iterable<Record>>>>() {
					@Override
					public void call(Iterator<Tuple2<String, Iterable<Record>>> tuple2Iterator) {
						Connection conn = null;
						PreparedStatement pst = null;

						try {
							conn = JDBCUtil.createConnection(JDBCUtil.DBType.MYSQL, Config.DB_IP, Config.DB_PORT,
									Config.DB, Config.DB_USER, Config.DB_PWD);
							conn.setAutoCommit(false);
							pst = conn.prepareStatement(SZDataAnalysis.HEAT_SQL);
							long count = 0L;

							while (tuple2Iterator.hasNext()) {
								Tuple2<String, Iterable<Record>> tuple2 = tuple2Iterator.next();
								String deviceNum = tuple2._1;
								Iterator<Record> it = tuple2._2.iterator();

								long in = 0L;
								long out = 0L;
								long num = 0L;
								Record r = null;

								while (it.hasNext()) {
									r = it.next();

									if (StringUtils.isBlank(deviceNum)) {
										LOG.info("deviceNum is null,record:" + r);
									}

									if (r.getEndTime() != 0L) {
										out += 1L;
									} else if (r.getStartTime() > 0L) {
										in += 1L;
									}
								}

								in = in * (100 - Config.IPHONE_RATIO) / 100L;
								out = out * (100 - Config.IPHONE_RATIO) / 100L;
								num = in - out;

								long createTime = r.getStartTime() - r.getStartTime() % Config.EXECUTE_INTERVAL;

								pst.setString(1, deviceNum);
								pst.setLong(2, createTime);
								pst.setLong(3, num);
								pst.setLong(4, in);
								pst.setLong(5, out);
								pst.addBatch();

								count += 1L;

								if (count % 1000L == 0L) {
									pst.executeBatch();
									conn.commit();
									pst.clearBatch();
								}
							}

							pst.executeBatch();
							conn.commit();
							LOG.info("partition insert into db heat chat record count:" + count);
							pst.clearBatch();
						} catch (SQLException e) {
							LOG.error("error get ");
						} finally {
							try {
								if (pst != null) {
									pst.close();
								}
								if (conn != null) {
									conn.setAutoCommit(true);
									conn.close();
								}
								LOG.info("preparedstatment and connection of this partition has been closed..");
							} catch (Exception e) {
								LOG.info(e);
							}
						}
					}
				});
			}
		});

		LOG.info("------------------------------------------------------------------------------------------");
		LOG.info("heat chat analysis for this batch data,has submitted to DAGScheduler and TaskScheduler......");
		LOG.info("------------------------------------------------------------------------------------------");

		cachStream.mapToPair(new PairFunction<Record, String, Record>() {
			public Tuple2<String, Record> call(Record kafkaRecord) throws Exception {
				return new Tuple2<>(kafkaRecord.getPhone() + "_" + kafkaRecord.getMac(), kafkaRecord);
			}
		}).groupByKey().foreachRDD(new VoidFunction2<JavaPairRDD<String, Iterable<Record>>, Time>() {
			@Override
			public void call(JavaPairRDD<String, Iterable<Record>> javaPairRDD, Time time) throws Exception {
				javaPairRDD.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Iterable<Record>>>>() {
					@Override
					public void call(Iterator<Tuple2<String, Iterable<Record>>> tuple2Iterator) {
						Time myTime = time;

						Connection conn = null;
						PreparedStatement trackPST = null;
						PreparedStatement distributeQuery = null;
						ResultSet queryRS = null;
						PreparedStatement distributePST = null;

						PreparedStatement prewarn = null;
						Map<String, EquipLocation> equipments = null;
						List<Region> regions = null;
						Tuple2<Map<String, String>, Map<String, String>> info = null;
						Map<String, String> macID = null;
						Map<String, String> phoneId = null;
						EquipLocation equip = null;

						try {
							conn = JDBCUtil.createConnection(JDBCUtil.DBType.MYSQL, Config.DB_IP, Config.DB_PORT,
									Config.DB, Config.DB_USER, Config.DB_PWD);
							conn.setAutoCommit(false);
							equipments = JDBCUtil.getEquipsInfo(conn, "equipmentinfo");
							regions = JDBCUtil.getRegionInfo(conn, "carecontextinfo");
							info = JDBCUtil.getFocusUserList(conn, "tbl_focus_person");
							macID = info._1;
							phoneId = info._2;

							LOG.info("equipment info:" + equipments);
							LOG.info("ready to watch mac-id:" + macID);
							LOG.info("ready to watch phone-id:" + phoneId);

							trackPST = conn.prepareStatement(SZDataAnalysis.TRACK_SQL);
							distributeQuery = conn.prepareStatement(SZDataAnalysis.DISTRIBUTE_QUERY, 1005, 1008);
							distributePST = conn.prepareStatement(SZDataAnalysis.DISTRIBUTE_SQL);

							prewarn = conn.prepareStatement(SZDataAnalysis.WARN_SQL);

							long count = 0L;
							long sum = 0L;
							while (tuple2Iterator.hasNext()) {
								Tuple2<String, Iterable<Record>> tuple2 = tuple2Iterator.next();
								String phoneAndMac = (String) tuple2._1;

								String phone = phoneAndMac.split("[_]")[0];
								String mac = phoneAndMac.split("[_]")[1];
								JSONArray array = null;
								if ((macID.containsKey(mac)) || (phoneId.containsKey(phone))) {
									String person_id;
									if (macID.containsKey(mac)) {
										person_id = macID.get(mac);
									} else {
										person_id = phoneId.get(phone);
									}

									LOG.info("FOUND a focused person,phone:" + phone + " mac:" + mac + " persion_id:"
											+ person_id);

									long lastestTime = 0L;
									EquipLocation lastestEquip = new EquipLocation();
									Iterator<Record> it = tuple2._2.iterator();
									array = new JSONArray();
									while (it.hasNext()) {
										Record r = (Record) it.next();
										equip = (EquipLocation) equipments.get(r.getDeviceNum());
										if (!Objects.equals(equip, null)) {

											if (r.getStartTime() >= lastestTime && (System.currentTimeMillis() / 1000L
													- r.getStartTime() < Config.DELAY_START_TIME_SECONDS)) {
												lastestTime = r.getStartTime();
												lastestEquip = equip;
											}
											JSONObject obj = new JSONObject();

											obj.put("servicecode", r.getServiceCode());
											obj.put("longitude", equip.getLongitude());
											obj.put("latitude", equip.getLatitude());
											obj.put("lasttime", lastestTime);
											array.add(obj);
										}
									}
									if ((lastestEquip.getLatitude() != 0.0D) || (lastestEquip.getLongitude() != 0.0D)) {
										LOG.info("lastest equip:" + lastestEquip);
										Location location = new Location(lastestEquip.getLatitude(),
												lastestEquip.getLongitude());

										for (Region region : regions) {
											if (region.containLocation(location)) {
												LOG.info("zd person:" + person_id + ", location:" + location
														+ ", in area:" + region);
												prewarn.setString(1, UUID.randomUUID().toString());
												prewarn.setInt(2, region.getRegionId());
												prewarn.setInt(3, Integer.parseInt(person_id));
												prewarn.setDouble(4, location.getLatitude());
												prewarn.setDouble(5, location.getLongitude());
												prewarn.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

												prewarn.addBatch();
												sum += 1L;

												LOG.info(String.format(
														"insert into table prewarn info:region=%s,person=%s,latitude=%s,longitue=%s",
														region.getRegionId(), person_id, location.getLatitude(),
														location.getLongitude()));
											} else {
												LOG.info("人:" + person_id + " 经纬度:" + location + " 不在区域:" + region);
											}
										}
										LOG.info(String
												.format("calculate result:phone[%s],mac[%s],latitude[%s],longitude[%s],trajectory[%s]",
														new Object[] { phone, mac,
																Double.valueOf(location.getLatitude()),
																Double.valueOf(location.getLongitude()), array }));

										trackPST.setTimestamp(1, new Timestamp(myTime.milliseconds()));
										trackPST.setDouble(2, location.getLatitude());
										trackPST.setDouble(3, location.getLongitude());
										trackPST.setString(4, JSONArray.toJSONString(array));
										trackPST.setLong(5, Long.parseLong(person_id));
										trackPST.addBatch();

										distributeQuery.setString(1, person_id);
										queryRS = distributeQuery.executeQuery();
										if (queryRS.next()) {
											LOG.info(String.format(
													"Update Record For Focus DISTRIBUTE phone[%s],mac[%s],NEW equipInfo[%s],person_id[%s]",
													new Object[] { phone, mac, lastestEquip, person_id }));
											queryRS.updateTimestamp(2, new Timestamp(myTime.milliseconds()));
											queryRS.updateString(3,
													lastestEquip == null ? null : lastestEquip.getServiceCode());
											queryRS.updateString(4,
													lastestEquip == null ? null : lastestEquip.getEquipNum());
											queryRS.updateDouble(5,
													lastestEquip == null ? 0.0D : lastestEquip.getLatitude());
											queryRS.updateDouble(6,
													lastestEquip == null ? 0.0D : lastestEquip.getLongitude());
											queryRS.updateRow();
										} else {
											LOG.info(String.format(
													"New Record For Focus DISTRIBUTE:phone[%s],mac[%s],equipInfo[%s],person_id[%s]",
													new Object[] { phone, mac, lastestEquip, person_id }));

											distributePST.setTimestamp(1, new Timestamp(myTime.milliseconds()));
											distributePST.setString(2,
													lastestEquip == null ? null : lastestEquip.getServiceCode());
											distributePST.setString(3,
													lastestEquip == null ? null : lastestEquip.getEquipNum());
											distributePST.setDouble(4,
													lastestEquip == null ? 0.0D : lastestEquip.getLatitude());
											distributePST.setDouble(5,
													lastestEquip == null ? 0.0D : lastestEquip.getLongitude());
											distributePST.setInt(6, Integer.parseInt(person_id));
											distributePST.addBatch();
											count += 1L;
										}
										queryRS.close();
									}
								}
							}
							trackPST.executeBatch();
							distributePST.executeBatch();
							prewarn.executeBatch();
							conn.commit();
							trackPST.clearBatch();
							distributePST.clearBatch();
							prewarn.clearBatch();
							LOG.info("track and distribute record count:" + count);
							LOG.info("prewarn for important person execute batch,count:" + sum);
							conn.setAutoCommit(true);
							return;
						} catch (SQLException e) {
							LOG.error(e);
						} finally {
							try {
								if (distributeQuery != null) {
									distributeQuery.close();
								}
								if (trackPST != null) {
									trackPST.close();
								}
								if (distributePST != null) {
									distributePST.close();
								}
								if (prewarn != null) {
									prewarn.close();
								}
								if (conn != null) {
									conn.close();
								}
								LOG.info("CLEAR MYSQL SESSION FINISHED.....");
							} catch (Exception e) {
								LOG.error(e);
							}
						}
					}
				});
			}
		});
	}
}