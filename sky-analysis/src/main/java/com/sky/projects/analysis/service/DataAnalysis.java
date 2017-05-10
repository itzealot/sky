package com.sky.projects.analysis.service;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sky.projects.analysis.config.Config;
import com.sky.projects.analysis.config.DBConfig;
import com.sky.projects.analysis.entity.Circle;
import com.sky.projects.analysis.entity.EquipLocation;
import com.sky.projects.analysis.entity.Location;
import com.sky.projects.analysis.entity.Record;
import com.sky.projects.analysis.entity.Region;
import com.sky.projects.analysis.util.JDBCUtil;
import com.sky.projects.analysis.util.TransferUtil;

import scala.Tuple2;

/**
 * 数据分析
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class DataAnalysis implements Config, DBConfig, Serializable {

	private static final Log LOG = LogFactory.getLog(DataAnalysis.class);

	private static final String HEAT_SQL = "REPLACE INTO devicenumhistoryinfo(devicenum,start_time,number,countinto,countleave) VALUES(?,?,?,?,?)";

	private static final String TRACK_SQL = "REPLACE INTO tbl_person_location(insert_time,latitude,longitude,trajectory,person_id) VALUES(?,?,?,?,?)";

	private static final String DISTRIBUTE_QUERY = "SELECT id,insert_time,service_code,equipment_num,latitude,longitude FROM tbl_person_distribute WHERE person_id=?";

	private static final String DISTRIBUTE_SQL = "REPLACE INTO tbl_person_distribute(insert_time,service_code,equipment_num,latitude,longitude,person_id) VALUES(?,?,?,?,?,?)";

	private static final String PASS_SERVICE_SQL = "REPLACE INTO tbl_person_pass_service(person_id,service_code,insert_time) VALUES(?,?,?)";

	private static final String WARN_SQL = "REPLACE INTO tbl_area_warn(id,area_id,person_id,latitude,longitude,create_time) VALUES(?,?,?,?,?,?)";

	public static void execute(JavaPairDStream<String, String> kafkaStream) {
		JavaPairDStream<String, String> windowStream = kafkaStream
				.window(Durations.seconds(WINDOW_CAPACITY * EXECUTE_INTERVAL), Durations.seconds(EXECUTE_INTERVAL));

		FlowRouteStatistics.execute(windowStream, EXECUTE_INTERVAL);

		// 从kafka读取记录，并返回mac_phone_deviceNum排重结果
		JavaPairDStream<String, Record> cacheRdd = kafkaStream.map(new Function<Tuple2<String, String>, Record>() {
			@Override
			public Record call(Tuple2<String, String> tuple2) {
				String[] sp = tuple2._2().split("[|]");

				Record record = null;
				try {
					String deviceNum = sp[17];
					int power = Integer.parseInt(sp[4].equals("MULL") ? "0" : sp[4]);
					int type = Integer.parseInt(sp[1].equals("MULL") ? "2" : sp[1]);
					String serviceCode = sp[18];
					long startTime = (sp[2].equals("MULL") ? System.currentTimeMillis() : Long.parseLong(sp[2]));
					long endTime = Long.parseLong(sp[3].equals("MULL") ? "0" : sp[3]);
					String mac = sp[0];
					String phone = sp[15];
					record = new Record(deviceNum, serviceCode, type, mac, phone, power, startTime, endTime);
				} catch (Exception e) {
					LOG.error("error map", e);
				}
				return record;
			}
		}).filter(new Function<Record, Boolean>() {
			@Override
			public Boolean call(Record kafkaRecord) throws Exception {
				return kafkaRecord != null && kafkaRecord.getType() == 2;
			}
		}).mapToPair(new PairFunction<Record, String, Record>() {
			// 构建key值 mac_phone_deviceNum
			@Override
			public Tuple2<String, Record> call(Record r) throws Exception {
				return new Tuple2<>(r.getMac() + "_" + r.getPhone() + "_" + r.getDeviceNum(), r);
			}
		}).reduceByKey(new Function2<Record, Record, Record>() {
			// 保存相同key的最大出现时间
			@Override
			public Record call(Record r1, Record r2) throws Exception {
				return r2.getStartTime() >= r1.getStartTime() ? r2 : r1;
			}
		});

		cacheRdd.cache();

		// mac+phone组成 key
		cacheRdd.mapToPair(new PairFunction<Tuple2<String, Record>, String, Record>() {
			@Override
			public Tuple2<String, Record> call(Tuple2<String, Record> tuple2) throws Exception {
				return new Tuple2<>(tuple2._2.getMac() + tuple2._2.getPhone(), tuple2._2);
			}
		}).reduceByKey(new Function2<Record, Record, Record>() {
			// 根据 power与startTime过滤记录
			@Override
			public Record call(Record r1, Record r2) throws Exception {
				if (r1.getPower() > r2.getPower()) {
					return r1;
				}

				if (r1.getPower() < r2.getPower()) {
					return r2;
				}

				if (r1.getStartTime() <= r2.getStartTime()) {
					return r2;
				}

				return r1;
			}
		}).mapToPair(new PairFunction<Tuple2<String, Record>, String, Record>() {
			// 按照设备排重
			@Override
			public Tuple2<String, Record> call(Tuple2<String, Record> tuple2) throws Exception {
				return new Tuple2<>(tuple2._2.getDeviceNum(), tuple2._2);
			}
		}).groupByKey().foreachRDD(new VoidFunction2<JavaPairRDD<String, Iterable<Record>>, Time>() {
			// key:设备, value:Iterable<Record>
			@Override
			public void call(JavaPairRDD<String, Iterable<Record>> rdd, Time time) {
				// 按照时间删除基于当前时间以前的数据
				Connection conn = null;
				try {
					conn = JDBCUtil.createConnection(JDBCUtil.DBType.MYSQL, Config.DB_IP, Config.DB_PORT, Config.DB,
							Config.DB_USER, Config.DB_PWD);
					conn.setAutoCommit(true);
					// 三天前的数据
					long timeBefore = time.milliseconds() / 1000L - 259200L;

					PreparedStatement delete = conn
							.prepareStatement("DELETE FROM devicenumhistoryinfo where start_time<?");

					delete.setQueryTimeout(10);
					delete.setLong(1, timeBefore);
					delete.execute();
					delete.close();

					try {
						if (conn != null) {
							conn.close();
						}
					} catch (Exception e) {
					}

					rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Iterable<Record>>>>() {
						@Override
						public void call(Iterator<Tuple2<String, Iterable<Record>>> iterator) {
							Connection conn = null;
							PreparedStatement pst = null;
							try {
								conn = JDBCUtil.createConnection(JDBCUtil.DBType.MYSQL, Config.DB_IP, Config.DB_PORT,
										Config.DB, Config.DB_USER, Config.DB_PWD);
								conn.setAutoCommit(false);
								pst = conn.prepareStatement(DataAnalysis.HEAT_SQL);

								long count = 0L;
								while (iterator.hasNext()) {
									Tuple2<String, Iterable<Record>> tuple2 = iterator.next();
									String deviceNum = (String) tuple2._1;
									Iterator<Record> it = tuple2._2.iterator();
									long in = 0L;
									long out = 0L;
									long num = 0L;
									Record r = null;
									while (it.hasNext()) {
										r = (Record) it.next();
										if (StringUtils.isBlank(deviceNum)) {
											DataAnalysis.LOG.info("deviceNum is null,record:" + r);
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

									long createTime = r.getStartTime() - r.getStartTime() % Config.EXECUTE_INTERVAL
											+ Config.EXECUTE_INTERVAL;
									pst.setString(1, deviceNum);
									pst.setLong(2, createTime);
									pst.setLong(3, num);
									pst.setLong(4, num);
									pst.setLong(5, out);
									pst.addBatch();
									count += 1L;
									if (count % 3000L == 0L) {
										pst.executeBatch();
										conn.commit();
										pst.clearBatch();
									}
								}
								if (count % 3000L != 0L) {
									pst.executeBatch();
									conn.commit();
									pst.clearBatch();
								}
								return;
							} catch (SQLException e) {
								showLogError("Exception when handler database by jdbc...");
							} finally {
								try {
									if (!Objects.equals(pst, null)) {
										pst.close();
									}
									if (!Objects.equals(conn, null)) {
										conn.setAutoCommit(true);
										conn.close();
									}
								} catch (Exception e) {
								}
							}
						}
					});
				} catch (Exception e) {
					LOG.error("", e);
				} finally {
					try {
						if (conn != null)
							conn.close();
					} catch (Exception e) {
					}
				}
			}
		});

		// phone+mac 分组
		cacheRdd.mapToPair(new PairFunction<Tuple2<String, Record>, String, Record>() {
			@Override
			public Tuple2<String, Record> call(Tuple2<String, Record> tuple2) throws Exception {
				return new Tuple2<>(tuple2._2().getPhone() + "_" + tuple2._2().getMac(), tuple2._2());
			}
		}).groupByKey().foreachRDD(new VoidFunction2<JavaPairRDD<String, Iterable<Record>>, Time>() {
			@Override
			public void call(JavaPairRDD<String, Iterable<Record>> pairRDD, Time time) throws Exception {
				pairRDD.foreachPartition(new VoidFunction<Iterator<Tuple2<String, Iterable<Record>>>>() {
					@Override
					public void call(Iterator<Tuple2<String, Iterable<Record>>> tuple2Iterator) {
						Time myTime = time;
						Connection conn = null;
						PreparedStatement m_trackPST = null;
						PreparedStatement m_distributeQuery = null;
						ResultSet m_queryRS = null;
						PreparedStatement m_distributePST = null;
						PreparedStatement m_pass_service = null;

						PreparedStatement m_prewarn = null;
						Map<String, EquipLocation> m_equipments = null;
						Tuple2<Map<String, String>, Map<String, String>> info = null;

						Map<String, String> m_macID = null;

						Map<String, String> m_phoneId = null;

						List<Region> m_regions = null;

						EquipLocation equip = null;

						List<Circle> list = new ArrayList<>();
						try {
							conn = JDBCUtil.createConnection(JDBCUtil.DBType.MYSQL, Config.DB_IP, Config.DB_PORT,
									Config.DB, Config.DB_USER, Config.DB_PWD);
							conn.setAutoCommit(false);
							DataAnalysis.LOG.info("partition get jdbc connection:" + conn);
							m_equipments = JDBCUtil.getEquipsInfo(conn, "equipmentinfo");
							info = JDBCUtil.getFocusUserList(conn, "tbl_focus_person");
							m_macID = info._1;
							m_phoneId = info._2;

							if ((m_macID.size() == 0) && (m_phoneId.size() == 0)) {
								LOG.error("No focused persons,ignore this function and release jdbc connection");
								try {
									if (!Objects.equals(conn, null)) {
										m_equipments.clear();
										conn.setAutoCommit(true);
										conn.close();
										conn = null;
									}
								} catch (Exception e) {
									LOG.error("error", e);
								}
							} else {
								m_regions = JDBCUtil.getRegionInfo(conn, "carecontextinfo");

								m_trackPST = conn.prepareStatement(DataAnalysis.TRACK_SQL);
								m_distributeQuery = conn.prepareStatement(DataAnalysis.DISTRIBUTE_QUERY, 1005, 1008);
								m_distributePST = conn.prepareStatement(DataAnalysis.DISTRIBUTE_SQL);
								m_pass_service = conn.prepareStatement(DataAnalysis.PASS_SERVICE_SQL);
								m_prewarn = conn.prepareStatement(DataAnalysis.WARN_SQL);
								String person_id;
								Location personLoc;
								while (tuple2Iterator.hasNext()) {
									Tuple2<String, Iterable<Record>> tuple2 = tuple2Iterator.next();
									String phoneAndMac = (String) tuple2._1;
									String phone = phoneAndMac.split("[_]")[0];
									String mac = phoneAndMac.split("[_]")[1];
									JSONArray array = null;
									if ((m_macID.containsKey(mac)) || (m_phoneId.containsValue(phone))) {
										// String matched = null;
										if (m_macID.containsKey(mac)) {
											person_id = m_macID.get(mac);
											// matched = "mac:" + mac;
										} else {
											person_id = (String) m_phoneId.get(phone);
											// matched = "phone:" +
											// phone;
										}
										long lastestTime = 0L;
										EquipLocation lastestEquip = null;
										Iterator<Record> it = tuple2._2.iterator();
										array = new JSONArray();
										while (it.hasNext()) {
											Record r = (Record) it.next();
											int power = r.getPower();
											equip = (EquipLocation) m_equipments.get(r.getDeviceNum());
											if (!Objects.equals(equip, null)) {
												if ((r.getStartTime() >= lastestTime)
														&& (System.currentTimeMillis() / 1000L
																- r.getStartTime() < Config.DELAY_START_TIME_SECONDS)) {
													lastestTime = r.getStartTime();
													lastestEquip = equip;
												}
												Circle c = CalcCoord.toCircle(equip.getLongitude(), equip.getLatitude(),
														TransferUtil.getDistance(power));
												list.add(c);
												JSONObject obj = new JSONObject();

												obj.put("servicecode", r.getServiceCode());
												obj.put("devicenum", equip.getEquipNum());
												obj.put("longitude", Double.valueOf(equip.getLongitude()));
												obj.put("latitude", Double.valueOf(equip.getLatitude()));
												obj.put("time", Long.valueOf(r.getStartTime()));
												array.add(obj);

												m_pass_service.setLong(1, Long.parseLong(person_id));
												m_pass_service.setString(2, r.getServiceCode());
												m_pass_service.setTimestamp(3, new Timestamp(myTime.milliseconds()));
												m_pass_service.addBatch();
											}
										}
										if ((list.size() != 0) && (array.size() != 0)) {
											Location location = CalcCoord.toLocation(list);

											try {
												m_trackPST.setTimestamp(1, new Timestamp(myTime.milliseconds()));

												m_trackPST.setDouble(2, location.getLatitude());
												m_trackPST.setDouble(3, location.getLongitude());
												m_trackPST.setString(4, JSONArray.toJSONString(array));
												m_trackPST.setLong(5, StringUtils.equals(person_id, "") ? -1L
														: Long.parseLong(person_id));

												m_trackPST.addBatch();
											} catch (SQLException e) {
												for (StackTraceElement ste : e.getStackTrace()) {
													DataAnalysis.LOG.error(ste);
												}
											}
											m_distributeQuery.setString(1, person_id);
											m_queryRS = m_distributeQuery.executeQuery();
											if (m_queryRS.next()) {
												m_queryRS.updateTimestamp(2, new Timestamp(myTime.milliseconds()));
												m_queryRS.updateString(3,
														lastestEquip == null ? null : lastestEquip.getServiceCode());
												m_queryRS.updateString(4,
														lastestEquip == null ? null : lastestEquip.getEquipNum());
												m_queryRS.updateDouble(5,
														lastestEquip == null ? -1.0D : lastestEquip.getLatitude());
												m_queryRS.updateDouble(6,
														lastestEquip == null ? -1.0D : lastestEquip.getLongitude());
												m_queryRS.updateRow();
											} else {
												m_distributePST.setTimestamp(1, new Timestamp(myTime.milliseconds()));
												m_distributePST.setString(2,
														lastestEquip == null ? null : lastestEquip.getServiceCode());
												m_distributePST.setString(3,
														lastestEquip == null ? null : lastestEquip.getEquipNum());
												m_distributePST.setDouble(4,
														lastestEquip == null ? 0.0D : lastestEquip.getLatitude());
												m_distributePST.setDouble(5,
														lastestEquip == null ? 0.0D : lastestEquip.getLongitude());
												m_distributePST.setLong(6, Long.parseLong(person_id));

												m_distributePST.addBatch();
											}
											m_queryRS.close();

											personLoc = new Location(lastestEquip.getLongitude(),
													lastestEquip.getLatitude());
											for (Region region : m_regions) {
												if (region.containLocation(personLoc)) {
													m_prewarn.setString(1, UUID.randomUUID().toString());
													m_prewarn.setInt(2, region.getRegionId());
													m_prewarn.setInt(3, Integer.parseInt(person_id));
													m_prewarn.setDouble(4, personLoc.getLatitude());
													m_prewarn.setDouble(5, personLoc.getLongitude());
													m_prewarn.setTimestamp(6,
															new Timestamp(System.currentTimeMillis()));

													m_prewarn.addBatch();
												}
											}
										}
									}
								}
								m_pass_service.executeBatch();
								m_trackPST.executeBatch();
								m_distributePST.executeBatch();
								m_prewarn.executeBatch();
								conn.commit();
								m_trackPST.clearBatch();
								m_distributePST.clearBatch();
								m_pass_service.clearBatch();
								m_prewarn.clearBatch();
							}
							return;
						} catch (SQLException e) {
							LOG.error("error", e);
						} finally {
							try {
								if (!Objects.equals(m_distributeQuery, null)) {
									m_distributeQuery.close();
								}
								if (!Objects.equals(m_trackPST, null)) {
									m_trackPST.close();
								}
								if (!Objects.equals(m_distributePST, null)) {
									m_distributePST.close();
								}
								if (!Objects.equals(m_pass_service, null)) {
									m_pass_service.close();
								}
								if (!Objects.equals(conn, null)) {
									conn.setAutoCommit(true);
									conn.close();
								}
							} catch (Exception e) {
								LOG.error("Exception when release jdbc sessions....");
							}
						}
					}
				});
			}
		});
		showLogInfo("BATCH RDD EXECUTE END....");
	}

	private static void showLOG(Throwable e) {
		LOG.error(e);
		System.err.println(e);
		for (StackTraceElement ste : e.getStackTrace()) {
			LOG.error(ste);
			System.err.println(ste);
		}
	}

	private static void showLogInfo(String str) {
		LOG.info(str);
		System.out.println(str);
	}

	private static void showLogError(String str) {
		LOG.error(str);
		System.err.println(str);
	}
}