package com.sky.project.share.common.function;

import java.io.Serializable;

/**
 * (T1, T2)=>R function
 * 
 * @author zealot
 *
 * @param <T1>
 * @param <T2>
 * @param <R>
 */
@FunctionalInterface
public interface Function2<T1, T2, R> extends Serializable {

	/**
	 * (T1, T2)=>R
	 * 
	 * @param paramT1
	 * @param paramT2
	 * @return
	 * @throws Exception
	 */
	R call(T1 paramT1, T2 paramT2) throws Exception;
}