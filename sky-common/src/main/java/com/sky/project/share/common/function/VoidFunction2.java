package com.sky.project.share.common.function;

import java.io.Serializable;

/**
 * (T1,T2)=>None function
 * 
 * @author zealot
 *
 * @param <T1>
 * @param <T2>
 */
@FunctionalInterface
public interface VoidFunction2<T1, T2> extends Serializable {

	/**
	 * (T1,T2)=>None
	 * 
	 * @param paramT1
	 * @param paramT2
	 * @throws Exception
	 */
	void call(T1 paramT1, T2 paramT2) throws Exception;
}