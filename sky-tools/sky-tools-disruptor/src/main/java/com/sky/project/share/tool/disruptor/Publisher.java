package com.sky.project.share.tool.disruptor;

import java.io.Serializable;

/**
 * 发布事件接口
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface Publisher<T> extends Serializable {

	/**
	 * 根据数据源发布事件
	 * 
	 * @param source
	 */
	void publish(T source);

}
