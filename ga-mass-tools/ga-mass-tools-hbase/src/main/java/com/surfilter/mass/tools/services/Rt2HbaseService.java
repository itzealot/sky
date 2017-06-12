package com.surfilter.mass.tools.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SysConstants;
import com.surfilter.mass.tools.hbase.HBaseDao;
import com.surfilter.mass.tools.util.HbaseToolsUtil;
import com.surfilter.mass.tools.util.Id15To18;

public class Rt2HbaseService {
	private static Logger LOG = LoggerFactory.getLogger(Rt2HbaseService.class);
	private static final int BATCH_SIZE = 5000;
	private static final String CF = "cf";
	private static final String DEFAULT_COMPANY_ID = "MULL";
	private String companyId;
	private String[] hbaseParams = null;
	private boolean versionIsFxj; // 是否为分县局版本
	
	public Rt2HbaseService() {
		MassConfiguration conf = new MassConfiguration();
		String hbaseZkUrl = conf.get("hbase.zk.quorum");
		String hbasePort = conf.get("hbase.zk.port");
		String hbaseMaster = conf.get("hbase.master");
		String hbaseRootDir = conf.get("hbase.rootdir");
		
		this.hbaseParams = new String[] { hbaseZkUrl, hbasePort, hbaseMaster, hbaseRootDir };
		this.versionIsFxj = "true".equals(conf.get(SysConstants.VERSION_IS_FXJ));
	}

	public Rt2HbaseService(String companyId) {
		this();
		this.companyId = companyId;
	}

	public void importRelation2Hbase(File[] files, File finishDir, String tableName) {
		for (File f : files) {
			if ((!f.exists()) || (f.isFile())) {
				LOG.debug("Start to import file:{}", f.getName());
				BufferedReader br = null;
				try {
					long start = System.currentTimeMillis();
					int i = 1;
					br = new BufferedReader(new FileReader(f));
					String line = "";
					List<Put> puts = Lists.newArrayListWithCapacity(10000);
					while ((line = br.readLine()) != null) {
						if (StringUtils.isNotEmpty(line)) {
							String[] columns = line.split("\t");
							Put put = buildRtPut(columns, false);
							Put put1 = buildRtPut(columns, true);
							if ((put != null) && (put1 != null)) {
								puts.add(put);
								puts.add(put1);
								if (i % BATCH_SIZE == 0) {
									insert2Hbase(puts, tableName);
									LOG.debug("insert data {} to hbase", Integer.valueOf(puts.size()));
									puts.clear();
								}
							}
						}
						i++;
					}
					if (puts.size() > 0) {
						insert2Hbase(puts, tableName);
						LOG.debug("insert data {} to hbase", Integer.valueOf(puts.size()));
					}
					FileUtils.moveFileToDirectory(f, finishDir, true);
					long end = System.currentTimeMillis();
					LOG.debug("End to import file:" + f.getName() + ",total records:{},total spend:{}s",
							Integer.valueOf(i), Long.valueOf((end - start) / 1000L));
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(br);
				}
			}
		}
	}

	private Put buildRtPut(String[] columns, boolean isReverse) {
		if ((columns != null) && (columns.length == 11)) {
			String idFrom = columns[0];
			String fromType = columns[1];
			String idTo = columns[2];
			String toType = columns[3];

			int sysSource = 0;
			try {
				if (("NULL".equals(columns[8])) || ("0".equals(columns[8]))) {
					sysSource = 2;
				} else {
					sysSource = Integer.parseInt(columns[8]);
				}
			} catch (Exception localException1) {
			}
			if ((sysSource == 6) || (sysSource == 7) || (sysSource == 8)) {
				return null;
			}
			int source = 0;
			try {
				source = Integer.parseInt(columns[6]);
			} catch (Exception localException2) {
			}
			if ((idFrom.length() <= 1) || (idFrom.startsWith("+")) || (idFrom.startsWith("-")) || (idTo.length() <= 1)
					|| (idTo.startsWith("-")) || (idTo.startsWith("+")) || ("MULL".equals(idFrom))
					|| ("MULL".equals(idTo))) {
				return null;
			}
			if (("1020004".equals(fromType)) && (idFrom.length() < 11)) {
				return null;
			}

			if ("1021111".equals(fromType) && idFrom.length() == 15) {
				try {
					idFrom = Id15To18.id15Or18Filter(idFrom);
				} catch (Exception e) {
					return null;
				}
			}
			if (("1330001".equals(toType)) && (idTo.length() > 50)) {
				return null;
			}
			String companyId = "";
			if ("NULL".equals(columns[10])) {
				if (StringUtils.isEmpty(this.companyId)) {
					companyId = DEFAULT_COMPANY_ID;
				} else {
					companyId = this.companyId;
				}
			} else {
				companyId = columns[10];
			}
			Put put = null;
			if (!isReverse) {
				String rowkey = idFrom + "|" + fromType + "|" + idTo + "|" + toType;
				
				if (versionIsFxj) { // 分县局版本
					rowkey = HbaseToolsUtil.addRelationHashPrefix(rowkey, 8);
				}
				
				put = new Put(Bytes.toBytes(rowkey));
			} else {
				String rowkey = idTo + "|" + toType + "|" + idFrom + "|" + fromType;
				
				if (versionIsFxj) { // 分县局版本
					rowkey = HbaseToolsUtil.addRelationHashPrefix(rowkey, 8);
				}
				
				put = new Put(Bytes.toBytes(rowkey));
			}
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("first_start_time"),
					Bytes.toBytes(Long.parseLong(columns[4])));
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("first_terminal_num"), Bytes.toBytes(columns[5]));
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("create_time"), Bytes.toBytes(Long.parseLong(columns[7])));
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("discover_times"), Bytes.toBytes(1L));

			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("last_start_time"),
					Bytes.toBytes(Long.parseLong(columns[4])));
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("last_terminal_num"), Bytes.toBytes(columns[5]));
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("s" + sysSource + "_" + source + "_" + companyId),
					Bytes.toBytes(1));
			return put;
		}
		LOG.error("export data format error,expect 11 columns but only {},pls check ", Integer.valueOf(columns.length));

		return null;
	}

	private void insert2Hbase(List<Put> puts, String tableName) throws IOException {
		HBaseDao dao = HBaseDao.getInstance(this.hbaseParams);
		dao.insertDatas(tableName, puts);
	}
}
