package com.surfilter.mass.tools.cli;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.dao.RedisDao;
import com.surfilter.mass.tools.hbase.HbaseClient;
import com.surfilter.mass.tools.thread.FileRelation2HbaseThread;
import com.surfilter.mass.tools.util.FileUtil;

/**
 * 从文件中的relation记录导入 hbase
 * 
 * @author zealot
 *
 */
public class FileRelation2HbaseCli {
	private static final Logger LOG = LoggerFactory.getLogger(FileRelation2HbaseCli.class);

	public static void main(String[] args) {
		LOG.info("从文件中读取relation记录到hbase手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		MassConfiguration massConfiguration = new MassConfiguration();
		int poolSize = massConfiguration.getInt("copy.relation.to.hbase.pool.size", 10);
		// 根据线程池大小创建线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		int queueSizeLimit = massConfiguration.getInt("copy.relation.to.hbase.queue.size.limit", 20000);
		int sleep = massConfiguration.getInt("copy.relation.to.hbase.sleep", 1000);

		FileRelation2HbaseThread[] threads = new FileRelation2HbaseThread[poolSize];

		String tableName = massConfiguration.get("copy.relation.to.hbase.table");
		String fileName = massConfiguration.get("copy.relation.to.hbase.source.file");

		String dir = massConfiguration.get("copy.relation.to.hbase.target.dir");
		String filter = "\",\\,/,',>,<,|,?, ,=,+,[,],{,},%,;,&,^,!,(,)";

		HbaseClient hbase = new HbaseClient(tableName);
		RedisDao redisDao = RedisDao.getInstance(massConfiguration.get("redis.conf"));

		for (int i = 0; i < poolSize; i++) {
			threads[i] = new FileRelation2HbaseThread(queue, hbase, redisDao, dir, filter);
			threadPool.execute(threads[i]);
		}

		try {
			FileUtil.read(queue, new File(fileName), sleep, queueSizeLimit);
		} catch (Exception e) {
			LOG.error("读取文件中的relation记录到hbase手动任务任务失败!", e);
		}

		while (!queue.isEmpty()) {
			Threads.sleep(2000);
			LOG.info("wait to finish dealing with the queue........");
		}

		// close the thread and the pool shut down
		for (int i = 0; i < poolSize; i++) {
			threads[i].close();
		}
		threadPool.shutdown();
	}
}
