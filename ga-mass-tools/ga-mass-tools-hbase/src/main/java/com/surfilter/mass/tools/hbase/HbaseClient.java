package com.surfilter.mass.tools.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.entity.MysqlRelation;

/**
 * HBase 基本操作
 * 
 * HBaseAdmin ---> Admin
 * 
 * HTableDescriptor ---> Table
 * 
 * HColumnDescriptor ---> ColumnFamily
 * 
 * @author zt
 */
public final class HbaseClient {
	private static final Logger LOG = LoggerFactory.getLogger(HbaseClient.class);

	public static final String COL_FAMILY = "cf";// 存放基础数据
	public static final byte[] COL_FAMILY_BYTES = Bytes.toBytes(COL_FAMILY);
	public static final String SPILITER = "|";
	private static AtomicInteger countsAll = new AtomicInteger(0);
	private String[] hbaseParams;
	private String tableName;

	public HbaseClient(String tableName) {
		MassConfiguration conf = new MassConfiguration();

		String hbaseZkUrl = conf.get("hbase.zk.quorum");
		String hbasePort = conf.get("hbase.zk.port");
		String hbaseMaster = conf.get("hbase.master");
		String hbaseRootDir = conf.get("hbase.rootdir");

		this.hbaseParams = new String[] { hbaseZkUrl, hbasePort, hbaseMaster, hbaseRootDir };
		this.tableName = tableName;
	}

	/**
	 * 使用多线程存在线程安全问题，会出现 flushCommit 异常，可以使用 synchronized 关键字或者使用单线程
	 * 
	 * @param relations
	 */
	public void put(final List<MysqlRelation> relations) {
		List<Put> puts = new ArrayList<>();

		try {
			for (MysqlRelation relation : relations) {
				String idFrom = relation.getIdFrom();
				String fromType = relation.getFromType();
				String idTo = relation.getIdTo();
				String toType = relation.getToType();

				String rowKey = idFrom + SPILITER + fromType + SPILITER + idTo + SPILITER + toType;
				String rowKeyReverse = idTo + SPILITER + toType + SPILITER + idFrom + SPILITER + fromType;

				String first_start_time = relation.getFirstStartTime();
				String first_terminal_num = relation.getFirstTerminalNum();
				String source = relation.getSource();
				String create_time = relation.getCreateTime();
				String discover_times = relation.getDiscoverTimes();
				String update_time = relation.getUpdateTime();
				String sys_source = relation.getSysSource();
				if (isBlank(sys_source)) {
					sys_source = "4";
				}

				if (isBlank(source)) {
					source = "MULL";
				}

				if (isBlank(update_time)) {
					update_time = first_start_time;
				}

				if (isBlank(discover_times)) {
					discover_times = "1";
				}

				String last_start_time = relation.getLastStartTime();
				String last_terminal_num = relation.getLastTerminalNum();

				if (isBlank(last_start_time) || Long.parseLong(first_start_time) > Long.parseLong(last_start_time)) {
					last_start_time = first_start_time;
					last_terminal_num = first_terminal_num;
				}

				if (isBlank(last_terminal_num)) {
					last_terminal_num = first_terminal_num;
				}

				if (isBlank(create_time)) {
					create_time = last_start_time;
				}

				String company_id = "MULL";

				if (isBlank(first_start_time)) {
					first_start_time = "946684800";
				}

				if (isBlank(update_time)) {
					update_time = "946684800";
				}

				puts.add(buildRelationPut(rowKey, Long.parseLong(first_start_time), first_terminal_num,
						Long.parseLong(create_time), Long.parseLong(update_time), last_terminal_num,
						Integer.parseInt(sys_source), source, company_id, Long.parseLong(discover_times)));

				puts.add(buildRelationPut(rowKeyReverse, Long.parseLong(first_start_time), first_terminal_num,
						Long.parseLong(create_time), Long.parseLong(update_time), last_terminal_num,
						Integer.parseInt(sys_source), source, company_id, Long.parseLong(discover_times)));
			}

			insert2Hbase(puts, tableName);

			int batchCounts = relations.size() * 2;
			LOG.debug("push hbase size:{}, total push:{}", batchCounts, countsAll.addAndGet(batchCounts));
		} catch (Exception e) {
			LOG.error("put into hbase relation error, size:{}, {}", puts.size(), e);
		}

		puts = null;
	}

	public Put buildRelationPut(String rowKey, long first_start_time, String first_terminal_num, long create_time,
			long last_start_time, String last_terminal_num, int sys_source, String source, String company_id,
			long discover_times) {
		Put put = new Put(Bytes.toBytes(rowKey));

		boolean flag = false;
		for (int i = 1; i < 8; i++) {
			int pow = (int) (Math.pow(2, i));
			if ((sys_source & pow) != 0) {
				// COL_FAMILY_SOURCE列簇中存厂商+小类，有值才写入
				// company_id : MULL, source: MULL
				put.addColumn(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("s" + i + "_" + source + "_MULL"),
						Bytes.toBytes(1));
				flag = true;
			}
		}

		if (!flag) {
			put.addColumn(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("s2_" + source + "_MULL"), Bytes.toBytes(1));
		}

		put.addColumn(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("discover_times"), Bytes.toBytes(discover_times));
		put.addColumn(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("first_start_time"), Bytes.toBytes(first_start_time));
		put.addColumn(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("first_terminal_num"),
				Bytes.toBytes(first_terminal_num));
		put.addColumn(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("last_start_time"), Bytes.toBytes(last_start_time));
		put.addColumn(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("last_terminal_num"), Bytes.toBytes(last_terminal_num));
		put.addColumn(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("create_time"), Bytes.toBytes(create_time));

		return put;
	}

	private boolean isBlank(String source) {
		return source == null || source.trim().isEmpty() || "MULL".equalsIgnoreCase(source)
				|| "NULL".equalsIgnoreCase(source) || "\\N".equals(source);
	}

	private void insert2Hbase(List<Put> puts, String tableName) throws IOException {
		HBaseDao dao = HBaseDao.getInstance(this.hbaseParams);
		try {
			dao.insertDatas(tableName, puts);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}