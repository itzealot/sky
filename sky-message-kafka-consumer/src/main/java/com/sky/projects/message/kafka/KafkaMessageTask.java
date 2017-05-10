package com.sky.projects.message.kafka;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.projects.message.SkyRunnable;
import com.sky.projects.message.function.Function;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

/**
 * Kafka message runner
 * 
 * @author zealot
 *
 * @param <V>
 */
@SuppressWarnings("serial")
public class KafkaMessageTask<V> implements SkyRunnable {
	private static Logger LOG = LoggerFactory.getLogger(KafkaMessageTask.class);

	private KafkaStream<byte[], byte[]> partition;
	private Function<byte[], V> executor;
	private BlockingQueue<V> queue;

	public KafkaMessageTask(BlockingQueue<V> queue, KafkaStream<byte[], byte[]> partition,
			Function<byte[], V> executor) {
		this.queue = queue;
		this.partition = partition;
		this.executor = executor;
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = partition.iterator();

		while (it.hasNext()) {
			MessageAndMetadata<byte[], byte[]> item = it.next();

			try {
				queue.put(executor.call(item.message()));
			} catch (Exception e) {
				LOG.error("put message into queue error", e);
			}
		}
	}

}
