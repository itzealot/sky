package com.surfilter.mass.tools.cli;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SfSysConsts;
import com.surfilter.mass.tools.thread.Relation2SfDataThread;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.Threads;

/**
 * 对关系数据进行解析并生成身份关系数据 .log 和 .log.ok 文件
 * 
 * @author zealot
 *
 */
public class Relation2SfDataCli {
	private static final Logger LOG = LoggerFactory.getLogger(Relation2SfDataCli.class);

	public static void main(String[] args) {
		LOG.info("关系数据转换为身份关系数据处理手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		MassConfiguration conf = new MassConfiguration();
		int poolSize = conf.getInt(SfSysConsts.SF_POOL_SIZE_PROPERTY, 10);
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		Relation2SfDataThread[] threads = new Relation2SfDataThread[poolSize];

		try {
			String source = conf.get(SfSysConsts.SF_SRC_PROPERTY);
			int sleep = conf.getInt(SfSysConsts.SF_SLEEP_PROPERTY, 1000);
			int counts = conf.getInt(SfSysConsts.SF_SLEEP_COUNTS_PROPERTY, 60000);

			List<File> sourcesFiles = FileUtil.getSourceFiles(source);
			for (int i = 0; i < poolSize; i++) {
				threads[i] = new Relation2SfDataThread(queue, conf);
				threadPool.execute(threads[i]);
			}

			for (File file : sourcesFiles) {
				LOG.info("start deal file name: " + file.getName());
				FileUtil.read(queue, file, sleep, counts);
				LOG.info("finish deal file name: " + file.getName());
			}
		} catch (Exception e) {
			LOG.error("关系数据转换为身份关系数据处理手动任务手动任务失败!", e);
		}

		while (!queue.isEmpty()) {
			LOG.info("finish reading all lines and wait for dealing all lines.");
			Threads.sleep(1000);
		}

		for (int i = 0; i < poolSize; i++) {
			if (threads[i] != null)
				threads[i].stop();
		}
		threadPool.shutdown();
	}

}
