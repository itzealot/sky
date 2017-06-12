package com.surfilter.mass.tools.services;

import com.google.common.collect.Lists;
import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SysConstants;
import com.surfilter.mass.tools.hbase.HBaseDao;
import com.surfilter.mass.tools.util.HbaseToolsUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseToolsDeleteService {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsDeleteService.class);

	private static final int BUFFER_SIZE = 2 * 1024 * 1024;
	private static final int BATCH_SIZE = 5000;
	private static final String HBASE_SPLITER = SysConstants.HBASE_KEY_SPLITER;

	private String[] hbaseParams;
	private boolean versionIsFxj; // 是否为分县局版本

	public HbaseToolsDeleteService() {
		MassConfiguration conf = new MassConfiguration();

		String hbaseZkUrl = conf.get("hbase.zk.quorum");
		String hbasePort = conf.get("hbase.zk.port");
		String hbaseMaster = conf.get("hbase.master");
		String hbaseRootDir = conf.get("hbase.rootdir");

		this.versionIsFxj = "true".equals(conf.get(SysConstants.VERSION_IS_FXJ));
		this.hbaseParams = new String[] { hbaseZkUrl, hbasePort, hbaseMaster, hbaseRootDir };
	}

	public void deleteRows(String tableName, String rowkeys, String mode) {
		String[] rowKeysArray = StringUtils.split(rowkeys, ",");

		if (rowKeysArray != null && rowKeysArray.length > 0) {
			HBaseDao dao = HBaseDao.getInstance(this.hbaseParams);

			try {
				dao.deleteByRowkeys(tableName, rowKeysArray);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteRowsInFile(String tableName, File file, String spliter, String mode) {
		BufferedReader br = null;

		try {
			int i = 1;
			br = new BufferedReader(new FileReader(file), BUFFER_SIZE);
			String line = "";

			List<String> rowkeyLists = Lists.newArrayListWithCapacity(BATCH_SIZE);
			HBaseDao dao = HBaseDao.getInstance(this.hbaseParams);

			while ((line = br.readLine()) != null) {
				if (StringUtils.isNotEmpty(line)) {
					String[] arrays = line.split(spliter);

					if (arrays.length == 4 && "r".equals(mode)) {
						rowkeyLists.add(getRelationKey(arrays));
						rowkeyLists.add(getRelationReverseKey(arrays)); // 关系删除需要删除反Key
					} else if (arrays.length == 2 && "c".equals(mode)) {
						String rowkey = arrays[0] + "|" + arrays[1];
						
						if (versionIsFxj) { // 分县局版本
							rowkey = HbaseToolsUtil.addHashPrefix(rowkey, 8);
						}
						
						rowkeyLists.add(rowkey);
					} else {
						LOG.error("error line:{}", line);
						continue;
					}

					if (i % BATCH_SIZE == 0) { // 执行删除
						dao.deleteByRowkeys(tableName, rowkeyLists);
						LOG.info("finish delete rows size:{}", rowkeyLists.size());
						rowkeyLists.clear();
					}
					i++;
				}
			}

			if (rowkeyLists.size() > 0) {
				dao.deleteByRowkeys(tableName, rowkeyLists);
			}

			LOG.debug("Total delete rowkeys:{}", i);
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
}
