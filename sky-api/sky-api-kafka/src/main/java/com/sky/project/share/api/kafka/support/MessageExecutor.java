package com.sky.project.share.api.kafka.support;

import java.util.concurrent.BlockingQueue;

/**
 * MessageExecutor
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface MessageExecutor<T> {

	void register(BlockingQueue<String> queue);

	/**
	 * consume
	 * 
	 * @param obj
	 * @throws Exception
	 */
	void consume(T obj) throws Exception;
}
