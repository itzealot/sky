package com.surfilter.mass.tools.task.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.JdbcUtils;
import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.task.Task;
import com.surfilter.mass.tools.util.Threads;

/**
 * 消费者
 * 
 * @author zealot
 */
public class ConsumerMsgTask implements Task {

	static Logger LOG = LoggerFactory.getLogger(ConsumerMsgTask.class);

	private volatile boolean running = true;
	private BlockingQueue<String[]> queue;
	private MassConfiguration conf;

	public ConsumerMsgTask(BlockingQueue<String[]> queue, MassConfiguration conf) {
		this.queue = queue;
		this.conf = conf;
	}

	@Override
	public void run() {
		while (running) {
			if (queue.isEmpty()) {
				Threads.sleep(50);
				continue;
			}

			List<String[]> msgs = new ArrayList<>(JdbcUtils.BATCH_SIZE);
			queue.drainTo(msgs, JdbcUtils.BATCH_SIZE);

			if (!msgs.isEmpty()) {
				doConsumer(msgs);
				msgs.clear();
			}

			msgs = null;
		}
	}

	/**
	 * 消费
	 * 
	 * @param msgs
	 */
	private void doConsumer(List<String[]> msgs) {
		// TODO
	}

	@Override
	public void shutdown() {
		this.running = false;
	}

	public MassConfiguration getConf() {
		return conf;
	}
}