package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * T=>None function
 * 
 * @author zealot
 *
 * @param <T>
 */
public abstract interface VoidFunction<T> extends Serializable {
	/**
	 * T=>None
	 * 
	 * @param paramT
	 * @throws Exception
	 */
	public abstract void call(T paramT) throws Exception;
}