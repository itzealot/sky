package com.sky.project.share.streaming.analysis.support;

import org.apache.spark.api.java.JavaRDD;

import com.sky.project.share.streaming.analysis.SortByValueFunction;

import scala.Tuple2;

/**
 * AbstractSortByValueFunction
 * 
 * @author zealot
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class AbstractSortByValueFunction<T> implements SortByValueFunction<T> {

	@Override
	public void sortByValue(JavaRDD<T> rdd) {
		rdd.mapToPair(t -> new Tuple2<>(getReduceKey(t), t)).reduceByKey((v1, v2) -> reduce(v1, v2))
				.map(tuple2 -> tuple2).sortBy(tuple -> tuple._2, isAscending(), getNumPartitions());
	}

}
