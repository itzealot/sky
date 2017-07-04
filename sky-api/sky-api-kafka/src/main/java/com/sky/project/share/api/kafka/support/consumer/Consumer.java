package com.sky.project.share.api.kafka.support.consumer;

/**
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
	void execute(KafkaMessageConsumer task);

	/**
	 * close
	 */
	void close();
}
