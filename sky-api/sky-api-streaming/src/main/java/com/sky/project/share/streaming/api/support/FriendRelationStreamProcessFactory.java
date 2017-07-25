package com.sky.project.share.streaming.api.support;

import com.sky.project.share.streaming.api.Processor;
import com.sky.project.share.streaming.api.ProcessorFactory;
import com.sky.project.share.streaming.api.StreamKafkaContext;

public class FriendRelationStreamProcessFactory implements ProcessorFactory<String[]> {

	@Override
	public Processor<String[]> getProcessor(StreamKafkaContext<String[]> context) {
		return new FriendRelationStreamProcess(context);
	}

}
