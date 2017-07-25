package com.sky.project.share.streaming.analysis.support;

import org.apache.spark.api.java.JavaRDD;

import com.sky.project.share.streaming.analysis.SortByKeyAndValueFunction;

import scala.Tuple2;

/**
 * AbstractSortByAndValueFunction
 * 
 * @author zealot
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class AbstractSortByAndValueFunction<T> implements SortByKeyAndValueFunction<T> {

	@Override
	public void sortByKeyAndValue(JavaRDD<T> rdd) {
		rdd.mapToPair(t -> new Tuple2<>(getReduceKey(t), t)).reduceByKey((v1, v2) -> reduce(v1, v2))
				.sortByKey(isAscending(), getNumPartitions()).map(tuple2 -> tuple2)
				.sortBy(tuple -> tuple._2, isAscending(), getNumPartitions());
	};
}
