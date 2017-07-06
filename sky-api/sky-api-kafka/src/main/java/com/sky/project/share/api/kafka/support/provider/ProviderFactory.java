package com.sky.project.share.api.kafka.support.provider;

import com.sky.project.share.api.kafka.SkyKafkaContext;

import kafka.common.TopicAndPartition;

public interface ProviderFactory {

	void register(Provider provider);

	void remove(Provider provider);

	Provider getProvider(SkyKafkaContext context, TopicAndPartition topicAndPartition);
}
