package com.sky.project.share.common.function;

/**
 * T=>U function
 * 
 * @author zealot
 *
 * @param <T>
 * @param <U>
 */
@FunctionalInterface
public interface MapFunction<T, U> extends Function<T, U> {

	/**
	 * T=>U
	 * 
	 * @param paramT
	 * @return
	 * @throws Exception
	 */
	U call(T paramT) throws Exception;
}