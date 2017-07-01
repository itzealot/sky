package com.sky.project.share.common.function;

import java.io.Serializable;

/**
 * (T,T)=>T function
 * 
 * @author zealot
 *
 * @param <T>
 */
@FunctionalInterface
public interface ReduceFunction<T> extends Serializable {

	/**
	 * (T,T)=>T
	 * 
	 * @param paramT1
	 * @param paramT2
	 * @return
	 * @throws Exception
	 */
	T call(T paramT1, T paramT2) throws Exception;
}