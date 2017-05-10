package com.sky.projects.message;

/**
 * 注册接口，接受相应的参数
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface Register<T> {

	/**
	 * 接受参数，注册到实现类中
	 * 
	 * @param t
	 */
	void register(T t);
}
