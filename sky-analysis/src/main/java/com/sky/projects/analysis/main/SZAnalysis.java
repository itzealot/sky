package com.sky.projects.analysis.main;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.I0Itec.zkclient.ZkClient;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.streaming.Seconds;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.analysis.config.Config;
import com.sky.projects.analysis.config.DBConfig;
import com.sky.projects.analysis.service.SZDataAnalysis;
import com.sky.projects.analysis.util.KafkaUtils;
import com.sky.projects.analysis.util.ZkUtils;

import kafka.api.OffsetRequest;
import kafka.common.TopicAndPartition;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import scala.Tuple2;

@SuppressWarnings("serial")
public class SZAnalysis implements IMain, Config, DBConfig, Serializable {
	private static Logger LOG = LoggerFactory.getLogger(SZAnalysis.class);
	private static String zkConn;
	private static String groupId;

	public static void main(String[] args) {
		new SZAnalysis().execute(args);
	}

	public void execute(String[] args) {
		SparkConf m_conf = new SparkConf();
		m_conf.setMaster(SPARK_URL).setAppName("HeatChatAndUserTrackAnalysis");
		m_conf.set("spark.streaming.unpersist", "true");
		m_conf.set("spark.shuffle.blockTransferService", SHUFFLE_TYPE);
		JavaStreamingContext m_streamContext = new JavaStreamingContext(m_conf, Seconds.apply(EXECUTE_INTERVAL));
		m_streamContext.checkpoint("./scistor-streaming-checkpoint");
		LOG.info("sparkconf:" + m_conf);
		LOG.info("sparkStreamingContext:" + m_streamContext);

		zkConn = ZOOKEEPER_ADDRESS;
		groupId = KAFKA_GROUP_HEATANDWATCH;
		Map<String, String> m_kafkaparam = new HashMap<>();
		Map<String, Integer> m_topics = new HashMap<>();
		m_kafkaparam.put("zookeeper.connect", ZOOKEEPER_ADDRESS);
		m_kafkaparam.put("group.id", KAFKA_GROUP_HEATANDWATCH);
		m_kafkaparam.put("metadata.broker.list", KAFKA_BROKERS);
		m_kafkaparam.put("broker.list", KAFKA_BROKERS);
		for (String topic : TOPICS.split("[,]")) {
			m_topics.put(topic, Integer.valueOf(5));
		}
		LOG.info("kafkaparma:" + m_kafkaparam);
		Map<Integer, Long> wlPartionAndOffset = null;
		try {
			wlPartionAndOffset = getOffset("last", Config.TOPICS, KAFKA_BROKERS);
		} catch (Exception e) {
			LOG.error("Fetch Wl kafka offset error", e);
		}
		LOG.info("topic,wl OFFSET:" + wlPartionAndOffset);
		JavaDStream<String> wlDealStream = kafkaLoad(m_streamContext, m_kafkaparam, m_topics, null, wlPartionAndOffset);
		JavaPairDStream<String, String> pairsDStream = wlDealStream
				.flatMapToPair(new PairFlatMapFunction<String, String, String>() {
					public Iterable<Tuple2<String, String>> call(String s) throws Exception {
						List<Tuple2<String, String>> list = new ArrayList<>();
						for (String str : s.split("\002")) {
							list.add(new Tuple2<>("", str));
						}
						return list;
					}
				});
		SZDataAnalysis.execute(pairsDStream);
		LOG.info("streaming logic is ready to submit to DAGScheduler and TaskScheduler...");
		m_streamContext.start();
		LOG.info("streaming context has started,await for termination....");
		m_streamContext.awaitTermination();
		m_streamContext.stop();
	}

	private static Map<Integer, Long> getOffset(String kafkaMode, String topic, String bootstrapServers)
			throws Exception {
		Map<Integer, Long> wlPartionAndOffset = null;
		try {
			if (kafkaMode.equalsIgnoreCase("last")) {
				wlPartionAndOffset = getLastestOffset(topic, bootstrapServers);
			} else if (kafkaMode.equalsIgnoreCase("early")) {
				wlPartionAndOffset = getEarliestOffset(topic, bootstrapServers);
			} else {
				throw new Exception("kafkaMode config error, must be (last or early)");
			}
		} catch (Exception e) {
			LOG.error("获取 kafka 的消费偏移失败!", e);
			throw new Exception("Fetch earliestOffset error", e);
		}
		return wlPartionAndOffset;
	}

	public static Map<TopicAndPartition, Long> topicOffsetToMap(String zkConn, String groupId, String topic,
			Map<Integer, Long> partionAndOffset) {
		ZkClient zkClient = null;
		Map<TopicAndPartition, Long> topicMap = new HashMap<>();
		try {
			zkClient = new ZkClient(zkConn, 10000, 10000, new ZkUtils.StringSerializer());

			List<String> brokerPartitions = ZkUtils.getBrokerPartitions(zkClient, topic);
			for (String brokerPartition : brokerPartitions) {
				String[] brokerPartitionParts = brokerPartition.split("-");

				String partitionId = brokerPartitionParts[1];
				int partition = Integer.parseInt(partitionId);

				long lastConsumedOffset = ZkUtils.getLastConsumedOffset(zkClient, groupId, topic, partitionId);
				if (partionAndOffset.containsKey(Integer.valueOf(partition))) {
					long kafkaOffset = ((Long) partionAndOffset.get(Integer.valueOf(partition))).longValue();
					if (kafkaOffset > lastConsumedOffset) {
						lastConsumedOffset = kafkaOffset;
					}
				}
				topicMap.put(new TopicAndPartition(topic, partition), Long.valueOf(lastConsumedOffset));
				LOG.info("topic({}), parititon({}), offset is {}.",
						new Object[] { topic, Integer.valueOf(partition), Long.valueOf(lastConsumedOffset) });
			}
		} catch (Exception e) {
			LOG.error("topicOffsetToMap error!", e);
		} finally {
			try {
				ZkUtils.close(zkClient);
			} catch (IOException e) {
				LOG.error("close zk connection error !", e);
			}
		}
		return topicMap;
	}

	private static JavaDStream<String> kafkaLoad(JavaStreamingContext jssc, Map<String, String> kafkaParams,
			Map<String, Integer> topicMap, boolean[] filterArr, Map<Integer, Long> partionAndOffset) {
		String wlTopic = null;
		for (String topic : topicMap.keySet()) {
			wlTopic = topic;
			LOG.info(" each topicS:" + topicMap);
		}
		LOG.error("topicOFFSETTOMAP:" + topicOffsetToMap(zkConn, groupId, wlTopic, partionAndOffset));
		JavaDStream<String> w01SourceStream = org.apache.spark.streaming.kafka.KafkaUtils.createDirectStream(jssc,
				String.class, String.class, StringDecoder.class, StringDecoder.class, String.class, kafkaParams,
				topicOffsetToMap(zkConn, groupId, wlTopic, partionAndOffset),
				new Function<MessageAndMetadata<String, String>, String>() {
					public String call(MessageAndMetadata<String, String> msgAndMd) throws Exception {
						return (String) msgAndMd.message();
					}
				});
		LOG.info("w01sourceS:" + w01SourceStream);

		return w01SourceStream;
	}

	public static Map<Integer, Long> getEarliestOffset(String topic, String bootstrapServers) throws Exception {
		String[] servers = bootstrapServers.split(",");
		List<String> kafkaHosts = new ArrayList<>();
		List<Integer> kafkaPorts = new ArrayList<>();
		int i = 0;
		for (int size = servers.length; i < size; i++) {
			String[] hostAndPort = servers[i].split(":");
			try {
				String host = hostAndPort[0];
				Integer port = Integer.valueOf(Integer.parseInt(hostAndPort[1]));
				kafkaHosts.add(host);
				kafkaPorts.add(port);
			} catch (Exception e) {
			}
		}
		if (kafkaHosts.size() < 1) {
			throw new Exception("parse bootstrapServers error!");
		}
		Map<Integer, Long> partionAndOffset = getOffset(topic, kafkaHosts, kafkaPorts, false);
		return partionAndOffset;
	}

	public static Map<Integer, Long> getLastestOffset(String topic, String bootstrapServers) throws Exception {
		String[] servers = bootstrapServers.split(",");
		List<String> kafkaHosts = new ArrayList<>();
		List<Integer> kafkaPorts = new ArrayList<>();
		int i = 0;
		for (int size = servers.length; i < size; i++) {
			String[] hostAndPort = servers[i].split(":");
			try {
				String host = hostAndPort[0];
				Integer port = Integer.valueOf(Integer.parseInt(hostAndPort[1]));
				kafkaHosts.add(host);
				kafkaPorts.add(port);
			} catch (Exception e) {
			}
		}
		if (kafkaHosts.size() < 1) {
			throw new Exception("parse bootstrapServers error!");
		}
		Map<Integer, Long> partionAndOffset = getOffset(topic, kafkaHosts, kafkaPorts, true);
		return partionAndOffset;
	}

	private static Map<Integer, Long> getOffset(String topic, List<String> kafkaHosts, List<Integer> kafkaPorts,
			boolean isLast) throws Exception {
		Map<Integer, Long> partionAndOffset = null;
		int i = 0;
		for (int size = kafkaHosts.size(); i < size; i++) {
			String host = (String) kafkaHosts.get(i);
			int port = ((Integer) kafkaPorts.get(i)).intValue();
			try {
				partionAndOffset = getOffset(topic, host, port, isLast);
			} catch (Exception e) {
				throw new Exception("topic(" + topic + "),kafkaHost(" + host + "),kafkaPort(" + port
						+ "), Kafka getEarliestOffset error!", e);
			}
			if (partionAndOffset.size() > 0) {
				break;
			}
		}
		return partionAndOffset;
	}

	public static Map<Integer, Long> getOffset(String topic, String bootstrapServers, boolean isLast) throws Exception {
		String[] servers = bootstrapServers.split(",");
		List<String> kafkaHosts = new ArrayList<>();
		List<Integer> kafkaPorts = new ArrayList<>();
		int i = 0;
		for (int size = servers.length; i < size; i++) {
			String[] hostAndPort = servers[i].split(":");
			try {
				String host = hostAndPort[0];
				Integer port = Integer.valueOf(Integer.parseInt(hostAndPort[1]));
				kafkaHosts.add(host);
				kafkaPorts.add(port);
			} catch (Exception e) {
			}
		}
		if (kafkaHosts.size() < 1) {
			throw new Exception("parse bootstrapServers error!");
		}
		Map<Integer, Long> partionAndOffset = getOffset(topic, kafkaHosts, kafkaPorts, isLast);
		return partionAndOffset;
	}

	private static Map<Integer, Long> getOffset(String topic, String kafkaHost, int kafkaPort, boolean isLast)
			throws Exception {
		Map<Integer, Long> partionAndOffset = new HashMap<>();
		TreeMap<Integer, PartitionMetadata> metadatas = null;
		try {
			metadatas = KafkaUtils.findLeader(kafkaHost, kafkaPort, topic);
		} catch (Exception e) {
			throw new Exception("topic(" + topic + "),kafkaHost(" + kafkaHost + "),kafkaPort(" + kafkaPort
					+ "), Kafka findLeader error!", e);
		}
		for (Map.Entry<Integer, PartitionMetadata> entry : metadatas.entrySet()) {
			int partition = ((Integer) entry.getKey()).intValue();
			String leadBroker = ((PartitionMetadata) entry.getValue()).leader().host();
			String clientName = "Client_" + topic + "_" + partition;
			SimpleConsumer consumer = null;
			try {
				consumer = new SimpleConsumer(leadBroker, kafkaPort, 100000, 65536, clientName);

				long offset = -1L;
				if (isLast) {
					offset = KafkaUtils.getOffset(consumer, topic, partition, OffsetRequest.LatestTime(), clientName);
				} else {
					offset = KafkaUtils.getOffset(consumer, topic, partition, OffsetRequest.EarliestTime(), clientName);
				}
				partionAndOffset.put(Integer.valueOf(partition), Long.valueOf(offset));
			} catch (Exception e) {
				throw new Exception("topic(" + topic + "),kafkaHost(" + kafkaHost + "),kafkaPort(" + kafkaPort
						+ "), Kafka fetch earliestOffset error!", e);
			} finally {
				if (consumer != null) {
					consumer.close();
				}
			}
		}
		return partionAndOffset;
	}
}