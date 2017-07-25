package com.sky.project.share.streaming.main;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Seconds;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.project.share.streaming.StreamProcessDemo;
import com.sky.project.share.streaming.util.KafkaUtils;
import com.sky.project.share.streaming.util.SparkConfUtil;

/**
 * 提交Streaming job
 * 
 * @author zealot
 *
 */
public class SparkJobMain {

	private final static Logger LOG = LoggerFactory.getLogger(SparkJobMain.class);

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < args.length; i++) {
			LOG.info("args[{}] = {}", i, args[i]);
		}

		// yarn mode
		String yarnMode = args[0];

		// kafka brokers url
		String brokers = args[1];

		// zk url
		String zkUrl = args[2];

		// topic
		String topic = args[3];
		String group = args[4];
		String kafkaMode = args[5];
		String maxRatePerPartition = args[6];
		String jobDuration = args[6];

		// 名称
		SparkConf conf = new SparkConf();
		initSparkConf(conf, yarnMode, maxRatePerPartition);

		JavaStreamingContext jssc = null;
		try {
			JavaSparkContext jsc = new JavaSparkContext(conf);

			// 广播变量
			Broadcast<String> bZkConn = jsc.broadcast(zkUrl);
			Broadcast<String> bGroupId = jsc.broadcast(group);

			Map<String, Broadcast<String>> broadcastMap = new HashMap<String, Broadcast<String>>();
			broadcastMap.put("ga.zkUrl", bZkConn);
			broadcastMap.put("ga.topic.group", bGroupId);

			// 一个流式处理类一个 JavaStreamingContext
			jssc = new JavaStreamingContext(jsc, Seconds.apply(Long.parseLong(jobDuration)));

			Map<String, String> kafkaParams = initKafka(brokers, zkUrl, group);

			StreamProcessDemo demo = new StreamProcessDemo(conf, broadcastMap);

			Map<Integer, Long> partionAndOffset = null;
			try {
				partionAndOffset = getOffset(kafkaMode, topic, brokers);
			} catch (Exception e) {
				LOG.error(String.format("fetch kafka offset error, topic:{}", topic), e);
			}

			demo.deal(jssc, kafkaParams, topic, partionAndOffset);

			jssc.start();
			jssc.awaitTermination();
		} catch (Exception e) {
			LOG.error("start StreamProcessDemo fail.", e);
		} finally {
			if (jssc != null) {
				jssc.close();
			}
		}
	}

	private static void initSparkConf(SparkConf conf, String yarnMode, String maxRatePerPartition) {
		// webUI 上的job名称
		conf.setAppName("streaming-test").setMaster(yarnMode);

		conf.set("spark.streaming.concurrentJobs", "60");

		// 解决 executor 在运行过程中某些 executor 会挂掉问题
		conf.set("spark.yarn.executor.memoryOverhead", "1024");

		// 关闭反压制特性
		conf.set("spark.streaming.backpressure.enabled", "false");

		// 推测执行
		conf.set("spark.speculation", "true");

		/** 序列化 */
		conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
		conf.set("spark.kryo.registrator", "com.ga.projects.spark.main.StreamRegistrator");

		conf.set("spark.streaming.kafka.maxRatePerPartition", maxRatePerPartition);

		/** 使用 UnifiedMemoryManager 管理内存 */
		SparkConfUtil.usingUnifiedMemoryManager(conf, "0.75", "0.5");

		/** shuffle 优化 */
		SparkConfUtil.shuffleOpt(conf);
	}

	private static Map<Integer, Long> getOffset(String kafkaMode, String topic, String bootstrapServers)
			throws Exception {
		Map<Integer, Long> wlPartionAndOffset = null;
		try {
			if (kafkaMode.equalsIgnoreCase("last")) {
				wlPartionAndOffset = KafkaUtils.getLastestOffset(topic, bootstrapServers);
			} else if (kafkaMode.equalsIgnoreCase("early")) {
				wlPartionAndOffset = KafkaUtils.getEarliestOffset(topic, bootstrapServers);
			} else {
				throw new Exception("kafkaMode config error, must be (last or early)");
			}
		} catch (Exception e) {
			LOG.error("fetch Kafka offset fail.", e);
			throw new Exception("Fetch earliestOffset error", e);
		}
		return wlPartionAndOffset;
	}

	private static Map<String, String> initKafka(String brokers, String zkUrl, String group) {
		Map<String, String> kafkaParams = new HashMap<String, String>();

		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("zookeeper.connect", zkUrl);
		kafkaParams.put("zookeeper.connection.timeout.ms", "10000");
		kafkaParams.put("zookeeper.session.timeout.ms", "6000");
		kafkaParams.put("zookeeper.sync.time.ms", "5000");
		kafkaParams.put("group.id", group);
		kafkaParams.put("auto.offset.reset", "smallest");
		kafkaParams.put("refresh.leader.backoff.ms", "1000");
		kafkaParams.put("auto.commit.interval.ms", "5000");
		kafkaParams.put("fetch.message.max.bytes", "41943040");

		return kafkaParams;
	}

}
