package com.surfilter.mass.tools.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.surfilter.mass.tools.util.IQueryUtil;

public class DataParseThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(DataParseThread.class);

	public static final int BATCH_SIZE = 5000;
	private volatile boolean running = true;
	private BlockingQueue<String> queue;
	private IQueryUtil util;
	private int filedSize;
	private String spliter;

	public DataParseThread(BlockingQueue<String> queue, IQueryUtil util, int filedSize, String spliter) {
		this.queue = queue;
		this.util = util;
		this.filedSize = filedSize;
		this.spliter = spliter;
	}

	@Override
	public void run() {
		List<String> lines = new ArrayList<>(BATCH_SIZE);
		List<List<String>> dataLLists = new ArrayList<>(BATCH_SIZE);

		while (running) {
			if (!queue.isEmpty()) {
				queue.drainTo(lines, BATCH_SIZE);

				if (!lines.isEmpty()) {
					for (String line : lines) {
						doParseLine(line, dataLLists);
					}
					util.batch(dataLLists);
					dataLLists.clear();
					lines.clear();
				}
			} else {
				Threads.sleep(200);
			}
		}
		LOG.info("thread stop execute........................");
	}

	private void doParseLine(String line, List<List<String>> dataLLists) {
		Iterable<String> it = Splitter.on(spliter).trimResults().split(line);
		int index = 0;
		String[] fileds = new String[filedSize];

		try {
			for (String filed : it) {
				if (index >= filedSize) {
					index++;
					break;
				}
				if (filed == null || "".equals(filed) || "\\N".equals(filed)) {
					fileds[index] = "MULL";
				} else {
					fileds[index] = filed;
				}
				index++;
			}

			if (index == filedSize) {
				dataLLists.add(Arrays.asList(fileds));
			} else {
				LOG.error("error line: " + line);
			}
		} catch (Exception e) {
			LOG.error("error line : " + line, e);
		}
	}

	public void stop() {
		this.running = false;
		util.close();
		util = null;
	}

}
