package com.surfilter.mass.tools.services;

import com.google.common.collect.Lists;
import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SysConstants;
import com.surfilter.mass.tools.hbase.HBaseDao;
import com.surfilter.mass.tools.util.HbaseToolsUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseToolsDeleteColService {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsDeleteColService.class);

	private static final int BATCH_SIZE = 5000;
	private static final byte[] CF_BYTES = Bytes.toBytes("cf");

	private String[] hbaseParams;
	private boolean versionIsFxj; // 是否为分县局版本

	public HbaseToolsDeleteColService() {
		MassConfiguration conf = new MassConfiguration();

		String hbaseZkUrl = conf.get("hbase.zk.quorum");
		String hbasePort = conf.get("hbase.zk.port");
		String hbaseMaster = conf.get("hbase.master");
		String hbaseRootDir = conf.get("hbase.rootdir");

		this.versionIsFxj = "true".equals(conf.get(SysConstants.VERSION_IS_FXJ));
		this.hbaseParams = new String[] { hbaseZkUrl, hbasePort, hbaseMaster, hbaseRootDir };
	}

	public void deleteCol(String tableName, String mode, File file, File finishDir, String column, String spliter) {
		if ("c".equals(mode)) {// 删除 certification
			deleteCertCol(tableName, file, finishDir, column, spliter);
		} else if ("r".equals(mode)) {// 删除 relation
			deleteRelation(tableName, file, finishDir, column, spliter);
		}
	}

	private void deleteCertCol(String tableName, File file, File finishDir, String column, String spliter) {
		BufferedReader br = null;
		long start = System.currentTimeMillis();
		int i = 1;

		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			List<Delete> deletes = Lists.newArrayListWithCapacity(BATCH_SIZE);

			while ((line = br.readLine()) != null) {
				if (StringUtils.isNotEmpty(line)) {
					String[] arrays = line.split(spliter);

					if (arrays.length != 2) {
						LOG.error("row:{}", line);
						continue;
					}

					Delete delete = buildCertDelete(arrays, column);

					if (delete != null) {
						deletes.add(delete);
						if (i % BATCH_SIZE == 0) {
							LOG.debug("delete data {} to hbase", deletes.size());
							deleteHbaseCol(tableName, deletes);
							deletes.clear();
						}
					}
				}
				i++;
			}

			if (deletes.size() > 0) {
				LOG.debug("delete data {} to hbase", deletes.size());
				deleteHbaseCol(tableName, deletes);
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

	private void deleteRelation(String tableName, File file, File finishDir, String column, String spliter) {
		BufferedReader br = null;
		long start = System.currentTimeMillis();
		int i = 1;

		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			List<Delete> deletes = Lists.newArrayListWithCapacity(BATCH_SIZE);

			while ((line = br.readLine()) != null) {
				if (StringUtils.isNotEmpty(line)) {
					String[] columns = line.split(spliter);
					if (columns.length != 4) {
						LOG.error("error line:{}", line);
						continue;
					}

					Delete delete = buildRelationDelete(columns, true, column);
					Delete delete1 = buildRelationDelete(columns, false, column);

					if (delete != null && delete1 != null) {
						deletes.add(delete);
						deletes.add(delete1);

						if (deletes.size() % BATCH_SIZE == 0) {
							deleteHbaseCol(tableName, deletes);
							LOG.debug("delete data {} to hbase", deletes.size());
							deletes.clear();
						}
					}
				}
				i++;
			}

			if (deletes.size() > 0) {
				deleteHbaseCol(tableName, deletes);
				LOG.debug("delete data {} to hbase", deletes.size());
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

	private Delete buildRelationDelete(String[] columns, boolean flag, String column) {
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
		return new Delete(Bytes.toBytes(rowkey)).addColumns(CF_BYTES, Bytes.toBytes(column));
	}

	private Delete buildCertDelete(String[] arrays, String column) {
		String rowkey = arrays[0] + "|" + arrays[1];

		if (versionIsFxj) { // 分县局版本
			rowkey = HbaseToolsUtil.addHashPrefix(rowkey, 8);
		}

		byte[] row = Bytes.toBytes(rowkey);
		return new Delete(row).addColumns(CF_BYTES, Bytes.toBytes(column));
	}

	private void deleteHbaseCol(String tableName, List<Delete> dels) {
		HBaseDao dao = HBaseDao.getInstance(this.hbaseParams);

		try {
			dao.deleteCol(tableName, dels);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
