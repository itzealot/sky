package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * T=>U function
 * 
 * @author zealot
 *
 * @param <T>
 * @param <U>
 */
public abstract interface MapFunction<T, U> extends Serializable {
	/**
	 * T=>U
	 * 
	 * @param paramT
	 * @return
	 * @throws Exception
	 */
	public abstract U call(T paramT) throws Exception;
}