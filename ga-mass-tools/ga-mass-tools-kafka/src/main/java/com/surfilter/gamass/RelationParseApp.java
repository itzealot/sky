package com.surfilter.gamass;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.gamass.conf.Constant;
import com.surfilter.gamass.conf.MassConfiguration;
import com.surfilter.gamass.dao.HBaseDaoAlias;
import com.surfilter.gamass.entity.Relation;
import com.surfilter.gamass.service.RelationParseTask;
import com.surfilter.gamass.util.ParseRelationUtil;
import com.surfilter.mass.tools.util.Threads;

public class RelationParseApp {
	private static final Logger LOG = LoggerFactory.getLogger(RelationParseApp.class);

	public static void main(String[] args) throws Exception {
		MassConfiguration conf = new MassConfiguration();

		String src = conf.get(Constant.READ_SRC_FILE);
		String dst = conf.get(Constant.WRITE_DST_DIR);
		int poolSize = conf.getInt(Constant.PARSE_THREADS, 10);
		int counts = conf.getInt(Constant.READ_SLEEP_COUNTS, 60000);
		int sleep = conf.getInt(Constant.READ_SLEEP_MILLS, 1000);
		String version = conf.get(Constant.VERSION_IS_FXJ);

		if (!"false".equals(version) && !"true".equals(version)) {
			LOG.error("error settings for version.is.fxj property, must be true or flase.");
			throw new IllegalArgumentException("error settings for version.is.fxj property, must be true or flase.");
		}

		System.out.println("src file:" + src);
		System.out.println("dst file:" + dst);
		System.out.println("poolSize:" + poolSize);
		System.out.println("counts:" + counts);
		System.out.println("sleep:" + sleep);
		System.out.println("poolSize:" + poolSize);
		System.out.println("versionIsFxj:" + version);

		HBaseDaoAlias.getInstance(conf.get(Constant.WRITE_HBASE_PARAMS).replaceAll(";", ",").split("\\|"));

		BlockingQueue<Relation> queue = new LinkedBlockingQueue<Relation>();
		File file = new File(src);
		RelationParseTask[] tasks = new RelationParseTask[poolSize];
		ExecutorService executor = Executors.newFixedThreadPool(poolSize);

		for (int i = 0; i < poolSize; i++) {
			tasks[i] = new RelationParseTask(queue, conf);
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
			ParseRelationUtil.read2Relation(queue, file, sleep, counts);
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
					ParseRelationUtil.read2Relation(queue, f, sleep, counts);
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

		LOG.info("finish deal the relation migrate...............");
	}
}
