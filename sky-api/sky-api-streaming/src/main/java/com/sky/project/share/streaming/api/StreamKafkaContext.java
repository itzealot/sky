package com.sky.project.share.streaming.api;

import java.io.Serializable;
import java.util.Map;

import org.apache.spark.streaming.api.java.JavaDStream;

/**
 * StreamKafkaContext
 * 
 * @author zealot
 * @param <T>
 * @param <V>
 */
public interface StreamKafkaContext<T> extends Serializable {

	/**
	 * get Stream
	 * 
	 * @return
	 */
	JavaDStream<T> getStream();

	/**
	 * get parameters map
	 * 
	 * @return
	 */
	Map<String, Object> getParams();

	/**
	 * 
	 * @return
	 */
	Map<String, String> getCertProtocolMap();

	Map<String, String> getMacHasCompanyMap();

}
