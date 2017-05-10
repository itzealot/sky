package com.sky.projects.message;

/**
 * 更新接口，适用于 src=>dst 更新
 * 
 * @author zealot
 *
 * @param <T1>
 * @param <T2>
 */
public interface Modifiable<T1, T2> {

	/**
	 * 更新 src=>dst
	 * 
	 * @param src
	 * @param dst
	 */
	void update(T1 src, T2 dst);
}
