package com.sky.projects.message.function;

import java.io.Serializable;
import java.util.Iterator;

/**
 * (K,Iterator<V>)=>R function
 * 
 * @author zealot
 *
 * @param <K>
 * @param <V>
 * @param <R>
 */
public abstract interface MapGroupsFunction<K, V, R> extends Serializable {
	/**
	 * (K,Iterator<V>)=>R
	 * 
	 * @param paramK
	 * @param paramIterator
	 * @return
	 * @throws Exception
	 */
	public abstract R call(K paramK, Iterator<V> paramIterator) throws Exception;
}