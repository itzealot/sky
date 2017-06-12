package com.surfilter.gamass.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

/**
 * 针对 Kafka 单个 partition 进行消费
 * 
 * @author zealot
 */
public class ConsumerMsgTask implements Runnable {

	private static Logger LOG = LoggerFactory.getLogger(ConsumerMsgTask.class);

	private KafkaStream<byte[], byte[]> stream;
	private int threadNumber;

	public ConsumerMsgTask(KafkaStream<byte[], byte[]> stream, int threadNumber) {
		this.threadNumber = threadNumber;
		this.stream = stream;
	}

	@Override
	public void run() {
		LOG.debug("Thread " + threadNumber + ": start...........");
		ConsumerIterator<byte[], byte[]> it = stream.iterator();

		while (it.hasNext()) {
			String msg = new String(it.next().message());
			LOG.info("Thread " + threadNumber + " receive size :" + msg.split("\002").length);
		}

		LOG.debug("Shutting down Thread: " + threadNumber);
	}
}