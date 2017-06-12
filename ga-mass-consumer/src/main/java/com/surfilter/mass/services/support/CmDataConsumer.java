package com.surfilter.mass.services.support;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.ImcaptureContext;
import com.surfilter.mass.services.DataConsumer;
import com.surfilter.mass.services.ImcapRunner;

/**
 * 消费者实现类，使用多线程消费
 * 
 * @author zealot
 *
 */
public class CmDataConsumer implements DataConsumer {

	private ImcaptureContext context;
	private ExecutorService executeService;
	private static final int DEFAULT_THREAD_SIZE = 5;
	private Integer threadSize;

	public CmDataConsumer(ImcaptureContext context) {
		this.context = context;
		threadSize = context.getInt(ImcaptureConsts.MATCH_THREAD_SIZE, DEFAULT_THREAD_SIZE);
		this.executeService = Executors.newFixedThreadPool(threadSize);
	}

	@Override
	public void startConsume() {
		for (int i = 0; i < this.threadSize; i++) {
			executeService.execute(new ImcapRunner(this.context));
		}
	}

	@Override
	public void stop() {
		this.executeService.shutdown();
	}

}
