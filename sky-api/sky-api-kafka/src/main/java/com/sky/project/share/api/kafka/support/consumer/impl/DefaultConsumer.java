package com.sky.project.share.api.kafka.support.consumer.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sky.project.share.api.kafka.SkyKafkaConsts;
import com.sky.project.share.api.kafka.SkyKafkaContext;
import com.sky.project.share.api.kafka.support.consumer.Consumer;
import com.sky.project.share.api.kafka.support.consumer.AbstractKafkaMessageConsumer;
import com.sky.project.share.common.thread.pool.NamedThreadFactory;

/**
 * DefaultConsumer
 * 
 * @author zealot
 *
 */
public class DefaultConsumer implements Consumer {

	private final ExecutorService executors;

	public DefaultConsumer(SkyKafkaContext context) {
		int nums = context.getInt(SkyKafkaConsts.CONSUMER_NUM, SkyKafkaConsts.DEFAULT_CONSUMER_NUM);
		this.executors = Executors.newFixedThreadPool(nums, new NamedThreadFactory("SkyKafkaConsumer", true));
	}

	@Override
	public void execute(AbstractKafkaMessageConsumer task) {
		if (executors != null) {
			executors.execute(task);
		}
	}

	@Override
	public void close() {
		if (this.executors != null) {
			this.executors.shutdown();
		}
	}
}
