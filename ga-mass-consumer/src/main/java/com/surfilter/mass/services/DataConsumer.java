package com.surfilter.mass.services;

/**
 * 消息消费接口
 * 
 * @author zealot
 *
 */
public interface DataConsumer {

	/**
	 * 开启消费
	 */
	void startConsume();

	/**
	 * 终止消费
	 */
	void stop();

}
