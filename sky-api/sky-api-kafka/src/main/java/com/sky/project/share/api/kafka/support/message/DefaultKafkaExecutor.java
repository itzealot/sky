package com.sky.project.share.api.kafka.support.message;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import com.sky.project.share.api.kafka.support.MessageExecutor;

/**
 * KafkaStringConsumer
 * 
 * @author zealot
 *
 */
public class DefaultKafkaExecutor implements MessageExecutor<byte[]> {

	private BlockingQueue<String> queue;

	@Override
	public void consume(byte[] bytes) throws Exception {
		queue.put(new String(bytes, "UTF-8"));
	}

	@Override
	public void register(BlockingQueue<String> queue) {
		Objects.requireNonNull(queue, "queue can't be null");
		this.queue = queue;
	}

}
