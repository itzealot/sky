package com.sky.project.share.streaming.api.support;

import com.sky.project.share.streaming.api.MessageParser;
import com.sky.project.share.streaming.api.StreamKafkaContext;

/**
 * FriendRelation 抽象的流式处理类
 * 
 * @author zealot
 */
@SuppressWarnings("serial")
public class FriendRelationStreamProcess extends AbstractStreamProcess {

	public FriendRelationStreamProcess(StreamKafkaContext<String[]> context) {
		super(context);
	}

	@Override
	protected void persist(MessageParser parser) {
		persistFriendRelation(parser);
	}

	private void persistFriendRelation(MessageParser parser) {
		// TODO
	}

}
