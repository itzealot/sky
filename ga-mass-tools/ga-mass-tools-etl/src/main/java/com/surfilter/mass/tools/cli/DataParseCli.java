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
import com.surfilter.mass.tools.conf.SysConsts;
import com.surfilter.mass.tools.entity.KaiKaData;
import com.surfilter.mass.tools.thread.AbstractDataTransferThread;
import com.surfilter.mass.tools.thread.KaiKaDataTransferThread;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.Threads;

/**
 * 数据进行解析并生成 .log 和 .log.ok 文件
 * 
 * @author zealot
 *
 */
public abstract class DataParseCli {
	private static final Logger LOG = LoggerFactory.getLogger(DataParseCli.class);

	public static void main(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("args error.");
		}

		String type = args[0];
		int poolSize = 10;
		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		MassConfiguration conf = new MassConfiguration();
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
		AbstractDataTransferThread<KaiKaData>[] threads = null;

		switch (type.toLowerCase()) {
		case "kaika":
			LOG.info("开卡数据处理手动任务开始,在配置文件conf/conf.properties中修改配置参数...");
			threads = new KaiKaDataTransferThread[poolSize];
			for (int i = 0; i < poolSize; i++) {
				threads[i] = new KaiKaDataTransferThread(queue, conf);
				threadPool.submit(threads[i]);
			}
			break;
		}

		try {
			String source = conf.get(SysConsts.DATA_SRC_PROPERTY);

			List<File> sourcesFiles = FileUtil.getSourceFiles(source);

			for (File file : sourcesFiles) {
				LOG.info("start deal file name: " + file.getName());
				FileUtil.read(queue, file, conf.getInt(SysConsts.DATA_MAIN_SLEEP_PROPERTY, 1000),
						conf.getInt(SysConsts.DATA_MAIN_SLEEP_COUNTS_PROPERTY, 60000));
				LOG.info("finish deal file name: " + file.getName());
			}
		} catch (Exception e) {
			LOG.error("数据处理手动任务手动任务失败!", e);
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
