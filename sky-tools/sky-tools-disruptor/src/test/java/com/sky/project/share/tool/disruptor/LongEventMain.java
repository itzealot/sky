package com.sky.project.share.tool.disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import com.lmax.disruptor.dsl.Disruptor;
import com.sky.project.share.tool.disruptor.factory.LongEventFactory;
import com.sky.project.share.tool.disruptor.handler.LongEventHandler;
import com.sky.project.share.tool.disruptor.message.LongEvent;
import com.sky.project.share.tool.disruptor.producer.LongEventProducer;

public class LongEventMain {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException {
		LongEventFactory factory = new LongEventFactory();

		int bufferSize = 1024;

		Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory, bufferSize,
				Executors.defaultThreadFactory());

		disruptor.handleEventsWith(new LongEventHandler());

		disruptor.start();

		Publisher<ByteBuffer> producer = new LongEventProducer(disruptor.getRingBuffer());

		ByteBuffer buffer = ByteBuffer.allocate(8);
		for (long l = 0; true; l++) {
			buffer.putLong(0, l);
			producer.publish(buffer);
			Thread.sleep(1000);
		}
	}
}