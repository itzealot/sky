package com.sky.project.share.api.registry;

/**
 * Registry
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface Registry<T> {

	void register(T t);

	void close();
}
