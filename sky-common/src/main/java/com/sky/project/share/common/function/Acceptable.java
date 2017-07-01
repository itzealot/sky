package com.sky.project.share.common.function;

import java.io.Serializable;

/**
 * Accept T function
 * 
 * @author zealot
 *
 * @param <T>
 */
@FunctionalInterface
public interface Acceptable<T> extends Serializable {

	/**
	 * Accept T
	 * 
	 * @param paramT
	 * @throws Exception
	 */
	boolean accept(T paramT);
}