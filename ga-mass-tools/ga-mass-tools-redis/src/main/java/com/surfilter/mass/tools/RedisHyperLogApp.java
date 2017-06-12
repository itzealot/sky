package com.surfilter.mass.tools;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.Constant;
import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.dao.RedisDaoAlias;
import com.surfilter.mass.tools.task.RedisHyperLogTask;
import com.surfilter.mass.tools.util.ParseRelationUtil;
import com.surfilter.mass.tools.util.Threads;

public class RedisHyperLogApp {

	private static final Logger LOG = LoggerFactory.getLogger(RedisHyperLogApp.class);

	public static void main(String[] args) throws Exception {
		MassConfiguration conf = new MassConfiguration();

		String src = conf.get(Constant.READ_SRC_FILE);
		int poolSize = conf.getInt(Constant.PARSE_THREADS, 10);
		int counts = conf.getInt(Constant.READ_SLEEP_COUNTS, 60000);
		long sleep = conf.getInt(Constant.READ_SLEEP_MILLS, 1000);
		int size = conf.getInt(Constant.WRITE_BATCH_SIZE, Constant.REDIS_BATCH_SIZE);

		System.out.println("src file:" + src);
		System.out.println("poolSize:" + poolSize);
		System.out.println("counts:" + counts);
		System.out.println("batchSize:" + size);
		System.out.println("sleep:" + sleep);
		System.out.println("poolSize:" + poolSize);

		RedisDaoAlias.getInstance(conf.get(Constant.WRITE_REDIS_IP) + "|" + conf.get(Constant.WRITE_REDIS_PORT));

		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		File file = new File(src);
		RedisHyperLogTask[] tasks = new RedisHyperLogTask[poolSize];
		ExecutorService executor = Executors.newFixedThreadPool(poolSize);

		for (int i = 0; i < poolSize; i++) {
			tasks[i] = new RedisHyperLogTask(queue, conf);
			executor.submit(tasks[i]);
		}

		File finishDir = null;

		if (file.exists() && file.isFile()) {
			finishDir = new File(file.getParentFile(), "finish");
			if (!finishDir.exists()) {
				try {
					FileUtils.forceMkdir(finishDir);
				} catch (IOException e) {
				}
			}
			ParseRelationUtil.readCertification2RowKey(queue, file, sleep, counts);
			FileUtils.moveFileToDirectory(file, finishDir, true);
		} else if (file.isDirectory()) {
			finishDir = new File(file, "finish");
			if (!finishDir.exists()) {
				try {
					FileUtils.forceMkdir(finishDir);
				} catch (IOException e) {
				}
			}
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					LOG.info("start deal file name:{}", f.getName());
					ParseRelationUtil.readCertification2RowKey(queue, f, sleep, counts);
					LOG.info("finish deal file name:{}", f.getName());
					FileUtils.moveFileToDirectory(f, finishDir, true);
				}
			}
		} else {
			LOG.error("src file error:{}", src);
		}

		while (true) {
			if (!queue.isEmpty()) {
				Threads.sleep(1000);
			} else {
				Threads.sleep(60000);
				break;
			}
		}

		for (int i = 0; i < poolSize; i++) {
			if (tasks[i] != null) {
				tasks[i].shutdown();
			}
		}
		executor.shutdown();

		LOG.info("finish redis certification value exchange...............");
	}
}
