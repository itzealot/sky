package com.sky.projects.message;

/**
 * 参数接口，返回相应的需要的参数列表
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface Params<T> {

	/**
	 * 返回参数列表
	 * 
	 * @param t
	 * @return
	 */
	Object[] getParams(T t);
}
