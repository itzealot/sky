package com.sky.project.share.api.kafka.support.provider.impl;

import java.util.Objects;

import com.sky.project.share.api.kafka.support.MessageExecutor;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

/**
 * 消费某个topic的单个partition
 * 
 * @author zealot
 *
 */
public class KafkaPartitionProvider implements Runnable {

	private final KafkaStream<byte[], byte[]> kafkaStream;
	private final MessageExecutor<byte[]> consumer;

	public KafkaPartitionProvider(KafkaStream<byte[], byte[]> kafkaStream, MessageExecutor<byte[]> consumer) {
		Objects.requireNonNull(consumer, "consumer can't be null");

		this.kafkaStream = kafkaStream;
		this.consumer = consumer;
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();

		while (it.hasNext()) {
			try {
				consumer.consume(it.next().message());
			} catch (Exception e) {
			}
		}
	}

}
