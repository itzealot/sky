package com.sky.project.share.api.kafka.support.consumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import com.sky.project.share.api.kafka.SkyKafkaConsts;
import com.sky.project.share.api.kafka.SkyKafkaContext;

/**
 * AbstractKafkaMessageConsumer
 * 
 * @author zealot
 *
 */
public abstract class AbstractKafkaMessageConsumer implements Runnable {

	private final BlockingQueue<String> queue;
	protected final SkyKafkaContext context;
	private final int batchSize;

	public AbstractKafkaMessageConsumer(SkyKafkaContext context) {
		this.context = context;
		this.queue = context.getBlockingQueue();
		this.batchSize = context.getInt(SkyKafkaConsts.CONSUMER_CONSUME_BATCH_SIZE,
				SkyKafkaConsts.DEFAULT_CONSUMER_CONSUME_BATCH_SIZE);
	}

	@Override
	public final void run() {
		while (true) {
			Collection<String> messages = new ArrayList<>(batchSize);

			int size = queue.drainTo(messages);

			if (size > 0) {
				try {
					this.doRun(messages);
				} catch (Throwable e) {// 防御性容错
				}

				messages.clear();
			}

			messages = null;
		}
	}

	/**
	 * 消费消息
	 * 
	 * @param messages
	 */
	protected abstract void doRun(Collection<String> messages);

}
