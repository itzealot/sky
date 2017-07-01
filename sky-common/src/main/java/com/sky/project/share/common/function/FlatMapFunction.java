package com.sky.project.share.common.function;

import java.io.Serializable;

/**
 * T=>Iterable<R> function
 * 
 * @author zealot
 *
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface FlatMapFunction<T, R> extends Serializable {

	/**
	 * T=>Iterable<R>
	 * 
	 * @param paramT
	 * @return
	 * @throws Exception
	 */
	Iterable<R> call(T paramT) throws Exception;
}