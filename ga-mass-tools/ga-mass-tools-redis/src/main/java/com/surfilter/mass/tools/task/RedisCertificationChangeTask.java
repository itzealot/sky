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
public class RedisCertificationChangeTask implements Runnable {

	private volatile boolean running = true;

	private BlockingQueue<String> queue;
	private MassConfiguration conf;

	public RedisCertificationChangeTask(BlockingQueue<String> queue, MassConfiguration conf) {
		this.queue = queue;
		this.conf = conf;
	}

	@Override
	public void run() {
		String serversInfo = this.conf.get(Constant.WRITE_REDIS_IP) + "|" + this.conf.get(Constant.WRITE_REDIS_PORT);
		int size = this.conf.getInt(Constant.WRITE_BATCH_SIZE, Constant.REDIS_BATCH_SIZE);
		int prefixLen = this.conf.getInt(Constant.MAC_MODIFY_PREFIX_LENGTH, 6);

		while (running) {
			if (!queue.isEmpty()) {
				List<String> values = new ArrayList<>(size);
				queue.drainTo(values, size);

				if (!values.isEmpty()) {
					RedisUtil.modifyCertificationSaveModel(values, serversInfo, prefixLen);
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
