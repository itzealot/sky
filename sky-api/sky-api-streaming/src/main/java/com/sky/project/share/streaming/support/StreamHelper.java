package com.sky.project.share.streaming.support;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.ZkClient;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.kafka.HasOffsetRanges;
import org.apache.spark.streaming.kafka.OffsetRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.project.share.streaming.util.ZkUtils;

import kafka.common.TopicAndPartition;

/**
 * 辅助处理类. 抽取信息等.
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class StreamHelper implements Serializable {
	private static Logger LOG = LoggerFactory.getLogger(StreamHelper.class);

	/**
	 * 初始化Kafka 的 topic 的每个分区的偏移。
	 * 
	 * @param zkConn
	 * @param groupId
	 * @param topic
	 * @param partionAndOffset
	 * @return
	 */
	public static Map<TopicAndPartition, Long> topicOffsetToMap(String zkConn, String groupId, String topic,
			final Map<Integer, Long> partionAndOffset) {
		ZkClient zkClient = null;
		Map<TopicAndPartition, Long> topicMap = new HashMap<TopicAndPartition, Long>();
		try {
			zkClient = new ZkClient(zkConn, 10000, 10000, new ZkUtils.StringSerializer());
			List<String> brokerPartitions = ZkUtils.getBrokerPartitions(zkClient, topic);

			for (String brokerPartition : brokerPartitions) {
				String[] brokerPartitionParts = brokerPartition.split("-");
				int partition = Integer.parseInt(brokerPartitionParts[1]);

				long lastConsumedOffset = ZkUtils.getLastConsumedOffset(zkClient, groupId, topic,
						brokerPartitionParts[1]);

				if (partionAndOffset.containsKey(partition)) {
					long kafkaOffset = partionAndOffset.get(partition);
					if (kafkaOffset > lastConsumedOffset) {
						lastConsumedOffset = kafkaOffset;
					}
				}

				topicMap.put(new TopicAndPartition(topic, partition), lastConsumedOffset);
				LOG.info("topicOffsetToMap topic({}), parititon({}), offset is {}.", topic, partition,
						lastConsumedOffset);
			}
		} catch (Exception e) {
			LOG.error("topicOffsetToMap error!", e);
		} finally {
			try {
				ZkUtils.close(zkClient);
			} catch (IOException e) {
				LOG.error("close zk connection error!", e);
			}
		}
		return topicMap;
	}

	/**
	 * 将 streaming 的 Kafka 消费偏移更新到 zk.
	 * 
	 * @param sourceStream
	 * @param streamType
	 * @param zkConn
	 * @param groupId
	 */
	public static void updateOffsetToZk(JavaDStream<String> sourceStream, final String streamType, final String zkConn,
			final String groupId) {
		sourceStream.foreachRDD(new VoidFunction<JavaRDD<String>>() {
			@Override
			public void call(JavaRDD<String> rdd) throws Exception {
				OffsetRange[] offsets = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
				ZkClient zkClient = null;
				try {
					zkClient = new ZkClient(zkConn, 10000, 10000, new ZkUtils.StringSerializer());
					for (int i = 0; i < offsets.length; i++) {
						OffsetRange offsetRange = offsets[i];
						String topic = offsetRange.topic();
						int partition = offsetRange.partition();

						long last = ZkUtils.getLastConsumedOffset(zkClient, groupId, topic, String.valueOf(partition));
						long untilOffset = offsetRange.untilOffset();

						if (untilOffset > last) {
							ZkUtils.commitLastConsumedOffset(zkClient, groupId, topic, String.valueOf(partition),
									untilOffset);
						}
					}
				} catch (Exception e) {
					LOG.error(streamType + " update offset to zk error!", e);
				} finally {
					try {
						ZkUtils.close(zkClient);
					} catch (IOException e) {
						LOG.error(streamType + " close zk error!", e);
					}
				}
			}
		});
	}

	private StreamHelper() {
	}
}