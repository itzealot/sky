package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * T=>Iterable<R> function
 * 
 * @author zealot
 *
 * @param <T>
 * @param <R>
 */
public abstract interface FlatMapFunction<T, R> extends Serializable {
	/**
	 * T=>Iterable<R>
	 * 
	 * @param paramT
	 * @return
	 * @throws Exception
	 */
	public abstract Iterable<R> call(T paramT) throws Exception;
}