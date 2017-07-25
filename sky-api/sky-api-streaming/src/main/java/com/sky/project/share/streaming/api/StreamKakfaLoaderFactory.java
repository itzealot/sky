package com.sky.project.share.streaming.api;

import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.broadcast.Broadcast;

/**
 * StreamKakfaLoaderFactory
 * 
 * @param <T>
 * @author zealot
 */
public interface StreamKakfaLoaderFactory<T> {

	/**
	 * getLoader
	 * 
	 * @param conf
	 * @param broadcastRedisMap
	 * @param broadcastMap
	 * @param streamingType
	 * @return
	 */
	StreamKakfaLoader<T> getLoader(SparkConf conf, Map<String, Broadcast<Map<String, String>>> broadcastRedisMap,
			Map<String, Broadcast<String>> broadcastMap);
}
