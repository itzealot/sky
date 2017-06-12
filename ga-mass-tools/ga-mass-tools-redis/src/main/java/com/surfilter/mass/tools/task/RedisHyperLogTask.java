package com.surfilter.mass.tools.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.Constant;
import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.dao.RedisDaoAlias;
import com.surfilter.mass.tools.util.FileUtils;
import com.surfilter.mass.tools.util.Threads;

/**
 * redis certification update task
 * 
 * @author zealot
 *
 */
public class RedisHyperLogTask implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(RedisHyperLogTask.class);

	private volatile boolean running = true;

	private BlockingQueue<String> queue;
	private MassConfiguration conf;

	public RedisHyperLogTask(BlockingQueue<String> queue, MassConfiguration conf) {
		this.queue = queue;
		this.conf = conf;
	}

	@Override
	public void run() {
		String serversInfo = this.conf.get(Constant.WRITE_REDIS_IP) + "|" + this.conf.get(Constant.WRITE_REDIS_PORT);
		int size = this.conf.getInt(Constant.WRITE_BATCH_SIZE, Constant.REDIS_BATCH_SIZE);

		String dstFile = this.conf.get(Constant.WRITE_DST_DIR);
		String redisKey = "certification_20170419";

		while (running) {
			if (!queue.isEmpty()) {
				List<String> values = new ArrayList<>(size);
				queue.drainTo(values, size);

				if (!values.isEmpty()) {
					List<Boolean> exists = RedisDaoAlias.getInstance(serversInfo).pfadds(redisKey, values);
					FileUtils.write(new File(dstFile), values, exists);
					LOG.debug("finish write certification(key|value) into file, size:{}", values.size());
					values.clear();
					exists.clear();
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
