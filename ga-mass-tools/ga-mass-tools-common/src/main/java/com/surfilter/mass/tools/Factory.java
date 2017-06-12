package com.surfilter.mass.tools;

public interface Factory<T, V> {

	/**
	 * 根据值获取实例
	 * 
	 * @param value
	 * @return
	 */
	public T newInstance(V value);
}
