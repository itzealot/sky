package com.surfilter.gamass.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.poi.openxml4j.opc.StreamHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.gamass.dao.HBaseDaoAlias;

public final class HbaseUtil {

	private static Logger LOG = LoggerFactory.getLogger(StreamHelper.class);

	private static final byte[] DISCOVER_TIMES_BYTES = Bytes.toBytes("discover_times");
	private static final byte[] FIRST_START_TIME_BYTES = Bytes.toBytes("first_start_time");
	private static final byte[] FIRST_TERMINAL_NUM_BYTES = Bytes.toBytes("first_terminal_num");

	public static final byte[] LONG_DEFALT_VALUE_BYTES = Bytes.toBytes(1L);
	public static final byte[] INT_DEFALT_VALUE_BYTES = Bytes.toBytes(1);
	public static final String CF_NAME = "cf";
	public static final byte[] CF_NAME_BYTES = Bytes.toBytes(CF_NAME);
	private static final String CP = "|";

	/**
	 * 关系数据迁移 Hbase，比较最新出现时间进行更新
	 * 
	 * @param hbaseParams
	 * @param rowKeys
	 * @param values
	 * @param tableName
	 */
	public static void migrateRelation2Hbase(String hbaseParams, List<String> rowKeys, List<String> values,
			String tableName, boolean versionIsFxj) {
		HBaseDaoAlias dao = HBaseDaoAlias.getInstance(hbaseParams.split("\\|"));

		List<Put> puts = getsRelationFromHbase(dao, tableName, rowKeys, values, versionIsFxj);
		insertData2Hbase(dao, tableName, puts);

		puts.clear();
		puts = null;
	}

	/**
	 * 身份数据迁移 Redis及Hbase，比较最新出现时间进行更新
	 * 
	 * @param hbaseParams
	 * @param rowKeys
	 *            id|idType
	 * @param values
	 *            firstStartTime|firstTerminalNum|source|createTime|sysSource
	 * @param tableName
	 */
	public static void migrateCertification2Hbase(String hbaseParams, List<String> rowKeys, List<String> values,
			String tableName) {
		if (rowKeys.isEmpty()) {
			return;
		}

		HBaseDaoAlias dao = HBaseDaoAlias.getInstance(hbaseParams.split("\\|"));

		List<Put> puts = getsCertificationFromHbase(dao, tableName, rowKeys, values);
		insertData2Hbase(dao, tableName, puts);

		puts.clear();
		puts = null;
	}

	/**
	 * 
	 * 
	 * @param dao
	 * @param tableName
	 * @param rowKeys
	 *            id|idType
	 * @param values
	 *            firstStartTime|firstTerminalNum|source|createTime|sysSource
	 * @return
	 */
	private static List<Put> getsCertificationFromHbase(HBaseDaoAlias dao, String tableName, List<String> rowKeys,
			List<String> values) {
		HConnection conn = null;
		int size = rowKeys.size();
		List<Put> puts = new ArrayList<>(size);

		try {
			conn = dao.getConn();

			int trys = 1;
			while (conn == null && trys < 4) {
				Threads.sleep(10);
				conn = dao.getConn();
				LOG.warn("getsCertificationFromHbase tableName:{}, trys:{}, size:{}", tableName, trys, size);
				trys++;
			}

			if (conn != null) {
				List<Get> gets = new ArrayList<>(size);

				for (int i = 0; i < size; i++) {
					gets.add(new Get(Bytes.toBytes(rowKeys.get(i))));
				}

				Result[] results = dao.gets(conn, tableName, gets);
				gets.clear();
				gets = null;

				for (int i = 0; i < size; i++) {
					String rowKey = rowKeys.get(i);

					byte[] rowKeyBytes = Bytes.toBytes(rowKey);

					String[] vals = values.get(i).split("\\|");
					long lastStartTime = Long.parseLong(vals[0]);
					byte[] lastTerminalNumBytes = Bytes.toBytes(vals[1]);
					String source = vals[2]; // source
					String sysSource = vals[4]; // sysSource
					byte[] lastStartTimeBytes = Bytes.toBytes(lastStartTime);

					byte[] col = Bytes.toBytes("s" + sysSource + "_" + source + "_MULL");

					Put put = new Put(rowKeyBytes);
					put.add(CF_NAME_BYTES, col, INT_DEFALT_VALUE_BYTES);

					byte[] ltnCol = Bytes.toBytes("ltn" + sysSource);
					byte[] lstCol = Bytes.toBytes("lst" + sysSource);

					boolean flag = false;// 是否更新最新发现时间以及场所标记

					if (results[i] != null && !results[i].isEmpty()) { // 行健存在
						byte[] val = results[i].getValue(CF_NAME_BYTES, Bytes.toBytes("lst" + sysSource));

						if (val == null || Bytes.toLong(val) < lastStartTime) { // 比较大类的最新出现时间
							flag = true;
						}

						val = results[i].getValue(CF_NAME_BYTES, FIRST_START_TIME_BYTES);

						if (val == null || Bytes.toLong(val) > lastStartTime) { // 比较首次发现时间
							put.add(CF_NAME_BYTES, FIRST_START_TIME_BYTES, Bytes.toBytes(Long.valueOf(lastStartTime)));
							put.add(CF_NAME_BYTES, FIRST_TERMINAL_NUM_BYTES, lastTerminalNumBytes);
						}
					} else { // 行健不存在，代表是第一次插入，插入次数
						put.add(CF_NAME_BYTES, DISCOVER_TIMES_BYTES, LONG_DEFALT_VALUE_BYTES);

						put.add(CF_NAME_BYTES, FIRST_START_TIME_BYTES, Bytes.toBytes(Long.valueOf(lastStartTime)));
						put.add(CF_NAME_BYTES, FIRST_TERMINAL_NUM_BYTES, lastTerminalNumBytes);

						flag = true;
					}

					if (flag) { // 是否更新最新发现时间以及场所
						put.add(CF_NAME_BYTES, ltnCol, lastTerminalNumBytes);
						put.add(CF_NAME_BYTES, lstCol, lastStartTimeBytes);
					}

					puts.add(put);
				}
			} else {
				LOG.error("getsCertificationFromHbase get hbase conn error.");
			}
		} catch (Exception e) {
			LOG.error("getsCertificationFromHbase persist datas into hbase error!", e);
		} finally {
			dao.releaseConn(conn);
		}

		return puts;
	}

	/**
	 * 根据 rowKey 及 value 比较获取最新发现时间，并构造 Put列表
	 * 
	 * @param rowKeys
	 *            id_from|from_type|id_to|to_type
	 * @param values
	 *            firstStartTime|firstTerminalNum|source|createTime|sysSource
	 * @param dao
	 * @return
	 */
	private static List<Put> getsRelationFromHbase(HBaseDaoAlias dao, String tableName, List<String> rowKeys,
			List<String> values, boolean versionIsFxj) {
		HConnection conn = null;
		int size = rowKeys.size();
		List<Put> puts = new ArrayList<>(size * 2);

		try {
			conn = dao.getConn();

			int trys = 1;
			while (conn == null && trys < 4) {
				Threads.sleep(10);
				conn = dao.getConn();
				LOG.warn("getsRelationFromHbase tableName:{}, trys:{}, size:{}", tableName, trys, size);
				trys++;
			}

			if (conn != null) {
				List<Get> gets = new ArrayList<>(size);

				for (int i = 0; i < size; i++) {
					gets.add(new Get(Bytes.toBytes(rowKeys.get(i))));
				}

				Result[] results = dao.gets(conn, tableName, gets);
				gets.clear();
				gets = null;

				for (int i = 0; i < size; i++) {
					String rowKey = rowKeys.get(i);

					byte[] rowKeyBytes = Bytes.toBytes(rowKey);
					byte[] reverseRowKeyBytes = Bytes.toBytes(reverseRelationKey(rowKey, versionIsFxj));

					String[] vals = values.get(i).split("\\|");
					long lastStartTime = Long.parseLong(vals[0]);
					byte[] lastTerminalNumBytes = Bytes.toBytes(vals[1]);
					String sysSource = vals[4]; // sysSource
					String source = vals[2]; // source
					byte[] lastStartTimeBytes = Bytes.toBytes(lastStartTime);

					byte[] col = Bytes.toBytes("s" + sysSource + "_" + source + "_MULL");

					Put put = new Put(rowKeyBytes);
					put.add(CF_NAME_BYTES, col, INT_DEFALT_VALUE_BYTES);

					Put reversePut = new Put(reverseRowKeyBytes);
					reversePut.add(CF_NAME_BYTES, col, INT_DEFALT_VALUE_BYTES);

					byte[] ltnCol = Bytes.toBytes("ltn" + sysSource);
					byte[] lstCol = Bytes.toBytes("lst" + sysSource);

					boolean flag = false;// 是否更新最新发现时间以及场所标记

					if (results[i] != null && !results[i].isEmpty()) { // 行健存在
						byte[] val = results[i].getValue(CF_NAME_BYTES, Bytes.toBytes("lst" + sysSource));

						if (val == null || Bytes.toLong(val) < lastStartTime) { // 比较最新出现时间
							flag = true;
						}
					} else { // 行健不存在，代表是第一次插入，插入次数
						put.add(CF_NAME_BYTES, DISCOVER_TIMES_BYTES, LONG_DEFALT_VALUE_BYTES);
						reversePut.add(CF_NAME_BYTES, DISCOVER_TIMES_BYTES, LONG_DEFALT_VALUE_BYTES);
						flag = true;
					}

					if (flag) { // 是否更新最新发现时间以及场所
						put.add(CF_NAME_BYTES, ltnCol, lastTerminalNumBytes);
						put.add(CF_NAME_BYTES, lstCol, lastStartTimeBytes);

						reversePut.add(CF_NAME_BYTES, ltnCol, lastTerminalNumBytes);
						reversePut.add(CF_NAME_BYTES, lstCol, lastStartTimeBytes);
					}

					puts.add(put);
					puts.add(reversePut);
				}
			} else {
				LOG.error("getsRelationFromHbase get hbase conn error.");
			}
		} catch (Exception e) {
			LOG.error("getsRelationFromHbase persist datas into hbase error!", e);
		} finally {
			dao.releaseConn(conn);
		}

		return puts;
	}

	private static String reverseRelationKey(String str, boolean versionIsFxj) {
		String[] keyArr = str.split("\\|");
		String rowkey = new StringBuffer(128).append(keyArr[2]).append(CP).append(keyArr[3]).append(CP)
				.append(keyArr[0]).append(CP).append(keyArr[1]).toString();

		if (versionIsFxj) { // 分县局版本
			rowkey = new StringBuffer(128).append(keyArr[3]).append(CP).append(keyArr[4]).append(CP).append(keyArr[1])
					.append(CP).append(keyArr[2]).toString();
			return ParseRelationUtil.addRelationHashPrefix(rowkey, 8);
		}

		return rowkey;
	}

	private static void insertData2Hbase(HBaseDaoAlias dao, String tableName, List<Put> puts) {
		if (puts.isEmpty()) {
			return;
		}

		HConnection conn = null;

		try {
			conn = dao.getConn();

			int trys = 1;
			while (conn == null && trys < 4) {
				Threads.sleep(10);
				conn = dao.getConn();
				LOG.warn("insertData2Hbase tableName:{}, trys:{}, size:{}", tableName, trys, puts.size());
				trys++;
			}

			if (conn != null) {
				dao.insertDatas(conn, tableName, puts);
			} else {
				LOG.error("insertData2Hbase get hbase conn error.");
			}
		} catch (Exception e) {
			LOG.error("persist datas into hbase error!size:" + puts.size(), e);
		} finally {
			dao.releaseConn(conn);
		}
	}

	private HbaseUtil() {
	}
}
