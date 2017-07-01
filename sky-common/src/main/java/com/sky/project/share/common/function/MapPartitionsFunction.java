package com.sky.project.share.common.function;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Iterator<T>=>Iterator<U> function
 * 
 * @author zealot
 *
 * @param <T>
 * @param <U>
 */
@FunctionalInterface
public interface MapPartitionsFunction<T, U> extends Serializable {

	/**
	 * Iterator<T>=>Iterator<U>
	 * 
	 * @param paramIterator
	 * @return
	 * @throws Exception
	 */
	Iterable<U> call(Iterator<T> paramIterator) throws Exception;
}