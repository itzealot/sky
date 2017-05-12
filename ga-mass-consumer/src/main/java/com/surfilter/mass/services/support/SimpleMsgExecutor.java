package com.surfilter.mass.services.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.services.MessageExecutor;

/**
 * 获取消息并放入阻塞队列实现类
 * 
 * @author hapuer
 *
 */
public class SimpleMsgExecutor implements MessageExecutor {

	private static Logger LOG = LoggerFactory.getLogger(SimpleMsgExecutor.class);

	private BlockingQueue<String> blockQueue;

	public SimpleMsgExecutor(BlockingQueue<String> blockQueue) {
		checkNotNull(blockQueue, "blockQueue can't be null");

		this.blockQueue = blockQueue;
	}

	@Override
	public void execute(String message) {
		try {
			this.blockQueue.put(message);
		} catch (InterruptedException e) {
			LOG.error("put into BlockingQueue fail.", e);
		}
	}

}
