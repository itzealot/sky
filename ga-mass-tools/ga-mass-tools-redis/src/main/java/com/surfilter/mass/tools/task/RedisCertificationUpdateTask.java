package com.surfilter.mass.tools.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.surfilter.mass.tools.conf.Constant;
import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.util.RedisUtil;
import com.surfilter.mass.tools.util.Threads;

/**
 * redis certification update task
 * 
 * @author zealot
 *
 */
public class RedisCertificationUpdateTask implements Runnable {

	private volatile boolean running = true;

	private BlockingQueue<String> queue;
	private MassConfiguration conf;

	public RedisCertificationUpdateTask(BlockingQueue<String> queue, MassConfiguration conf) {
		this.queue = queue;
		this.conf = conf;
	}

	@Override
	public void run() {
		String serversInfo = this.conf.get(Constant.WRITE_REDIS_IP) + "|" + this.conf.get(Constant.WRITE_REDIS_PORT);
		int size = this.conf.getInt(Constant.WRITE_BATCH_SIZE, Constant.REDIS_BATCH_SIZE);

		while (running) {
			if (!queue.isEmpty()) {
				List<String> values = new ArrayList<>(size);
				queue.drainTo(values, size);

				if (!values.isEmpty()) {
					RedisUtil.changeCertificationOnlyTimesByRowKey(values, serversInfo);
					values.clear();
				}

				values = null;
			} else {
				Threads.sleep(200);
			}
		}
	}

	public void shutdown() {
		running = false;
	}
}
