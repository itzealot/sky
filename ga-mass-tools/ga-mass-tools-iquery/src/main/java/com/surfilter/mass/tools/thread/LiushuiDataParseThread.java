package com.surfilter.mass.tools.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.surfilter.mass.tools.util.DateUtil;
import com.surfilter.mass.tools.util.IQueryUtil;

public class LiushuiDataParseThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(LiushuiDataParseThread.class);
	public static final int BATCH_SIZE = 5000;
	public static final int FILED_SIZE = 40;
	private volatile boolean running = true;
	private BlockingQueue<String> queue;
	private IQueryUtil util;
	private boolean flag = false;
	private static final int PAY_TIME = 17;
	private static final int PAY_TIME_P = FILED_SIZE - 1;
	private static final int PAY_MONEY = 16;
	private static final int SOURCE = 2;

	public LiushuiDataParseThread(BlockingQueue<String> queue, String tableName, boolean flag) {
		this.queue = queue;
		util = new IQueryUtil(tableName);
		this.flag = flag;
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

			if (flag) {// is shunfeng data, copy data [2, FILED_SIZE - 2]
				for (int i = FILED_SIZE - 2; i >= SOURCE; i--) {
					fileds[i + 1] = fileds[i];
				}
				fileds[SOURCE] = "1001";
				index++;
			}

			if (index == FILED_SIZE) {
				try {// PAY_MONEY
					float money = Float.parseFloat(fileds[PAY_MONEY].trim());
					if (money >= 1000000000.0f) {
						LOG.error("error pay_money line :" + line);
					}
				} catch (Exception e) {
				}

				subAndTrim(fileds);

				dataLLists.add(Arrays.asList(fileds));
			} else {
				LOG.error("error parse line:" + line);
			}
		} catch (Exception e) {
			LOG.error("error index line :" + line, e);
		}
	}

	private void subAndTrim(String[] fileds) {
		List<Integer> limits = Arrays.asList(

				128, 64, 0, 0, 32,

				256, 32, 256, 256, 256,

				32, 256, 128, 512, 256,

				128, 0, 128, 256, 128,

				1024, 512, 128, 1024, 0,

				1024, 128, 32, 64, 32,

				32, 256, 0, 0, 128,

				128, 256, 256, 0, 0);

		// sub
		for (int i = 0; i < FILED_SIZE; i++) {
			if (limits.get(i) == 32 && fileds[i].getBytes().length > 32) {
				fileds[i] = fileds[i].substring(0, 21);
				continue;
			}

			if (limits.get(i) > 32 && fileds[i].getBytes().length > limits.get(i)) {
				fileds[i] = fileds[i].substring(0, limits.get(i) / 3);
			}
		}

		try {
			// pay_time deal
			String src = fileds[PAY_TIME];
			int index = src.indexOf('.');
			if (index != -1) {
				src = src.substring(0, index);
				fileds[PAY_TIME] = src;
			}

			// pay_time_p deal, has pay time then deal
			fileds[PAY_TIME_P] = DateUtil.date2Str(fileds[PAY_TIME]);
		} catch (Exception e) {
		}

		if ("".equals(fileds[PAY_TIME_P]) || "__HIVE_DEFAULT_PARTITION__".equals(fileds[PAY_TIME_P].trim())
				|| "NULL".equals(fileds[PAY_TIME_P])) {
			fileds[PAY_TIME_P] = "20160816000000";
		}
	}

	public void stop() {
		this.running = false;
		util = null;
	}
}
