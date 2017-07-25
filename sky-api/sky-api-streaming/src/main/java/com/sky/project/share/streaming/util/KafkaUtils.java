package com.sky.project.share.streaming.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;

/**
 * Kafka Utils
 * 
 * @author zealot
 *
 */
public final class KafkaUtils {

	private static Logger LOG = LoggerFactory.getLogger(KafkaUtils.class);

	public static long getOffset(SimpleConsumer consumer, String topic, int partition, long whichTime,
			String clientName) {
		TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
		Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
		requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
		kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo,
				kafka.api.OffsetRequest.CurrentVersion(), clientName);
		OffsetResponse response = consumer.getOffsetsBefore(request);

		if (response.hasError()) {
			LOG.error("Error fetching data Offset Data the Broker. Reason: {}", response.errorCode(topic, partition));
			return 0;
		}

		long[] offsets = response.offsets(topic, partition);
		return offsets[0];
	}

	public static TreeMap<Integer, PartitionMetadata> findLeader(String brokerHost, int a_port, String a_topic)
			throws Exception {
		TreeMap<Integer, PartitionMetadata> map = new TreeMap<Integer, PartitionMetadata>();
		SimpleConsumer consumer = null;
		try {
			consumer = new SimpleConsumer(brokerHost, a_port, 100000, 64 * 1024, "leaderLookup" + new Date().getTime());
			List<String> topics = Collections.singletonList(a_topic);
			TopicMetadataRequest req = new TopicMetadataRequest(topics);
			kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);

			List<TopicMetadata> metaData = resp.topicsMetadata();
			for (TopicMetadata item : metaData) {
				for (PartitionMetadata part : item.partitionsMetadata()) {
					map.put(part.partitionId(), part);
				}
			}
		} catch (Exception e) {
			throw new Exception(
					"Error communicating with Broker [" + brokerHost + "] to find Leader for [" + a_topic + ", ]", e);
		} finally {
			if (consumer != null) {
				consumer.close();
			}
		}
		return map;
	}

	/**
	 * 为了解决kafka.common.OffsetOutOfRangeException 当streaming
	 * zk里面记录kafka偏移小于kafka有效偏移，就会出现OffsetOutOfRangeException
	 * 
	 * @param topic
	 *            主题
	 * @param bootstrapServers
	 *            kafka配置{e.g rzx162:9092,rzx164:9092,rzx166:9092}
	 */
	public static Map<Integer, Long> getEarliestOffset(String topic, String bootstrapServers) throws Exception {
		String[] servers = bootstrapServers.split(",");
		List<String> kafkaHosts = new ArrayList<String>();
		List<Integer> kafkaPorts = new ArrayList<Integer>();

		for (int i = 0, size = servers.length; i < size; i++) {
			String[] hostAndPort = servers[i].split(":");
			try {
				String host = hostAndPort[0];
				Integer port = Integer.parseInt(hostAndPort[1]);
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

	/**
	 * 初始化到最新数据
	 * 
	 * @param topic
	 *            主题
	 * @param bootstrapServers
	 */
	public static Map<Integer, Long> getLastestOffset(String topic, String bootstrapServers) throws Exception {
		String[] servers = bootstrapServers.split(",");
		List<String> kafkaHosts = new ArrayList<String>();
		List<Integer> kafkaPorts = new ArrayList<Integer>();

		for (int i = 0, size = servers.length; i < size; i++) {
			String[] hostAndPort = servers[i].split(":");
			try {
				String host = hostAndPort[0];
				Integer port = Integer.parseInt(hostAndPort[1]);
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

	public static Map<Integer, Long> getOffset(String topic, String bootstrapServers, boolean isLast) throws Exception {
		String[] servers = bootstrapServers.split(",");
		List<String> kafkaHosts = new ArrayList<String>();
		List<Integer> kafkaPorts = new ArrayList<Integer>();

		for (int i = 0, size = servers.length; i < size; i++) {
			String[] hostAndPort = servers[i].split(":");
			try {
				String host = hostAndPort[0];
				Integer port = Integer.parseInt(hostAndPort[1]);
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

	private static Map<Integer, Long> getOffset(String topic, List<String> kafkaHosts, List<Integer> kafkaPorts,
			boolean isLast) throws Exception {
		Map<Integer, Long> partionAndOffset = null;
		for (int i = 0, size = kafkaHosts.size(); i < size; i++) {
			String host = kafkaHosts.get(i);
			int port = kafkaPorts.get(i);
			try {
				partionAndOffset = getOffset(topic, host, port, isLast);
			} catch (Exception e) {
				throw new Exception("topic(" + topic + "), kafkaHost(" + host + "), kafkaPort(" + port
						+ "), Kafka getOffset error! isLast: " + isLast, e);
			}

			if (partionAndOffset.size() > 0) {
				break;
			} else {
				continue;
			}
		}
		return partionAndOffset;
	}

	private static Map<Integer, Long> getOffset(String topic, String kafkaHost, int kafkaPort, boolean isLast)
			throws Exception {
		Map<Integer, Long> partionAndOffset = new HashMap<Integer, Long>();
		TreeMap<Integer, PartitionMetadata> metadatas = null;
		try {
			metadatas = KafkaUtils.findLeader(kafkaHost, kafkaPort, topic);
		} catch (Exception e) {
			throw new Exception("topic(" + topic + "), kafkaHost(" + kafkaHost + "), kafkaPort(" + kafkaPort
					+ "), Kafka findLeader error! isLast: " + isLast, e);
		}

		for (Entry<Integer, PartitionMetadata> entry : metadatas.entrySet()) {
			int partition = entry.getKey();
			String leadBroker = entry.getValue().leader().host();
			String clientName = "Client_" + topic + "_" + partition;
			SimpleConsumer consumer = null;
			try {
				consumer = new SimpleConsumer(leadBroker, kafkaPort, 100000, 64 * 1024, clientName);
				long offset = -1;
				if (isLast) {
					// 获取最新偏移
					offset = KafkaUtils.getOffset(consumer, topic, partition, kafka.api.OffsetRequest.LatestTime(),
							clientName);
				} else {
					// 获取最早偏移
					offset = KafkaUtils.getOffset(consumer, topic, partition, kafka.api.OffsetRequest.EarliestTime(),
							clientName);
				}
				partionAndOffset.put(partition, offset);
			} catch (Exception e) {
				throw new Exception("topic(" + topic + "), kafkaHost(" + kafkaHost + "), kafkaPort(" + kafkaPort
						+ "), Kafka fetch getOffset error! isLast: " + isLast, e);
			} finally {
				if (consumer != null) {
					consumer.close();
				}
			}
		}
		return partionAndOffset;
	}

	private KafkaUtils() {
	}
}
