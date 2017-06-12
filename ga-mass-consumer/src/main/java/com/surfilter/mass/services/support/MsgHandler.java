package com.surfilter.mass.services.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;

/**
 * @author zealot
 *
 */
public class MsgHandler implements EventHandler<KafkaMsg> {
	
	private static Logger LOG = LoggerFactory.getLogger(MsgHandler.class);
	
	@Override
	public void onEvent(KafkaMsg msg, long seq, boolean flag) throws Exception {
		LOG.info(Thread.currentThread().getId() + ", MsgHandler: " + msg.getMsg());
	}
	
}
