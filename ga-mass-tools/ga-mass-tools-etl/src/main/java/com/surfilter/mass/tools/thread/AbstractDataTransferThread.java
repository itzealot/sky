package com.surfilter.mass.tools.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.surfilter.mass.tools.Factory;
import com.surfilter.mass.tools.Parser;
import com.surfilter.mass.tools.conf.SfSysConsts;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.Filter;
import com.surfilter.mass.tools.util.Threads;

/**
 * 抽象的数据转换类
 * 
 * @author zealot
 *
 * @param <T>
 */
public abstract class AbstractDataTransferThread<T> implements Runnable, Parser<String>, Factory<T, String[]> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDataTransferThread.class);

	protected static AtomicInteger allCounts = new AtomicInteger(0);
	protected List<String> lines = new ArrayList<>(SfSysConsts.DEFAULT_BUFFER_SIZE);
	protected List<T> datas = new ArrayList<T>(SfSysConsts.DEFAULT_BUFFER_SIZE);

	private volatile boolean running = true;
	private BlockingQueue<String> queue;

	protected AbstractDataTransferThread(BlockingQueue<String> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		while (running) {
			if (!queue.isEmpty()) {
				queue.drainTo(lines, SfSysConsts.DEFAULT_BUFFER_SIZE);
				doParse();
				this.lines.clear();
			} else {
				Threads.sleep(200);
			}
		}

		LOG.info("finish parse data........................");
	}

	private void doParse() {
		if (lines.isEmpty()) {
			return;
		}

		for (String line : lines) {
			String[] arrays = this.parse(line);
			T obj = null;

			if (arrays != null)
				obj = this.newInstance(arrays);

			if (obj != null)
				datas.add(obj);
		}

		FileUtil.writeWithJson(this.getPath(), datas, allCounts);

		if (!this.datas.isEmpty())
			this.datas.clear();
	}

	@Override
	public String[] parse(String line) {
		Iterable<String> it = Splitter.on(this.getSpliter()).trimResults().split(line);
		String[] arrays = new String[this.getFiledSize()];
		int index = 0;

		for (String filed : it) {
			if (index >= this.getFiledSize()) {
				index++;
				break;
			}
			arrays[index++] = Filter.trimNULL(filed);
		}

		if (this.getFiledSize() != index) {
			LOG.error("error Parse:");
			return null;
		}

		return arrays;
	}

	public void stop() {
		this.running = false;
	}

	/**
	 * 获取json存储路径
	 * 
	 * @return
	 */
	public abstract String getPath();

	public abstract String getSpliter();

	public abstract int getFiledSize();
}
