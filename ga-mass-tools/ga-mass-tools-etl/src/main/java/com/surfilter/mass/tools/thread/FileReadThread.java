package com.surfilter.mass.tools.thread;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SysConsts;
import com.surfilter.mass.tools.util.FileUtil;

/**
 * 文件读取线程
 * 
 * @author zealot
 *
 */
public class FileReadThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(FileReadThread.class);

	private BlockingQueue<String> queue;
	private File file;
	private MassConfiguration conf;

	public FileReadThread(MassConfiguration conf, File file) {
		this.file = file;
		this.conf = conf;
	}

	@Override
	public void run() {
		try {
			FileUtil.read(queue, file, conf.getInt(SysConsts.DATA_MAIN_SLEEP_PROPERTY),
					conf.getInt(SysConsts.DATA_MAIN_SLEEP_COUNTS_PROPERTY));
		} catch (Exception e) {
			LOG.error("error read file into queue, fileName:{}, {}", file.getName(), e);
		}
	}

}
