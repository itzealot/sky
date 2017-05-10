package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * T=>Iterable<Tuple2<K, V>> function
 * 
 * @author zealot
 *
 * @param <T>
 * @param <K>
 * @param <V>
 */
public abstract interface PairFlatMapFunction<T, K, V> extends Serializable {
	/**
	 * T=>Iterable<Tuple2<K, V>>
	 * 
	 * @param paramT
	 * @return
	 * @throws Exception
	 */
	public abstract Iterable<Tuple2<K, V>> call(T paramT) throws Exception;
}