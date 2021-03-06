package com.sky.project.share.common.function;

import java.io.Serializable;

/**
 * (T1, T2, T3)=>R function
 * 
 * @author zealot
 *
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @param <R>
 */
@FunctionalInterface
public interface Function3<T1, T2, T3, R> extends Serializable {

	/**
	 * (T1, T2, T3)=>R
	 * 
	 * @param paramT1
	 * @param paramT2
	 * @param paramT3
	 * @return
	 * @throws Exception
	 */
	R call(T1 paramT1, T2 paramT2, T3 paramT3) throws Exception;
}