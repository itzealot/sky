package com.sky.project.share.api.kafka.support.provider;

/**
 * Provider
 * 
 * @author zealot
 *
 */
public interface Provider extends AutoCloseable {

	/**
	 * provide data
	 */
	void provide();

	/**
	 * close
	 */
	void close();

}
