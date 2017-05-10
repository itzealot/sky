package com.sky.projects.message;

/**
 * 距离接口，根据距离判断是否可以归属同一个群体
 * 
 * @author zealot
 *
 * @param <T>
 * @param <V>
 */
public interface Cluster<T, V> {

	/**
	 * 是否聚集
	 * 
	 * @param t
	 * @param v
	 * @return
	 */
	boolean isCluster(T t, V v);
}
