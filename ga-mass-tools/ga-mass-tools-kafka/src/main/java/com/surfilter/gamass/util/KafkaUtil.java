package com.surfilter.gamass.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.surfilter.gamass.conf.Constant;
import com.surfilter.gamass.entity.KafkaTopicOffset;

import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;

/**
 * @function:kafka相关工具类
 */
@SuppressWarnings("serial")
public class KafkaUtil implements Serializable {

	/**
	 * 根据 brokerlist 获取 ip
	 * 
	 * @param brokerlist
	 * @return
	 */
	public static String[] getIpsFromBrokerList(String brokerlist) {
		String[] brokers = brokerlist.split(",");
		for (int i = 0; i < brokers.length; i++) {
			brokers[i] = brokers[i].split(":")[0];
		}
		return brokers;
	}

	/**
	 * 根据 brokerlist 获取端口
	 * 
	 * @param brokerlist
	 * @return
	 */
	public static Map<String, Integer> getPortFromBrokerList(String brokerlist) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		String[] brokers = brokerlist.split(",");
		for (String item : brokers) {
			String[] itemArr = item.split(":");
			if (itemArr.length > 1) {
				map.put(itemArr[0], Integer.parseInt(itemArr[1]));
			}
		}
		return map;
	}

	public static KafkaTopicOffset topicMetadataRequest(String brokerlist, String topic, String group) {
		List<String> topics = Collections.singletonList(topic);
		TopicMetadataRequest topicMetadataRequest = new TopicMetadataRequest(topics);

		KafkaTopicOffset kafkaTopicOffset = new KafkaTopicOffset(topic);
		String[] seeds = getIpsFromBrokerList(brokerlist);
		Map<String, Integer> portMap = getPortFromBrokerList(brokerlist);

		for (int i = 0, size = seeds.length; i < size; i++) {
			SimpleConsumer consumer = null;
			try {
				consumer = new SimpleConsumer(seeds[i], portMap.get(seeds[i]), Constant.TIMEOUT, Constant.BUFFERSIZE,
						group);

				List<TopicMetadata> metaData = consumer.send(topicMetadataRequest).topicsMetadata();

				for (TopicMetadata item : metaData) {
					for (PartitionMetadata part : item.partitionsMetadata()) {
						kafkaTopicOffset.getLeaderList().put(part.partitionId(), part.leader().host());
						kafkaTopicOffset.getOffsetList().put(part.partitionId(), 0L);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (consumer != null) {
					consumer.close();
				}
			}
		}

		return kafkaTopicOffset;
	}

	/**
	 * 根据 brokerlist及topic获取 last offset
	 * 
	 * @param brokerlist
	 * @param topic
	 * @return
	 */
	public static KafkaTopicOffset getLastOffsetByTopic(String brokerlist, String topic, String group) {
		KafkaTopicOffset kafkaTopicOffset = topicMetadataRequest(brokerlist, topic, group);
		String[] seeds = getIpsFromBrokerList(brokerlist);
		Map<String, Integer> portMap = getPortFromBrokerList(brokerlist);

		for (int i = 0, size = seeds.length; i < size; i++) {
			SimpleConsumer consumer = null;
			Iterator<Entry<Integer, Long>> iterator = kafkaTopicOffset.getOffsetList().entrySet().iterator();

			try {
				consumer = new SimpleConsumer(seeds[i], portMap.get(seeds[i]), Constant.TIMEOUT, Constant.BUFFERSIZE,
						group);

				while (iterator.hasNext()) {
					int partitonId = iterator.next().getKey();

					if (!kafkaTopicOffset.getLeaderList().get(partitonId).equals(seeds[i])) {
						continue;
					}

					TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partitonId);
					Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();

					requestInfo.put(topicAndPartition,
							new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(), 1));
					kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo,
							kafka.api.OffsetRequest.CurrentVersion(), group);
					OffsetResponse response = consumer.getOffsetsBefore(request);

					long[] offsets = response.offsets(topic, partitonId);
					if (offsets.length > 0) {
						kafkaTopicOffset.getOffsetList().put(partitonId, offsets[0]);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (consumer != null) {
					consumer.close();
				}
			}
		}

		return kafkaTopicOffset;
	}

	public static KafkaTopicOffset getEarlyOffsetByTopic(String brokerlist, String topic, String group) {
		KafkaTopicOffset kafkaTopicOffset = topicMetadataRequest(brokerlist, topic, group);
		String[] seeds = getIpsFromBrokerList(brokerlist);
		Map<String, Integer> portMap = getPortFromBrokerList(brokerlist);

		for (int i = 0; i < seeds.length; i++) {
			SimpleConsumer consumer = null;
			Iterator<Entry<Integer, Long>> iterator = kafkaTopicOffset.getOffsetList().entrySet().iterator();

			try {
				consumer = new SimpleConsumer(seeds[i], portMap.get(seeds[i]), Constant.TIMEOUT, Constant.BUFFERSIZE,
						group);

				while (iterator.hasNext()) {
					Map.Entry<Integer, Long> entry = iterator.next();
					int partitonId = entry.getKey();

					if (!kafkaTopicOffset.getLeaderList().get(partitonId).equals(seeds[i])) {
						continue;
					}

					TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partitonId);
					Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();

					requestInfo.put(topicAndPartition,
							new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.EarliestTime(), 1));
					kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo,
							kafka.api.OffsetRequest.CurrentVersion(), group);
					OffsetResponse response = consumer.getOffsetsBefore(request);
					long[] offsets = response.offsets(topic, partitonId);
					if (offsets.length > 0) {
						kafkaTopicOffset.getOffsetList().put(partitonId, offsets[0]);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (consumer != null) {
					consumer.close();
				}
			}
		}

		return kafkaTopicOffset;
	}

	public static Map<String, KafkaTopicOffset> getKafkaOffsetByTopicList(String brokerList, List<String> topics,
			String group) {
		Map<String, KafkaTopicOffset> map = new HashMap<String, KafkaTopicOffset>();
		for (int i = 0; i < topics.size(); i++) {
			map.put(topics.get(i), getLastOffsetByTopic(brokerList, topics.get(i), group));
		}
		return map;
	}

	public static Map<String, KafkaTopicOffset> getKafkaEarlyOffsetByTopicList(String brokerList, List<String> topics,
			String group) {
		Map<String, KafkaTopicOffset> map = new HashMap<String, KafkaTopicOffset>();
		for (int i = 0; i < topics.size(); i++) {
			map.put(topics.get(i), getEarlyOffsetByTopic(brokerList, topics.get(i), group));
		}
		return map;
	}

	private KafkaUtil() {
	}
}
