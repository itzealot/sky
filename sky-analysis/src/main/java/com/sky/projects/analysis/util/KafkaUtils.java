package com.sky.projects.analysis.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaUtils {
	private static Logger LOG = LoggerFactory.getLogger(KafkaUtils.class);

	public static long getOffset(SimpleConsumer consumer, String topic, int partition, long whichTime,
			String clientName) {
		TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);

		Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<>();
		requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));

		kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo,
				kafka.api.OffsetRequest.CurrentVersion(), clientName);

		OffsetResponse response = consumer.getOffsetsBefore(request);
		if (response.hasError()) {
			LOG.error("Error fetching data Offset Data the Broker. Reason: {}",
					Short.valueOf(response.errorCode(topic, partition)));
			return 0L;
		}
		long[] offsets = response.offsets(topic, partition);
		return offsets[0];
	}

	public static TreeMap<Integer, PartitionMetadata> findLeader(String brokerHost, int a_port, String a_topic)
			throws Exception {
		TreeMap<Integer, PartitionMetadata> map = new TreeMap<>();
		SimpleConsumer consumer = null;
		try {
			consumer = new SimpleConsumer(brokerHost, a_port, 100000, 65536, "leaderLookup" + new Date().getTime());

			List<String> topics = Collections.singletonList(a_topic);
			TopicMetadataRequest req = new TopicMetadataRequest(topics);
			TopicMetadataResponse resp = consumer.send(req);

			List<TopicMetadata> metaData = resp.topicsMetadata();
			for (TopicMetadata item : metaData) {
				for (PartitionMetadata part : item.partitionsMetadata()) {
					map.put(Integer.valueOf(part.partitionId()), part);
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

	private static Map<Integer, Long> getOffset(String topic, String kafkaHost, int kafkaPort, boolean isLast)
			throws Exception {
		Map<Integer, Long> partionAndOffset = new HashMap<>();
		TreeMap<Integer, PartitionMetadata> metadatas = null;
		try {
			metadatas = findLeader(kafkaHost, kafkaPort, topic);
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
					offset = getOffset(consumer, topic, partition, kafka.api.OffsetRequest.LatestTime(), clientName);
				} else {
					offset = getOffset(consumer, topic, partition, kafka.api.OffsetRequest.EarliestTime(), clientName);
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