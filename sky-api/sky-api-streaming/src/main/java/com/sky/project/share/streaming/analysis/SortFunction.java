package com.sky.project.share.streaming.analysis;

import java.io.Serializable;

/**
 * SortFunction
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface SortFunction<T> extends Serializable {

	/**
	 * (v1, v2)=>v
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	T reduce(T v1, T v2);

	/**
	 * 获取 reduce key
	 * 
	 * @return the reduce key
	 */
	String getReduceKey(T t);

	/**
	 * 是否为升序
	 * 
	 * @return true:升序,false:降序
	 */
	default boolean isAscending() {
		return true;
	}

	/**
	 * 
	 * @return
	 */
	default int getNumPartitions() {
		return 3;
	}

}
