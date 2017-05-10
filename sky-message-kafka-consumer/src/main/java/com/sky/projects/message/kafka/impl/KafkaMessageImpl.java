package com.sky.projects.message.kafka.impl;

import com.sky.projects.message.function.Function;

/**
 * Kafka message impl
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class KafkaMessageImpl implements Function<byte[], String> {

	@Override
	public String call(byte[] message) {
		return new String(message);
	}

}
