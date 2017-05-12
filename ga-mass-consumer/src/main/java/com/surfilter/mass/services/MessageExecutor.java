package com.surfilter.mass.services;

/**
 * 单条消息处理接口
 * 
 * @author hapuer
 *
 */
public interface MessageExecutor {

	/**
	 * 接收单条信息
	 * 
	 * @param message
	 */
	void execute(String message);

}
