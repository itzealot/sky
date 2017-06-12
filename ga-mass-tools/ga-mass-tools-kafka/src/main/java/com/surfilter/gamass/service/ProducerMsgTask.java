package com.surfilter.gamass.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.surfilter.gamass.conf.Constant;
import com.surfilter.gamass.conf.KafkaConfig;
import com.surfilter.mass.tools.util.Threads;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class ProducerMsgTask implements Runnable {

	private BlockingQueue<String> queue;
	private String topic;
	private ProducerConfig config;
	private volatile boolean isRunning = true;

	public ProducerMsgTask(BlockingQueue<String> queue, String brokerUrl, String topic) {
		this.queue = queue;
		config = new ProducerConfig(KafkaConfig.initProducer(brokerUrl));
		this.topic = topic;
	}

	@Override
	public void run() {
		Producer<byte[], byte[]> producer = new Producer<byte[], byte[]>(config);

		while (isRunning) {
			List<String> msgs = new ArrayList<>(Constant.PRODUCER_BATCH_SIZE);

			queue.drainTo(msgs, Constant.PRODUCER_BATCH_SIZE);

			if (!msgs.isEmpty()) {
				StringBuilder builder = new StringBuilder();

				for (int i = 0, size = msgs.size(); i < size; i++) {
					builder.append(msgs.get(i));
					if ((i + 1) % Constant.KAFKA_EVENTS_SIZE == 0 || i == size - 1) {
						builder.deleteCharAt(builder.length() - 1);

						producer.send(new KeyedMessage<byte[], byte[]>(topic, builder.toString().getBytes()));
						builder.setLength(0);
					}
				}
				System.out.println("put kafka size:" + msgs.size());
			} else {
				Threads.sleep(100);
			}

			msgs.clear();
			msgs = null;
		}

	}

	public void shutdown() {
		this.isRunning = false;
	}

}
