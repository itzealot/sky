package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * (T1, T2, T3, T4)=>R function
 * 
 * @author zealot
 *
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <T4>
 * @param <R>
 */
public abstract interface Function4<T1, T2, T3, T4, R> extends Serializable {
	/**
	 * (T1, T2, T3, T4)=>R
	 * 
	 * @param paramT1
	 * @param paramT2
	 * @param paramT3
	 * @param paramT4
	 * @return
	 * @throws Exception
	 */
	public abstract R call(T1 paramT1, T2 paramT2, T3 paramT3, T4 paramT4) throws Exception;
}