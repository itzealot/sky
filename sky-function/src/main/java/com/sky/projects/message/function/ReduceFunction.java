package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * (T,T)=>T function
 * 
 * @author zealot
 *
 * @param <T>
 */
public abstract interface ReduceFunction<T> extends Serializable {
	/**
	 * (T,T)=>T
	 * 
	 * @param paramT1
	 * @param paramT2
	 * @return
	 * @throws Exception
	 */
	public abstract T call(T paramT1, T paramT2) throws Exception;
}