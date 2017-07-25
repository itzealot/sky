package com.sky.project.share.streaming.api;

/**
 * MessageConverter(适用于消息的转换与校验)
 * 
 * @author zealot
 * @param <T>
 */
public interface MessageConverter<T> {

	/**
	 * convert string message to object
	 * 
	 * @param line
	 * @return
	 */
	T convert(String line);

	/**
	 * validate message
	 * 
	 * @param message
	 * @return
	 */
	boolean validate(T message);

	/**
	 * update message
	 * 
	 * @param message
	 * @return
	 */
	T update(T message);

	String getStreamingType();
}
