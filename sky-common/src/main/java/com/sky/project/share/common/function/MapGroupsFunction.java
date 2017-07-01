package com.sky.project.share.common.function;

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
@FunctionalInterface
public interface MapGroupsFunction<K, V, R> extends Serializable {

	/**
	 * (K,Iterator<V>)=>R
	 * 
	 * @param paramK
	 * @param paramIterator
	 * @return
	 * @throws Exception
	 */
	R call(K paramK, Iterator<V> paramIterator) throws Exception;
}