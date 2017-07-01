package com.sky.project.share.tool.disruptor.factory;

import com.lmax.disruptor.EventFactory;
import com.sky.project.share.tool.disruptor.message.LongEvent;

/**
 * LongEventFactory
 * 
 * @author zealot
 *
 */
public class LongEventFactory implements EventFactory<LongEvent> {

	@Override
	public LongEvent newInstance() {
		return new LongEvent();
	}

}