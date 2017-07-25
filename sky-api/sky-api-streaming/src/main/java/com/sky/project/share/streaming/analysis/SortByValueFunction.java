package com.sky.project.share.streaming.analysis;

import org.apache.spark.api.java.JavaRDD;

/**
 * SortByValueFunction
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface SortByValueFunction<T> extends SortFunction<T> {

	/**
	 * sortByValue
	 * 
	 * @param rdd
	 */
	void sortByValue(JavaRDD<T> rdd);

}
