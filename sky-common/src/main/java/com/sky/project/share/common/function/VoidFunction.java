package com.sky.project.share.common.function;

import java.io.Serializable;

/**
 * T=>None function
 * 
 * @author zealot
 *
 * @param <T>
 */
@FunctionalInterface
public interface VoidFunction<T> extends Serializable {

	/**
	 * T=>None
	 * 
	 * @param paramT
	 * @throws Exception
	 */
	void call(T paramT) throws Exception;
}