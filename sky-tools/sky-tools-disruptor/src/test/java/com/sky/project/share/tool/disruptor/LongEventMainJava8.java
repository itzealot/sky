package com.sky.project.share.tool.disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.sky.project.share.tool.disruptor.message.LongEvent;

/**
 * 用lambda表达式来注册EventHandler和EventProductor
 * 
 * @author zealot
 *
 */
public class LongEventMainJava8 {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException {
		// Executor that will be used to construct new threads for consumers
		ThreadFactory threadFactory = Executors.defaultThreadFactory();

		// Specify the size of the ring buffer, must be power of 2.
		int bufferSize = 1024;

		// Construct the Disruptor
		Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, bufferSize, threadFactory);

		// 可以使用lambda来注册一个EventHandler
		disruptor.handleEventsWith((event, sequence, endOfBatch) -> System.out.println("Event: " + event.getValue()));

		// Start the Disruptor, starts all threads running
		disruptor.start();
		// Get the ring buffer from the Disruptor to be used for publishing.
		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

		// LongEventProducer producer = new LongEventProducer(ringBuffer);

		ByteBuffer bb = ByteBuffer.allocate(8);
		for (long l = 0; true; l++) {
			bb.putLong(0, l);
			ringBuffer.publishEvent((event, sequence, buffer) -> event.setValue(buffer.getLong(0)), bb);
			Thread.sleep(1000);
		}
	}
}