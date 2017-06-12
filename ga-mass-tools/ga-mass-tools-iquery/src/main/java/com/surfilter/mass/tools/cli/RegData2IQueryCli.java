package com.surfilter.mass.tools.cli;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.thread.RegDataParseThread;
import com.surfilter.mass.tools.util.FileUtil;

public class RegData2IQueryCli {
	private static final Logger LOG = LoggerFactory.getLogger(RegData2IQueryCli.class);

	public static void main(String[] args) {
		LOG.info("注册数据入iQuery处理手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		MassConfiguration massConfiguration = new MassConfiguration();
		int poolSize = massConfiguration.getInt("reg.data.into.iquery.pool.size", 10);

		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		RegDataParseThread[] threads = new RegDataParseThread[poolSize];

		try {
			String source = massConfiguration.get("reg.data.into.iquery.source.dir");
			String tableName = massConfiguration.get("reg.data.into.iquery.table");
			int sleep = massConfiguration.getInt("reg.data.into.iquery.sleep", 1000);
			int counts = massConfiguration.getInt("reg.data.into.iquery.sleep.counts", 50000);

			List<File> sourcesFiles = FileUtil.getSourceFiles(source);
			for (int i = 0; i < poolSize; i++) {
				threads[i] = new RegDataParseThread(queue, tableName);
				threadPool.execute(threads[i]);
			}
			LOG.info("all files:" + sourcesFiles);
			for (File file : sourcesFiles) {
				LOG.info("start deal file name: " + file.getName());
				FileUtil.read(queue, file, sleep, counts);
				LOG.info("finish deal file name: " + file.getName());
			}

			while (!queue.isEmpty()) {
				LOG.info("finish reading all lines from source files and wait for dealing all lines.");
				Threads.sleep(1000);
			}
		} catch (Exception e) {
			LOG.error("注册数据入iQuery手动任务任务失败!", e);
		} finally {
			for (int i = 0; i < poolSize; i++) {
				if (threads[i] != null)
					threads[i].stop();
			}
			threadPool.shutdown();
		}
	}

}
