package com.sky.project.share.api.kafka.support.provider.impl;

import java.util.concurrent.BlockingQueue;

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
	private final BlockingQueue<String> queue;

	public KafkaPartitionProvider(KafkaStream<byte[], byte[]> kafkaStream, BlockingQueue<String> queue) {
		this.kafkaStream = kafkaStream;
		this.queue = queue;
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();

		while (it.hasNext()) {
			try {
				queue.put(new String(it.next().message(), "UTF-8"));
			} catch (Exception e) {
			}
		}
	}

}
