package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * T=>Tuple2<K, V> function
 * 
 * @author zealot
 *
 * @param <T>
 * @param <K>
 * @param <V>
 */
public abstract interface PairFunction<T, K, V> extends Serializable {
	/**
	 * T=>Tuple2<K, V>
	 * 
	 * @param paramT
	 * @return
	 * @throws Exception
	 */
	public abstract Tuple2<K, V> call(T paramT) throws Exception;
}