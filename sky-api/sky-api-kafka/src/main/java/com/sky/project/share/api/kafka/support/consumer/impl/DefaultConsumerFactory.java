package com.sky.project.share.api.kafka.support.consumer.impl;

import com.sky.project.share.api.kafka.SkyKafkaContext;
import com.sky.project.share.api.kafka.support.consumer.Consumer;
import com.sky.project.share.api.kafka.support.consumer.ConsumerFactory;

/**
 * DefaultConsumerFactory
 * 
 * @author zealot
 *
 */
public class DefaultConsumerFactory implements ConsumerFactory {

	@Override
	public Consumer getConsumer(SkyKafkaContext context) {
		return new DefaultConsumer(context);
	}

}
