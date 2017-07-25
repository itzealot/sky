package com.sky.project.share.streaming.api;

import java.io.Serializable;
import java.util.Map;

import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * StreamKakfaLoader
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface StreamKakfaLoader<T> extends Serializable {

	/**
	 * load kafka stream
	 * 
	 * @param jssc
	 * @param kafkaParams
	 * @param topic
	 * @param partionAndOffset
	 * @param converter
	 * @return
	 */
	StreamKafkaContext<T> kafkaLoad(JavaStreamingContext jssc, Map<String, String> kafkaParams, String topic,
			Map<Integer, Long> partionAndOffset, MessageConverter<T> converter);
}
