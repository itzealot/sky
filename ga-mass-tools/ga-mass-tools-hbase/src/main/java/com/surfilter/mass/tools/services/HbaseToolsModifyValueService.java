package com.surfilter.mass.tools.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SysConstants;
import com.surfilter.mass.tools.hbase.HBaseDao;
import com.surfilter.mass.tools.util.HbaseToolsUtil;

public class HbaseToolsModifyValueService {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsModifyValueService.class);

	private static final int BUFFER_SIZE = 2 * 1024 * 1024;
	private static final int BATCH_SIZE = 5000;
	private static final String HBASE_SPLITER = SysConstants.HBASE_KEY_SPLITER;
	private static final String CF = "cf";
	private static final byte[] CF_FAMILY = Bytes.toBytes(CF);

	private String[] hbaseParams;
	private boolean versionIsFxj; // 是否为分县局版本

	public HbaseToolsModifyValueService() {
		MassConfiguration conf = new MassConfiguration();

		String hbaseZkUrl = conf.get("hbase.zk.quorum");
		String hbasePort = conf.get("hbase.zk.port");
		String hbaseMaster = conf.get("hbase.master");
		String hbaseRootDir = conf.get("hbase.rootdir");

		this.versionIsFxj = "true".equals(conf.get(SysConstants.VERSION_IS_FXJ));
		this.hbaseParams = new String[] { hbaseZkUrl, hbasePort, hbaseMaster, hbaseRootDir };
	}

	public void putRowsInFile(String tableName, File file, String spliter, String mode, String column, String value,
			String valueType, String range) {
		BufferedReader br = null;

		try {
			int i = 1;
			br = new BufferedReader(new FileReader(file), BUFFER_SIZE);
			String line = "";

			HBaseDao dao = HBaseDao.getInstance(this.hbaseParams);
			byte[] v = values(value, valueType);
			List<String> rowKeys = Lists.newArrayListWithCapacity(BATCH_SIZE);

			String ranges[] = range.split("-");
			if (ranges.length != 2) {
				throw new RuntimeException("error range value");
			}

			while ((line = br.readLine()) != null) {
				if (StringUtils.isNotEmpty(line)) {
					String[] arrays = line.split(spliter);
					String rowKey = null;

					if (arrays.length == 4 && "r".equals(mode)) {// 关系删除需要删除反Key
						rowKey = getRelationKey(arrays);
					} else if (arrays.length == 2 && "c".equals(mode)) {
						rowKey = arrays[0] + "|" + arrays[1];

						if (versionIsFxj) { // 分县局版本
							rowKey = HbaseToolsUtil.addHashPrefix(rowKey, 8);
						}
					} else {
						LOG.error("error line:{}", line);
						continue;
					}
					if (rowKey != null)
						rowKeys.add(rowKey);

					if (i % BATCH_SIZE == 0) {
						batch(dao, tableName, column, rowKeys, valueType, ranges, v);
						LOG.info("finish modify value, size:{}", rowKeys.size());
						rowKeys.clear();
					}
					i++;
				}

				if (rowKeys.size() > 0) {
					batch(dao, tableName, column, rowKeys, valueType, ranges, v);
				}
			}

			LOG.debug("Total modify size:{}", i);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(br);
		}
	}

	private void batch(HBaseDao dao, String tableName, String column, List<String> rowKeys, String valueType,
			String[] ranges, byte[] v) throws IOException {
		List<byte[]> values = dao.getByRowKeys(tableName, CF, column, rowKeys);// 根据行健获取值

		List<Put> puts = Lists.newArrayListWithCapacity(BATCH_SIZE * 2);

		for (int j = 0, len = values.size(); j < len; j++) {
			byte[] bytes = values.get(j);
			if (bytes != null && valueIsBetween(bytes, valueType, ranges)) { // 在范围内
				String row1 = rowKeys.get(j);

				puts.add(new Put(Bytes.toBytes(row1)).addColumn(CF_FAMILY, Bytes.toBytes(column), v));// 执行更新
				if (tableName.toLowerCase().contains("relation")) { // 是关系要反转key
					puts.add(new Put(Bytes.toBytes(reverseKey(row1))).addColumn(CF_FAMILY, Bytes.toBytes(column), v));// 执行更新
				}
			}
		}

		dao.insertDatas(tableName, puts);

		puts.clear();
		values.clear();

		values = null;
		puts = null;
	}

	private String getRelationKey(String[] arrays) {
		String rowkey = arrays[0] + HBASE_SPLITER + arrays[1] + HBASE_SPLITER + arrays[2] + HBASE_SPLITER + arrays[3];

		if (versionIsFxj) { // 分县局版本
			rowkey = HbaseToolsUtil.addRelationHashPrefix(rowkey, 8);
		}

		return rowkey;
	}

	private String getRelationReverseKey(String[] arrays) {
		String rowkey = arrays[2] + HBASE_SPLITER + arrays[3] + HBASE_SPLITER + arrays[0] + HBASE_SPLITER + arrays[1];

		if (versionIsFxj) { // 分县局版本
			rowkey = HbaseToolsUtil.addRelationHashPrefix(rowkey, 8);
		}

		return rowkey;
	}

	private String reverseKey(String rowKey) {
		return getRelationReverseKey(rowKey.split("\\|"));
	}

	private byte[] values(String value, String valueType) {
		switch (valueType.toLowerCase().trim()) {
		case "int":
			return Bytes.toBytes(Integer.parseInt(value));
		case "long":
			return Bytes.toBytes(Long.parseLong(value));
		case "string":
			return Bytes.toBytes(value);
		}

		throw new IllegalArgumentException("valueType value error, must be int, long or string");
	}

	private boolean valueIsBetween(byte[] bytes, String valueType, String[] ranges) {
		switch (valueType.toLowerCase().trim()) {
		case "int":
			return isBetween(Bytes.toInt(bytes), Integer.parseInt(ranges[0]), Integer.parseInt(ranges[1]));
		case "long":
			return isBetween(Bytes.toLong(bytes), Long.parseLong(ranges[0]), Long.parseLong(ranges[1]));
		case "string":
			return isBetween(Bytes.toString(bytes), ranges[0], ranges[1]);
		}

		throw new IllegalArgumentException("valueType value error, must be int, long or string");
	}

	private boolean isBetween(int value, int val1, int val2) {
		return (value >= val1 && value < val2) || (value >= val2 && value < val1);
	}

	private boolean isBetween(long value, long val1, long val2) {
		return (value >= val1 && value < val2) || (value >= val2 && value < val1);
	}

	private boolean isBetween(String value, String val1, String val2) {
		return (value.compareTo(val1) >= 0 && value.compareTo(val2) < 0)
				|| (value.compareTo(val2) >= 0 && value.compareTo(val1) < 0);
	}
}
