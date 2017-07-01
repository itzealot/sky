package com.sky.project.share.common.function;

import java.io.Serializable;

import com.sky.project.share.common.support.Tuple2;

/**
 * T=>Iterable<Tuple2<K, V>> function
 * 
 * @author zealot
 *
 * @param <T>
 * @param <K>
 * @param <V>
 */
@FunctionalInterface
public interface PairFlatMapFunction<T, K, V> extends Serializable {

	/**
	 * T=>Iterable<Tuple2<K, V>>
	 * 
	 * @param paramT
	 * @return
	 * @throws Exception
	 */
	Iterable<Tuple2<K, V>> call(T paramT) throws Exception;
}