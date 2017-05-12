package com.surfilter.mass.services.support;

import com.lmax.disruptor.RingBuffer;
import com.surfilter.mass.services.MessageExecutor;

/**
 * 消息发布者实现类
 * 
 * @author hapuer
 *
 */
public class DisruptorMsgExecutor implements MessageExecutor {

	private final RingBuffer<KafkaMsg> ringBuffer;

	public DisruptorMsgExecutor(RingBuffer<KafkaMsg> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	@Override
	public void execute(String message) {
		long sequence = ringBuffer.next();

		try {
			// Get the entry in the Disruptor
			ringBuffer.get(sequence).setMsg(message);
		} finally {
			ringBuffer.publish(sequence);
		}
	}

}
