package com.surfilter.mass.tools.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.hadoop.hbase.util.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.surfilter.mass.tools.dao.RedisDao;
import com.surfilter.mass.tools.entity.MysqlRelation;
import com.surfilter.mass.tools.hbase.HbaseClient;
import com.surfilter.mass.tools.util.CertificationFilter;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.ParseLineUtil;

public class FileRelation2HbaseThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(FileRelation2HbaseThread.class);
	public static final int BATCH_SIZE = 2000;
	public static final String SPLITER = "|";

	private BlockingQueue<String> queue;
	private List<MysqlRelation> relations = new ArrayList<>(BATCH_SIZE);
	private List<String> lines = new ArrayList<>(BATCH_SIZE);
	private List<String> fileds = new ArrayList<>();
	private volatile boolean running = true;
	private HbaseClient hbase;
	private RedisDao redisDao;

	// to use get the mysql's info
	private String redisKey = "relation";
	private List<String> hashKeyList = new ArrayList<>();
	private List<String> hashValues = new ArrayList<>();

	// 错误数据
	private static BlockingQueue<String> errorQueue = new LinkedBlockingQueue<String>();
	// 错误数据缓冲
	private List<String> errorLines = new ArrayList<>(10000);

	private String dir;
	private String[] filters;

	public FileRelation2HbaseThread(BlockingQueue<String> queue, HbaseClient hbase, RedisDao redisDao, String dir,
			String filter) {
		Preconditions.checkNotNull(queue, "queue must not be null");
		Preconditions.checkNotNull(dir, "dir must not be null");
		Preconditions.checkNotNull(filter, "filter must not be null");

		this.queue = queue;
		this.hbase = hbase;
		this.redisDao = redisDao;
		this.dir = dir;

		new File(dir).mkdirs();

		this.filters = filter.split(",");
	}

	@Override
	public void run() {
		while (running) {
			if (queue.isEmpty()) {
				writeDataLines();
				Threads.sleep(200);
			} else {
				queue.drainTo(lines, BATCH_SIZE);

				if (lines.isEmpty()) {
					return;
				}

				parseLines();

				loadRedisDataAndSet();

				hbase.put(relations);
				relations.clear();
			}
		}

		writeDataLines();
		LOG.info("stop................................");
	}

	private void writeDataLines() {
		errorQueue.drainTo(errorLines, 10000);
		if (!errorLines.isEmpty()) {
			FileUtil.write(new File(dir + "/error-relation.txt"), errorLines);
			errorLines.clear();
		}
	}

	/**
	 * 从redis 中加载身份数据
	 */
	private void loadRedisDataAndSet() {
		redisDao.hget(redisKey, hashKeyList, hashValues);

		for (int i = 0, len = hashValues.size(); i < len; i++) {
			MysqlRelation relation = relations.get(i);
			String value = hashValues.get(i);

			try {
				String[] values = value.split("\\|");
				int lenValues = values.length;

				if (lenValues >= 1) {// 有 DiscoverTimes
					relation.setDiscoverTimes(values[0]);
				}

				if (lenValues >= 2) {// 有 TerminalNum
					relation.setLastTerminalNum(values[1]);
				}

				if (lenValues >= 3) {// 有 LastStartTime
					relation.setLastStartTime(values[2]);
				}

				if (lenValues >= 4) {// 有 sys_source
					String sys_source1 = values[3]; // sys_source from redis
					// sys_source from file
					String sys_source2 = relation.getSysSource();
					if (isBlank(sys_source2)) {
						sys_source2 = "2";
					}

					int sys_source = 0;
					try {
						sys_source = sys_source | Integer.parseInt(sys_source2);
						sys_source = sys_source | Integer.parseInt(sys_source1);
					} catch (Exception e) {
					}

					relation.setSysSource(String.valueOf(sys_source));
				}
			} catch (Exception e) {
				// TODO
			}
		}

		hashKeyList.clear();
		hashValues.clear();
	}

	private boolean isBlank(String source) {
		return source == null || source.trim().isEmpty() || "MULL".equals(source) || "NULL".equals(source);
	}

	/**
	 * 从读取的行中抽取关系实体
	 */
	private void parseLines() {
		for (String line : lines) {
			ParseLineUtil.parse(line, "\t", fileds);
			if (fileds.size() >= 9) {
				extractRelationAndAdd(line);
			} else {
				try {
					errorQueue.put(line);
				} catch (InterruptedException e) {
					LOG.error("parse line into errorQueue error, line is:" + line, e);
				}
			}
			this.fileds.clear();
		}
		this.lines.clear();
	}

	private void extractRelationAndAdd(String line) {
		String idFrom = fileds.get(0);
		String fromType = fileds.get(1);
		String idTo = fileds.get(2);
		String toType = fileds.get(3);

		if (ParseLineUtil.filter(idFrom, filters) || ParseLineUtil.filter(idTo, filters)) {
			return;
		}

		try {
			idFrom = validate(idFrom, fromType);
			idTo = validate(idTo, toType);
			if (idFrom == null || idTo == null) {
				throw new Exception("error id with idType");
			}
		} catch (Exception e) {
			return;
		}

		String firstStartTime = fileds.get(4);
		String firstTerminalNum = fileds.get(5);
		String source = fileds.get(6);
		String createTime = fileds.get(7);
		String sysSource = fileds.get(8);

		if (isBlank(sysSource)) {
			sysSource = "2";
		}
		try {
			sysSource = String.valueOf((int) Math.pow(2, Integer.parseInt(sysSource)));
		} catch (Exception e) {
			sysSource = "4";
		}
		String discoverTimes = "";
		String updateTime = "";

		this.hashKeyList.add(new StringBuilder().append(idFrom).append(SPLITER).append(fromType).append(SPLITER)
				.append(idTo).append(SPLITER).append(toType).toString());

		this.relations.add(new MysqlRelation(idFrom, fromType, idTo, toType, firstStartTime, firstTerminalNum, source,
				createTime, discoverTimes, updateTime, sysSource));
	}

	private String validate(String id, String idType) throws Exception {
		if ("-1".equals(id) || "0".equals(id) || id.startsWith(".") || id.startsWith("-")) {
			throw new Exception("error id.");
		}

		if ("1020002".equals(idType) && "00-00-00-00-00-00".equals(id)) {// mac
			throw new Exception("error mac.");
		}

		return CertificationFilter.evaluate(id, idType);
	}

	public void close() {
		this.running = false;
	}
}
