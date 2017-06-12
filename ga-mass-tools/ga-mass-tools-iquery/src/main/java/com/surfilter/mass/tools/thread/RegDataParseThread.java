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

public class RegDataParseThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(RegDataParseThread.class);
	public static final int BATCH_SIZE = 5000;
	public static final int FILED_SIZE = 32;
	private volatile boolean running = true;
	private BlockingQueue<String> queue;
	private IQueryUtil util;

	public RegDataParseThread(BlockingQueue<String> queue, String tableName) {
		this.queue = queue;
		util = new IQueryUtil(tableName);
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
		Iterable<String> it = Splitter.on('\t').trimResults().split(line);
		int index = 0;
		String[] fileds = new String[FILED_SIZE];

		try {
			for (String filed : it) {
				if (index >= FILED_SIZE) {
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

			if (index == FILED_SIZE) {
				subAndTrim(fileds);
				dataLLists.add(Arrays.asList(fileds));
			} else {
				LOG.error("error line: " + line);
			}
		} catch (Exception e) {
			LOG.error("error line : " + line, e);
		}
	}

	private void subAndTrim(String[] fileds) {
		List<Integer> limits = Arrays.asList(

				256, 0, 32, 256, 256,

				0, 128, 32, 64, 0,

				0, 32, 256, 256, 128,

				128, 32, 0, 0, 256,

				0, 0, 0, 0, 0,

				256, 512, 128, 0, 1024,

				0, 0);

		// sub
		for (int i = 0; i < FILED_SIZE; i++) {
			if (limits.get(i) == 32 && fileds[i].getBytes().length > 32) {
				fileds[i] = fileds[i].substring(0, 21);
			}

			if (limits.get(i) > 32 && fileds[i].getBytes().length > limits.get(i)) {
				fileds[i] = fileds[i].substring(0, limits.get(i) / 3);
			}
		}
	}

	public void stop() {
		this.running = false;
		if (util != null)
			util.close();
	}
}
