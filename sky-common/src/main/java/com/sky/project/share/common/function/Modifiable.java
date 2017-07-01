package com.sky.project.share.common.function;

/**
 * Modifiable<T1, T2>(T1=>T2)
 * 
 * @author zealot
 *
 * @param <T1>
 * @param <T2>
 */
@FunctionalInterface
public interface Modifiable<T1, T2> {

	/**
	 * src=update=>dst
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	boolean update(T1 src, T2 dst);
}
