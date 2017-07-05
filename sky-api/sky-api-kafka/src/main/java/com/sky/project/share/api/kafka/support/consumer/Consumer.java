package com.sky.project.share.api.kafka.support.consumer;

/**
 * Consumer
 * 
 * @author zealot
 *
 */
public interface Consumer extends AutoCloseable {

	/**
	 * execute task
	 * 
	 * @param task
	 */
	void execute(AbstractKafkaMessageConsumer task);

	/**
	 * close
	 */
	void close();
}
