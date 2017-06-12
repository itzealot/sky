package com.surfilter.mass.tools.task.impl;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.task.Task;

/**
 * 爬虫程序
 * 
 * @author zealot
 *
 */
public class ReptileTask implements Task {

	static Logger LOG = LoggerFactory.getLogger(ReptileTask.class);

	private BlockingQueue<String[]> queue;
	private MassConfiguration conf;

	public ReptileTask(BlockingQueue<String[]> queue, MassConfiguration conf) {
		this.queue = queue;
		this.conf = conf;
	}

	@Override
	public void run() {
		// TODO 根据配置信息启动爬虫程序，抓取url中的数据并返回 String[]
	}

	@Override
	public void shutdown() {
	}

	public MassConfiguration getConf() {
		return conf;
	}

	public BlockingQueue<String[]> getQueue() {
		return queue;
	}
}
