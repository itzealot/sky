package com.sky.projects.message.function;

import java.io.Serializable;
import java.util.Iterator;

/**
 * For each Iterator<T> function
 * 
 * @author zealot
 *
 * @param <T>
 */
public abstract interface ForeachPartitionFunction<T> extends Serializable {
	/**
	 * For each Iterator<T>
	 * 
	 * @param paramIterator
	 * @throws Exception
	 */
	public abstract void call(Iterator<T> paramIterator) throws Exception;
}