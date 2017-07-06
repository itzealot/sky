package com.sky.project.share.api.kafka.support.provider.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sky.project.share.api.kafka.SkyKafkaConsts;
import com.sky.project.share.api.kafka.SkyKafkaContext;
import com.sky.project.share.api.kafka.support.provider.Provider;
import com.sky.project.share.common.thread.pool.NamedThreadFactory;

import kafka.common.TopicAndPartition;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * Kafka 数据生产者，针对一类数据(指定topicName与partitions)从Kafka拉取数据
 * 
 * @author zealot
 *
 */
public class KafkaProvider implements Provider {

	private final ConsumerConnector connector;
	private final BlockingQueue<String> queue;
	private final ExecutorService threadPool;
	private final String topic;
	private final int partition;

	public KafkaProvider(SkyKafkaContext context, TopicAndPartition topicAndPartition) {
		this.connector = kafka.consumer.Consumer
				.createJavaConsumerConnector(new ConsumerConfig(initKafkaProps(context)));
		this.queue = context.getBlockingQueue();
		this.topic = topicAndPartition.topic();
		this.partition = topicAndPartition.partition();
		this.threadPool = Executors.newFixedThreadPool(partition, new NamedThreadFactory("SkyKafkaProvider"));
	}

	private Properties initKafkaProps(SkyKafkaContext context) {
		Properties properties = new Properties();

		properties.setProperty("zookeeper.connect", context.getStrings(SkyKafkaConsts.KAFKA_ZK_URL));
		properties.setProperty("zookeeper.connection.timeout.ms", "100000");
		properties.setProperty("auto.offset.reset", "largest");
		properties.setProperty("group.id", context.get(SkyKafkaConsts.KAFKA_GROUP_ID));

		/*
		 * 解决 ConsumerRebalanceFailedException, can't rebalance after {num}
		 * retries 问题的测试性参数
		 */
		properties.setProperty("zookeeper.session.timeout.ms", "5000");
		properties.setProperty("rebalance.max.retries", "10");
		properties.setProperty("rebalance.backoff.ms", "2000");

		return properties;
	}

	@Override
	public void provide() {
		Map<String, Integer> topicsMap = new HashMap<String, Integer>(1);
		topicsMap.put(topic, partition);

		List<KafkaStream<byte[], byte[]>> kafkaStreams = connector.createMessageStreams(topicsMap).get(topic);

		// for each every partition submitting Task
		for (KafkaStream<byte[], byte[]> kafkaStream : kafkaStreams) {
			threadPool.execute(new KafkaPartitionProvider(kafkaStream, queue));
		}
	}

	@Override
	public void close() {
		try {
			if (threadPool != null) {
				threadPool.shutdown();
			}
		} finally {
			if (connector != null)
				connector.shutdown();
		}
	}

}
