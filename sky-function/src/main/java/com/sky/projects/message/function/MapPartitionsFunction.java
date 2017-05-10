package com.sky.projects.message.function;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Iterator<T>=>Iterator<U> function
 * 
 * @author zealot
 *
 * @param <T>
 * @param <U>
 */
public abstract interface MapPartitionsFunction<T, U> extends Serializable {
	/**
	 * Iterator<T>=>Iterator<U>
	 * 
	 * @param paramIterator
	 * @return
	 * @throws Exception
	 */
	public abstract Iterable<U> call(Iterator<T> paramIterator) throws Exception;
}