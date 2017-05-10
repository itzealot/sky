package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * T=>boolean filter function
 * 
 * @author zealot
 *
 * @param <T>
 */
public abstract interface FilterFunction<T> extends Serializable {
	/**
	 * T=>boolean
	 * 
	 * @param paramT
	 * @return
	 * @throws Exception
	 */
	public abstract boolean call(T paramT) throws Exception;
}