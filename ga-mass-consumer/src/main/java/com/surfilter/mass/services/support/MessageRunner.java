package com.surfilter.mass.services.support;

import com.surfilter.mass.services.MessageExecutor;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

/**
 * 监听某个topic的单个partition从Kafka拉取数据写入缓冲队列
 * 
 * @author hapuer
 *
 */
public class MessageRunner implements Runnable {

	private KafkaStream<byte[], byte[]> partition;
	private MessageExecutor executor;

	MessageRunner(KafkaStream<byte[], byte[]> partition, MessageExecutor executor) {
		this.partition = partition;
		this.executor = executor;
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = partition.iterator();

		while (it.hasNext()) {
			executor.execute(new String(it.next().message()));
		}
	}

}
