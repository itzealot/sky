package com.sky.projects.message;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executor run task
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class SkyExecutor implements SkyRunnable {
	private static Logger LOG = LoggerFactory.getLogger(SkyExecutor.class);

	private Queue<SkyRunnable> queue = new LinkedBlockingQueue<>();

	@Override
	public void run() {
		try {
			while (true) {
				if (queue.isEmpty()) {
					this.wait();
				}

				SkyRunnable runnable = queue.poll();

				if (runnable != null) {
					runnable.run();
				}
			}
		} catch (Throwable e) {
			LOG.error("run task error.", e);
		}
	}

	public synchronized void submit(SkyRunnable runnable) {
		if (runnable != null) {
			queue.add(runnable);
			this.notifyAll();
		} else {
			LOG.warn("error runnable set for running, please to check.");
		}
	}
}
