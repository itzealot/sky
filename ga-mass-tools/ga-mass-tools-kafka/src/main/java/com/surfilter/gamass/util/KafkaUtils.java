package com.surfilter.gamass.util;

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
 * 首先，你必须知道读哪个topic的哪个partition <br />
 * 然后，找到负责该partition的broker leader，从而找到存有该partition副本的那个broker<br />
 * 再者，自己去写request并fetch数据 <br />
 * 最终，还要注意需要识别和处理broker leader的改变
 * 
 * @author zealot
 *
 */
public class KafkaUtils {

	public static long getOffset(SimpleConsumer consumer, String topic, int partition, long whichTime,
			String clientName) {
		TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
		Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
		requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));

		kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo,
				kafka.api.OffsetRequest.CurrentVersion(), clientName);

		OffsetResponse response = consumer.getOffsetsBefore(request);

		return response.hasError() ? -1l : response.offsets(topic, partition)[0];
	}

	/**
	 * Finding the Lead Broker for a Topic and Partition
	 * 
	 * 思路:遍历每个broker，取出该topic的metadata，然后再遍历其中的每个partition
	 * metadata，如果找到我们要找的partition就返回 根据返回的
	 * PartitionMetadata.leader().host()找到leader broker
	 * 
	 * @param brokerHost
	 * @param port
	 * @param topic
	 * @return
	 * @throws Exception
	 */
	public static TreeMap<Integer, PartitionMetadata> findLeader(String brokerHost, int port, String topic) {
		TreeMap<Integer, PartitionMetadata> map = new TreeMap<Integer, PartitionMetadata>();
		SimpleConsumer consumer = null;
		try {
			consumer = new SimpleConsumer(brokerHost, port, 100000, 64 * 1024, "leaderLookup" + new Date().getTime());
			List<String> topics = Collections.singletonList(topic);
			TopicMetadataRequest req = new TopicMetadataRequest(topics);

			// 发送TopicMetadata Request请求
			kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);

			List<TopicMetadata> metaData = resp.topicsMetadata(); // 取到Topic的Metadata

			for (TopicMetadata item : metaData) {
				for (PartitionMetadata part : item.partitionsMetadata()) { // 遍历每个partition的metadata
//					System.out.println("partitionId:" + part.partitionId() + ", metadata:" + part);
//					if (!map.containsKey(part.partitionId()))
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
	 * Finding the Lead Broker for a Topic and Partition
	 * 
	 * 思路:遍历每个broker，取出该topic的metadata，然后再遍历其中的每个partition
	 * metadata，如果找到我们要找的partition就返回 根据返回的
	 * PartitionMetadata.leader().host()找到leader broker
	 * 
	 * @param brokerHost
	 * @param port
	 * @param topic
	 * @param partition
	 * @return
	 * @throws Exception
	 */
	public static PartitionMetadata findLeader(String brokerHost, int port, String topic, int partition)
			throws Exception {
		SimpleConsumer consumer = null;
		try {
			consumer = new SimpleConsumer(brokerHost, port, 100000, 64 * 1024, "leaderLookup" + new Date().getTime());
			List<String> topics = Collections.singletonList(topic);
			TopicMetadataRequest req = new TopicMetadataRequest(topics);

			// 发送TopicMetadata Request请求
			kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);

			List<TopicMetadata> metaData = resp.topicsMetadata(); // 取到Topic的Metadata

			for (TopicMetadata item : metaData) {
				for (PartitionMetadata part : item.partitionsMetadata()) { // 遍历每个partition的metadata
					if (part.partitionId() == partition) { // 确认是否是我们要找的partition
						return part;
					}
				}
			}

			return null;
		} catch (Exception e) {
			throw new Exception(
					"Error communicating with Broker [" + brokerHost + "] to find Leader for [" + topic + "]", e);
		} finally {
			if (consumer != null) {
				consumer.close();
			}
		}
	}

	public static Map<Integer, Long> offset(String topic, String bootstrapServers, boolean isLast) {
		String[] servers = bootstrapServers.split(",");

		for (int i = 0, size = servers.length; i < size; i++) {
			String[] hostAndPort = servers[i].split(":");
			try {
				System.out.println(offset(topic, hostAndPort[0], Integer.parseInt(hostAndPort[1]), isLast));
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Map<Integer, Long> offset(String topic, String host, int port, boolean isLast) throws Exception {
		Map<Integer, Long> partionAndOffset = new HashMap<Integer, Long>();
		TreeMap<Integer, PartitionMetadata> metadatas = KafkaUtils.findLeader(host, port, topic);

		for (Entry<Integer, PartitionMetadata> entry : metadatas.entrySet()) {
			int partition = entry.getKey();
			String leadBroker = entry.getValue().leader().host();
			System.out.println("host:" + host + ",leadBroker:" + leadBroker + ", partition:" + partition);

			String clientName = "Client_" + topic + "_" + partition + System.currentTimeMillis();
			SimpleConsumer consumer = null;

			try {
				consumer = new SimpleConsumer(leadBroker, port, 100000, 64 * 1024, clientName);
				long offset = -1;
				if (isLast) {// 获取最新偏移
					offset = KafkaUtils.getOffset(consumer, topic, partition, kafka.api.OffsetRequest.LatestTime(),
							clientName);
				} else {// 获取最早偏移
					offset = KafkaUtils.getOffset(consumer, topic, partition, kafka.api.OffsetRequest.EarliestTime(),
							clientName);
				}
				System.out.println("partition:" + partition + ", offset:" + offset);
				partionAndOffset.put(partition, offset);
			} finally {
				if (consumer != null) {
					consumer.close();
				}
			}
		}

		return partionAndOffset;
	}

}
