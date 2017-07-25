package com.sky.project.share.api.kafka.util;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;

/**
 * 1.首先，你必须知道读哪个topic的哪个partition <br>
 * 2.找到负责该partition的broker leader，从而找到存有该partition副本的那个broker<br>
 * 3.写request并fetch数据 <br>
 * 4.注意需要识别和处理broker leader的改变
 * 
 * @author zealot
 */
public class KafkaUtils {

	private static final int SO_TIMEOUT = 100 * 1000;
	private static final int BUFFER_SIZE = 64 * 1024;

	public static long offset(SimpleConsumer consumer, String topic, int partition, long whichTime, String clientName) {
		// requestInfo
		Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
		requestInfo.put(new TopicAndPartition(topic, partition), new PartitionOffsetRequestInfo(whichTime, 1));

		// get offset
		OffsetResponse response = consumer.getOffsetsBefore(
				new kafka.javaapi.OffsetRequest(requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName));

		return response.hasError() ? -1L : response.offsets(topic, partition)[0];
	}

	/**
	 * find leader by topic(the topic's all partition)
	 * 
	 * 获取指定broker中某个topic的Partition元数据
	 * 
	 * @param broker
	 *            broker host
	 * @param port
	 *            broker port
	 * @param topic
	 * @return key=topic's partitionId,value=PartitionMetadata
	 */
	public static TreeMap<Integer, PartitionMetadata> findLeader(final String broker, final int port,
			final String topic) {
		return findLeader(broker, port, topic, "findLeader" + new Date().getTime());
	}

	/**
	 * find leader by topic(the topic's all partition)
	 * 
	 * 获取指定broker中某个topic的Partition元数据
	 * 
	 * @param broker
	 *            broker host
	 * @param port
	 *            broker port
	 * @param topic
	 * @return key=topic's partitionId,value=PartitionMetadata
	 */
	private static TreeMap<Integer, PartitionMetadata> findLeader(final String broker, final int port,
			final String topic, final String group) {
		TreeMap<Integer, PartitionMetadata> map = new TreeMap<Integer, PartitionMetadata>();
		SimpleConsumer consumer = null;

		try {
			consumer = new SimpleConsumer(broker, port, SO_TIMEOUT, BUFFER_SIZE, group);
			TopicMetadataRequest request = new TopicMetadataRequest(Collections.singletonList(topic));

			// 发送 TopicMetadata Request请求
			kafka.javaapi.TopicMetadataResponse resp = consumer.send(request);

			List<TopicMetadata> metaDatas = resp.topicsMetadata(); // 获取Topic的Metadata

			for (TopicMetadata metadata : metaDatas) { // 遍历topic的MetaData
				for (PartitionMetadata part : metadata.partitionsMetadata()) { // 遍历每个partition的metaData
					map.put(part.partitionId(), part);
				}
			}
		} finally {
			if (consumer != null) {
				consumer.close();
			}
		}

		return map;
	}

	/**
	 * find PartitionMetadata by topic's partitionId
	 * 
	 * @param broker
	 * @param port
	 * @param topic
	 * @param partition
	 * @return the topic partitionId's PartitionMetadata, can't find the return
	 *         null
	 */
	public static PartitionMetadata findLeader(final String broker, final int port, final String topic,
			final int partition) {
		return findLeader(broker, port, topic).get(partition);
	}

	public static PartitionMetadata findLeader(final HostWithPort hostWithPort, final String topic,
			final int partition) {
		return findLeader(hostWithPort.getHost(), hostWithPort.getPort(), topic).get(partition);
	}

	/**
	 * 获取所有 broker 中指定topic的所有Partition的offset
	 * 
	 * @param topic
	 * @param brokers
	 * @param isLast
	 * @return key=host,value=[key=partition,value=offset]
	 */
	public static Map<String, Map<Integer, Long>> offset(final String topic, final String brokers,
			final boolean isLast) {
		Map<String, Map<Integer, Long>> results = new HashMap<>();
		String[] servers = brokers.split(",");

		// 遍历所有的 broker
		for (int i = 0, size = servers.length; i < size; i++) {
			String[] hostAndPort = servers[i].split(":");
			results.put(hostAndPort[0], offset(hostAndPort[0], Integer.parseInt(hostAndPort[1]), topic, isLast));
		}

		return results;
	}

	/**
	 * 获取指定broker中某个topic的Partition偏移
	 * 
	 * @param topic
	 * @param broker
	 * @param port
	 * @param isLast
	 * @return the offset map,key=partition,value=offset
	 */
	public static Map<Integer, Long> offset(final String broker, final int port, final String topic,
			final boolean isLast) {
		Map<Integer, Long> offsets = new HashMap<Integer, Long>();

		TreeMap<Integer, PartitionMetadata> metadatas = KafkaUtils.findLeader(broker, port, topic);

		for (Entry<Integer, PartitionMetadata> entry : metadatas.entrySet()) {
			int partition = entry.getKey();
			String leadBroker = entry.getValue().leader().host();

			String clientName = "Client_" + topic + "_" + partition + System.currentTimeMillis();
			SimpleConsumer consumer = null;

			try {
				consumer = new SimpleConsumer(leadBroker, port, SO_TIMEOUT, BUFFER_SIZE, clientName);

				long whichTime;
				if (isLast) {// 获取最新偏移
					whichTime = kafka.api.OffsetRequest.LatestTime();
				} else {// 获取最早偏移
					whichTime = kafka.api.OffsetRequest.EarliestTime();
				}

				// put data
				offsets.put(partition, offset(consumer, topic, partition, whichTime, clientName));
			} finally {
				if (consumer != null) {
					consumer.close();
				}
			}
		}

		return offsets;
	}

	public static HostWithPort[] parse(String brokers) {
		String[] hostWithPorts = brokers.split(",");
		int len = hostWithPorts.length;

		HostWithPort[] results = new HostWithPort[len];

		for (int i = 0; i < len; i++) {
			String[] hostWithPort = hostWithPorts[i].split(":");
			results[i] = new HostWithPort(hostWithPort[0], Integer.valueOf(hostWithPort[1]));
		}

		return results;
	}

	public static class HostWithPort {
		private final String host;
		private final int port;

		public HostWithPort(String host, int port) {
			super();
			this.host = host;
			this.port = port;
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}
	}
}
