package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * For each T function
 * 
 * @author zealot
 *
 * @param <T>
 */
public abstract interface ForeachFunction<T> extends Serializable {
	/**
	 * for each T
	 * 
	 * @param paramT
	 * @throws Exception
	 */
	public abstract void call(T paramT) throws Exception;
}