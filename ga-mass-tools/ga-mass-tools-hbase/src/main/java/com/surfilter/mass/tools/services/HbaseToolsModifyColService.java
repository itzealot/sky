package com.surfilter.mass.tools.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SysConstants;
import com.surfilter.mass.tools.hbase.HBaseDao;
import com.surfilter.mass.tools.util.HbaseToolsUtil;

public class HbaseToolsModifyColService {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsModifyColService.class);

	private static final int BATCH_SIZE = 5000;
	private static final byte[] CF_BYTES = Bytes.toBytes("cf");

	private String[] hbaseParams;
	private boolean versionIsFxj; // 是否为分县局版本

	public HbaseToolsModifyColService() {
		MassConfiguration conf = new MassConfiguration();

		String hbaseZkUrl = conf.get("hbase.zk.quorum");
		String hbasePort = conf.get("hbase.zk.port");
		String hbaseMaster = conf.get("hbase.master");
		String hbaseRootDir = conf.get("hbase.rootdir");

		this.versionIsFxj = "true".equals(conf.get(SysConstants.VERSION_IS_FXJ));
		this.hbaseParams = new String[] { hbaseZkUrl, hbasePort, hbaseMaster, hbaseRootDir };
	}

	public void modifyCol(String tableName, String mode, File file, File finishDir, String deleteColumn,
			String addColumn, String spliter) {
		if ("c".equals(mode)) {// 更新 certification 列
			modifyCertCol(tableName, file, finishDir, deleteColumn, addColumn, spliter);
		} else if ("r".equals(mode)) {// 更新 relation 列
			modifyRelation(tableName, file, finishDir, deleteColumn, addColumn, spliter);
		}
	}

	private void modifyCertCol(String tableName, File file, File finishDir, String deleteColumn, String addColumn,
			String spliter) {
		BufferedReader br = null;
		long start = System.currentTimeMillis();
		int i = 1;

		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			List<String> rowkeys = Lists.newArrayListWithCapacity(BATCH_SIZE);

			while ((line = br.readLine()) != null) {
				if (StringUtils.isNotEmpty(line)) {
					String[] arrays = line.split(spliter);

					if (arrays.length < 2) {
						LOG.debug("row:{}", line);
						continue;
					}

					String rowkey = arrays[0] + "|" + arrays[1];
					if (versionIsFxj) { // 分县局版本
						rowkey = HbaseToolsUtil.addHashPrefix(rowkey, 8);
					}
					
					rowkeys.add(rowkey);

					if (rowkeys.size() % BATCH_SIZE == 0) {
						modifyHbaseCol(tableName, rowkeys, deleteColumn, addColumn);
						LOG.debug("modify data {} to hbase", rowkeys.size());
						rowkeys.clear();
					}
				}
				i++;
			}

			if (rowkeys.size() > 0) {
				LOG.debug("delete data {} to hbase", rowkeys.size());
				modifyHbaseCol(tableName, rowkeys, deleteColumn, addColumn);
			}

			FileUtils.moveFileToDirectory(file, finishDir, true);
			long end = System.currentTimeMillis();
			LOG.debug("End to delete column file:" + file.getName() + ",total records:{},total spend:{}s", i,
					(end - start) / 1000L);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void modifyRelation(String tableName, File file, File finishDir, String deleteColumn, String addColumn,
			String spliter) {
		BufferedReader br = null;
		long start = System.currentTimeMillis();
		int i = 1;

		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			List<String> rowkeys = Lists.newArrayListWithCapacity(BATCH_SIZE);

			while ((line = br.readLine()) != null) {
				if (StringUtils.isNotEmpty(line)) {
					String[] arrays = line.split(spliter);
					if (arrays.length < 4) {
						LOG.debug("row:{}", line);
						continue;
					}

					rowkeys.add(buildRelationRowKey(arrays, true));
					rowkeys.add(buildRelationRowKey(arrays, false));

					if (i % BATCH_SIZE == 0) {
						modifyHbaseCol(tableName, rowkeys, deleteColumn, addColumn);
						LOG.debug("delete data {} to hbase", rowkeys.size());
						rowkeys.clear();
					}
				}
				i++;
			}

			if (rowkeys.size() > 0) {
				modifyHbaseCol(tableName, rowkeys, deleteColumn, addColumn);
				LOG.debug("delete data {} to hbase", rowkeys.size());
			}

			FileUtils.moveFileToDirectory(file, finishDir, true);
			long end = System.currentTimeMillis();
			LOG.debug("End to delete column file:" + file.getName() + ",total records:{},total spend:{}s", i,
					(end - start) / 1000L);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String buildRelationRowKey(String[] columns, boolean flag) {
		String rowkey = "";
		if (flag) {
			rowkey = columns[0] + "|" + columns[1] + "|" + columns[2] + "|" + columns[3];
			
			if (versionIsFxj) { // 分县局版本
				rowkey = HbaseToolsUtil.addRelationHashPrefix(rowkey, 8);
			}
		} else {
			rowkey = columns[2] + "|" + columns[3] + "|" + columns[0] + "|" + columns[1];
			
			if (versionIsFxj) { // 分县局版本
				rowkey = HbaseToolsUtil.addRelationHashPrefix(rowkey, 8);
			}
		}
		return rowkey;
	}

	private void modifyHbaseCol(String tableName, List<String> rowkeys, String deleteColumn, String addColumn) {
		HBaseDao dao = HBaseDao.getInstance(this.hbaseParams);

		try {
			dao.modifyColumn(tableName, rowkeys, CF_BYTES, deleteColumn, addColumn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
