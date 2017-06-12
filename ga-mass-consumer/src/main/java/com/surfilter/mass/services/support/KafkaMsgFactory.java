package com.surfilter.mass.services.support;

import com.lmax.disruptor.EventFactory;

/**
 * @author zealot
 *
 */
public class KafkaMsgFactory implements EventFactory<KafkaMsg> {

	@Override
	public KafkaMsg newInstance() {
		return new KafkaMsg();
	}

}
