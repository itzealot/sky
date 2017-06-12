package com.surfilter.gamass.producer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.surfilter.gamass.service.ProducerMsgTask;

public class KafkaProducer {

	private ProducerMsgTask[] producerMsgTasks;
	private ExecutorService executor;
	private int poolSize;

	public KafkaProducer(BlockingQueue<String> queue, int poolSize, String brokerUrl, String topic) {
		executor = Executors.newFixedThreadPool(poolSize);
		this.poolSize = poolSize;
		producerMsgTasks = new ProducerMsgTask[poolSize];

		for (int i = 0; i < poolSize; i++) {
			producerMsgTasks[i] = new ProducerMsgTask(queue, brokerUrl, topic);
		}
	}

	public void produce() {
		for (int i = 0; i < poolSize; i++) {
			executor.submit(producerMsgTasks[i]);
		}
	}

	public void shutdown() {
		for (int i = 0; i < poolSize; i++) {
			producerMsgTasks[i].shutdown();
		}
		executor.shutdown();
	}
}
