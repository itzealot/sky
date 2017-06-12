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

public class HbaseToolsPutService {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsPutService.class);

	private static final int BUFFER_SIZE = 2 * 1024 * 1024;
	private static final int BATCH_SIZE = 5000;
	private static final String HBASE_SPLITER = SysConstants.HBASE_KEY_SPLITER;
	private static final byte[] CF_FAMILY = Bytes.toBytes("cf");

	private String[] hbaseParams;
	private boolean versionIsFxj; // 是否为分县局版本

	public HbaseToolsPutService() {
		MassConfiguration conf = new MassConfiguration();

		String hbaseZkUrl = conf.get("hbase.zk.quorum");
		String hbasePort = conf.get("hbase.zk.port");
		String hbaseMaster = conf.get("hbase.master");
		String hbaseRootDir = conf.get("hbase.rootdir");

		this.versionIsFxj = "true".equals(conf.get(SysConstants.VERSION_IS_FXJ));
		this.hbaseParams = new String[] { hbaseZkUrl, hbasePort, hbaseMaster, hbaseRootDir };
	}

	public void putRowsInFile(String tableName, File file, String spliter, String mode, String column, String value,
			String valueType) {
		BufferedReader br = null;

		try {
			int i = 1;
			br = new BufferedReader(new FileReader(file), BUFFER_SIZE);
			String line = "";

			List<Put> puts = Lists.newArrayListWithCapacity(BATCH_SIZE);
			HBaseDao dao = HBaseDao.getInstance(this.hbaseParams);
			byte[] v = values(value, valueType);
			byte[] qualifier = Bytes.toBytes(column);

			while ((line = br.readLine()) != null) {
				if (StringUtils.isNotEmpty(line)) {
					String[] arrays = line.split(spliter);

					if (arrays.length == 4 && "r".equals(mode)) {// 关系删除需要删除反Key
						String rowKey = getRelationKey(arrays);
						String reverseKey = getRelationReverseKey(arrays);
						puts.add(new Put(Bytes.toBytes(rowKey)).addColumn(CF_FAMILY, qualifier, v));
						puts.add(new Put(Bytes.toBytes(reverseKey)).addColumn(CF_FAMILY, qualifier, v));
					} else if (arrays.length == 2 && "c".equals(mode)) {
						String rowKey = arrays[0] + "|" + arrays[1];

						if (versionIsFxj) { // 分县局版本
							rowKey = HbaseToolsUtil.addHashPrefix(rowKey, 8);
						}

						puts.add(new Put(Bytes.toBytes(rowKey)).addColumn(CF_FAMILY, qualifier, v));
					} else {
						LOG.error("error line:{}", line);
						continue;
					}

					if (i % BATCH_SIZE == 0) { // 执行删除
						dao.insertDatas(tableName, puts);
						LOG.info("finish put row, size:{}", puts.size());
						puts.clear();
					}
					i++;
				}
			}

			if (puts.size() > 0) {
				dao.insertDatas(tableName, puts);
			}

			LOG.debug("Total put size:{}", i);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(br);
		}
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
}
