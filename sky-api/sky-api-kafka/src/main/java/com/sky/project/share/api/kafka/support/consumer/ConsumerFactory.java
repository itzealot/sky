package com.sky.project.share.api.kafka.support.consumer;

import com.sky.project.share.api.kafka.SkyKafkaContext;

/**
 * ConsumerFactory
 * 
 * @author zealot
 *
 */
public interface ConsumerFactory {

	Consumer getConsumer(SkyKafkaContext context);
}
