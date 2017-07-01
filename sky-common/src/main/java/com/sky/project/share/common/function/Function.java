package com.sky.project.share.common.function;

import java.io.Serializable;

/**
 * T1=>R function
 * 
 * @author zealot
 *
 * @param <T1>
 * @param <R>
 */
@FunctionalInterface
public interface Function<T1, R> extends Serializable {

	/**
	 * T1=>R
	 * 
	 * @param paramT1
	 * @return
	 * @throws Exception
	 */
	R call(T1 paramT1) throws Exception;
}