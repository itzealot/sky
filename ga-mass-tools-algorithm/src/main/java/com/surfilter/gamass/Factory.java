package com.surfilter.gamass;

/**
 * 工厂接口，专门用于创建实例
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface Factory<T> {

	/**
	 * 创建实例
	 * 
	 * @param objects
	 * @return
	 */
	T newInstance(Object... objects);
}
