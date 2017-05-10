package com.sky.projects.analysis.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.sky.projects.analysis.config.Config;
import com.sky.projects.analysis.entity.Record;
import com.sky.projects.analysis.util.JDBCUtil;

import kafka.serializer.StringDecoder;
import scala.Tuple2;

@SuppressWarnings("serial")
public class FlowRouteStatistics implements Config {
	private static final Log LOG = LogFactory.getLog(FlowRouteStatistics.class);
	private static final double RATIO = Config.MIGRATED_DESC / 100.0D;

	public static void main(String[] args) {
		int interval = EXECUTE_INTERVAL;
		SparkConf sc = new SparkConf().setAppName("streaming").setMaster(SPARK_URL);
		JavaStreamingContext jsc = new JavaStreamingContext(sc, Durations.seconds(interval));
		Map<String, Integer> topicParas = new HashMap<>();
		topicParas.put(TOPICS, Integer.valueOf(1));
		HashMap<String, String> params = new HashMap<>();
		params.put("zookeeper.connect", ZOOKEEPER_ADDRESS);
		params.put("group.id", KAFKA_GROUP_HEATANDWATCH);
		params.put("metadata.broker.list", KAFKA_BROKERS);
		JavaPairReceiverInputDStream<String, String> inputStream = KafkaUtils.createStream(jsc, String.class,
				String.class, StringDecoder.class, StringDecoder.class, params, topicParas,
				StorageLevel.MEMORY_ONLY_SER_2());

		JavaPairDStream<String, String> origin = inputStream.window(Durations.seconds(2 * interval),
				Durations.seconds(interval));

		execute(origin, EXECUTE_INTERVAL);

		jsc.start();
		jsc.awaitTermination();
	}

	public static JavaPairDStream<String, Long> execute(JavaPairDStream<String, String> windowStream, int interval) {
		// 对数据进行展开,
		JavaPairDStream<String, Record> recordStream = windowStream
				.transformToPair(new Function<JavaPairRDD<String, String>, JavaPairRDD<String, Record>>() {
					@Override
					public JavaPairRDD<String, Record> call(JavaPairRDD<String, String> info) throws Exception {
						return info.mapToPair(new PairFunction<Tuple2<String, String>, String, Record>() {
							@Override
							public Tuple2<String, Record> call(Tuple2<String, String> each) throws Exception {
								String[] data = each._2.split("\\|");
								String mac = data[0];
								int type = Integer.parseInt(data[1].equals("MULL") ? "2" : data[1]);
								long startTime = (data[2].equals("MULL") ? System.currentTimeMillis()
										: Long.parseLong(data[2]));
								long endTime = Long.parseLong(data[3].equals("MULL") ? "0" : data[3]);
								int power = Integer.parseInt(data[4].equals("MULL") ? "0" : data[4]);
								String deviceNum = data[17];
								String serviceCode = data[18];

								return new Tuple2<>(mac,
										new Record(deviceNum, serviceCode, type, mac, null, power, startTime, endTime));
							}
						});
					}
				});

		// join the stream,保留场所编码相同(_1=_2)&startTime(_1<=_2)&endTime(_1=0)
		JavaPairDStream<String, Tuple2<Record, Record>> joinStream = recordStream.join(recordStream);

		// 过滤数据
		JavaPairDStream<String, Tuple2<Record, Record>> filterEachFromTo = joinStream
				.filter(new Function<Tuple2<String, Tuple2<Record, Record>>, Boolean>() {
					@Override
					public Boolean call(Tuple2<String, Tuple2<Record, Record>> to) throws Exception {
						// 保留场所编码相同(_1=_2)&startTime(_1<=_2)&_1.endTime=0
						return !(to._2._1.getServiceCode().equals(to._2._2.getServiceCode())
								&& to._2._1.getStartTime() <= to._2._2.getStartTime() && to._2._1.getEndTime() == 0L);
					}
				});

		filterEachFromTo.cache();

		JavaPairDStream<String, Tuple2<Record, Record>> eachFromTo;
		// 使用 groupByKey 获取最大的信号强度
		if (isDistinctByEach.equals("yes")) {
			eachFromTo = filterEachFromTo.groupByKey(5).transformToPair(
					new Function<JavaPairRDD<String, Iterable<Tuple2<Record, Record>>>, JavaPairRDD<String, Tuple2<Record, Record>>>() {
						@Override
						public JavaPairRDD<String, Tuple2<Record, Record>> call(
								JavaPairRDD<String, Iterable<Tuple2<Record, Record>>> info) throws Exception {
							return info.mapToPair(
									new PairFunction<Tuple2<String, Iterable<Tuple2<Record, Record>>>, String, Tuple2<Record, Record>>() {
										@Override
										public Tuple2<String, Tuple2<Record, Record>> call(
												Tuple2<String, Iterable<Tuple2<Record, Record>>> pair)
												throws Exception {
											Iterable<Tuple2<Record, Record>> ls = pair._2;
											double max = -10000.0D;
											Tuple2<Record, Record> finalRet = null;

											for (Tuple2<Record, Record> tuple2 : ls) {
												double power = tuple2._1.getPower();

												if (power > max) {
													max = power;
													finalRet = tuple2;
												}
											}

											return new Tuple2<>(pair._1, finalRet);
										}
									});
						}
					});
		} else {
			eachFromTo = filterEachFromTo;
		}

		JavaPairDStream<String, String> fromTo = eachFromTo.transformToPair(
				new Function<JavaPairRDD<String, Tuple2<Record, Record>>, JavaPairRDD<String, String>>() {
					public JavaPairRDD<String, String> call(JavaPairRDD<String, Tuple2<Record, Record>> data) {
						try {
							data.mapToPair(new PairFunction<Tuple2<String, Tuple2<Record, Record>>, String, String>() {
								public Tuple2<String, String> call(Tuple2<String, Tuple2<Record, Record>> each) {
									if (each._2() == null) {
										return new Tuple2<>("test-test", "test");
									}
									Tuple2<String, String> t = null;
									try {
										t = new Tuple2<>(
												each._2._1.getServiceCode() + "-" + each._2._2.getServiceCode(),
												each._1);
									} catch (Exception e) {
										LOG.error(e);
										LOG.info("each.1:" + each._1);
										LOG.info("each.2.1:" + each._2._1);
										LOG.info("each.2.1:" + each._2._2);
									}
									return t;
								}
							});
						} catch (Exception e) {
							LOG.info(e);
							e.printStackTrace();
							for (StackTraceElement ste : e.getStackTrace()) {
								LOG.info(ste);
								System.out.println(ste);
							}
						}
						return null;
					}
				});

		JavaPairDStream<String, Long> fromToCount = fromTo.filter(new Function<Tuple2<String, String>, Boolean>() {
			public Boolean call(Tuple2<String, String> stringStringTuple2) throws Exception {
				return Boolean.valueOf(!((String) stringStringTuple2._1()).equalsIgnoreCase("test-test"));
			}
		}).groupByKey().mapToPair(new PairFunction<Tuple2<String, Iterable<String>>, String, Long>() {
			public Tuple2<String, Long> call(Tuple2<String, Iterable<String>> data) throws Exception {
				Iterable<String> tmp = data._2;
				long count = 0L;
				for (String str : tmp) {
					count += 1L;
				}
				return new Tuple2<>(data._1, Long.valueOf(count));
			}
		});
		fromToCount.foreachRDD(new VoidFunction<JavaPairRDD<String, Long>>() {
			@Override
			public void call(JavaPairRDD<String, Long> rdd) throws Exception {
				long now = System.currentTimeMillis();
				long timeBefore = now / 1000L - 259200L;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
				String time = sdf.format(Long.valueOf(timeBefore));
				LOG.info(String.format("execute SQL:[%s]",
						new Object[] { "DELETE FROM　tbl_migrated　WHERE UNIX_TIMESTAMP(insert_time) <" + timeBefore }));
				Connection conn = null;
				conn = JDBCUtil.createConnection(JDBCUtil.DBType.MYSQL, Config.DB_IP, Config.DB_PORT, Config.DB,
						Config.DB_USER, Config.DB_PWD);
				conn.setAutoCommit(true);
				PreparedStatement deletePST = conn
						.prepareStatement("DELETE FROM tbl_migrated WHERE UNIX_TIMESTAMP(insert_time) <?", 1008, 1005);
				deletePST.setLong(1, timeBefore);
				deletePST.execute();
				if (!Objects.equals(deletePST, null)) {
					deletePST.close();
				}
				if (!Objects.equals(conn, null)) {
					conn.close();
				}
				LOG.info("delete from tbl_migrated of this RDD end...");
				if (rdd.count() == 0L) {
					LOG.info("RDD Count ==0,return,...");
					return;
				}
				rdd.mapToPair(new PairFunction<Tuple2<String, Long>, Long, String>() {
					public Tuple2<Long, String> call(Tuple2<String, Long> r) throws Exception {
						return new Tuple2<>(r._2, r._1);
					}
				}).sortByKey(true).mapToPair(new PairFunction<Tuple2<Long, String>, String, Long>() {
					public Tuple2<String, Long> call(Tuple2<Long, String> r) throws Exception {
						return new Tuple2<>(r._2, r._1);
					}
				}).foreachPartition(new VoidFunction<Iterator<Tuple2<String, Long>>>() {
					public void call(Iterator<Tuple2<String, Long>> tuple2Iterator) throws Exception {
						List<Tuple2<String, Long>> act = new ArrayList<>();

						int pointer = 0;

						String sql = "insert into tbl_migrated (insert_time,source_service_code,distination_service_code,number) values(?,?,?,?)";
						PreparedStatement ps = null;
						Connection conn = null;
						while (tuple2Iterator.hasNext()) {
							pointer++;
							act.add(tuple2Iterator.next());
						}
						LOG.info("in fact:" + pointer);
						pointer = (int) (pointer * FlowRouteStatistics.RATIO);
						LOG.info("desc:" + pointer);
						conn = JDBCUtil.createConnection(JDBCUtil.DBType.MYSQL, Config.DB_IP, Config.DB_PORT, Config.DB,
								Config.DB_USER, Config.DB_PWD);
						conn.setAutoCommit(false);
						ps = conn.prepareStatement(sql);

						LOG.info("new connection:" + conn);
						LOG.info("flow route statistics statement:" + ps);
						long now = System.currentTimeMillis();
						// Timestamp t = new Timestamp(now - now %
						// (flowRouteStatistics.13.this.val$interval * 1000));
						Timestamp t = null;

						int count = 0;
						for (int i = 0; i < pointer; i++) {
							Tuple2<String, Long> tuple2 = act.get(i);
							String fromTo = tuple2._1;
							long number = tuple2._2.longValue();
							String[] sp = fromTo.split("-");

							ps.setTimestamp(1, t);
							ps.setString(2, sp[0]);
							ps.setString(3, sp[1]);
							ps.setInt(4, (int) number);
							ps.addBatch();
							count++;
							if (count % 2000 == 0) {
								ps.executeBatch();
								conn.commit();
								ps.clearBatch();
							}
						}
						ps.executeBatch();
						conn.commit();
						ps.clearBatch();
						ps.close();
						conn.close();
						LOG.info("migrate,partition,insert into migrated number:" + count);
					}
				});
			}
		});
		return fromToCount;
	}
}