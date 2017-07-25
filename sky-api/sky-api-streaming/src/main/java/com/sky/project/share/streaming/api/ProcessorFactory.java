package com.sky.project.share.streaming.api;

/**
 * ProcessorFactory
 * 
 * @param <T>
 * @author zealot
 */
public interface ProcessorFactory<T> {

	/**
	 * getProcessor
	 * 
	 * @param context
	 * @return
	 */
	Processor<T> getProcessor(StreamKafkaContext<T> context);

}
