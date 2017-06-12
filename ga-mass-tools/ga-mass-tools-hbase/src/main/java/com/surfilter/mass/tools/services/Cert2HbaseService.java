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
import com.surfilter.mass.tools.util.CertificationFilter;
import com.surfilter.mass.tools.util.HbaseToolsUtil;
import com.surfilter.mass.tools.util.ParseLineUtil;

public class Cert2HbaseService {
	private static Logger LOG = LoggerFactory.getLogger(Cert2HbaseService.class);
	private static final int BATCH_SIZE = 2000;
	private static final String CF = "cf";
	private static final String DEFAULT_COMPANY_ID = "MULL";
	private static final String DEFAULT_VALUE = "MULL";
	private static final String[] FILTERS = "\",\\,/,',>,<,|,?, ,=,+,[,],{,},%,;,&,^,!,(,)".split(",");
	private String[] hbaseParams = null;
	private String companyId;
	private boolean versionIsFxj; // 是否为分县局版本

	public Cert2HbaseService() {
		MassConfiguration conf = new MassConfiguration();
		String hbaseZkUrl = conf.get("hbase.zk.quorum");
		String hbasePort = conf.get("hbase.zk.port");
		String hbaseMaster = conf.get("hbase.master");
		String hbaseRootDir = conf.get("hbase.rootdir");

		this.versionIsFxj = "true".equals(conf.get(SysConstants.VERSION_IS_FXJ));
		this.hbaseParams = new String[] { hbaseZkUrl, hbasePort, hbaseMaster, hbaseRootDir };
	}

	public Cert2HbaseService(String companyId) {
		this();
		this.companyId = companyId;
	}

	public void importCert2Hbase(File[] files, File finishDir, String tableName) {
		for (File f : files) {
			if ((!f.exists()) || (f.isFile())) {
				LOG.debug("Start to import file:{}", f.getName());
				BufferedReader br = null;
				try {
					long start = System.currentTimeMillis();
					int i = 1;
					br = new BufferedReader(new FileReader(f));
					String line = "";
					List<Put> puts = Lists.newArrayListWithCapacity(BATCH_SIZE);
					while ((line = br.readLine()) != null) {
						if (StringUtils.isNotEmpty(line)) {
							String[] columns = line.split("\t");
							Put put = buildCertPut(columns);
							if (put != null) {
								puts.add(put);
								if (i % 2000 == 0) {
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
					LOG.error("execute importCert2Hbase error", e);
				} finally {
					IOUtils.closeQuietly(br);
				}
			}
		}
	}

	private Put buildCertPut(String[] columns) {
		if ((columns != null) && (columns.length == 8)) {
			String id = columns[0];
			String type = columns[1];

			// sys_source
			String sysSource;
			if (isEmpty(columns[6])) {
				sysSource = DEFAULT_VALUE;
			} else {
				sysSource = columns[6];
			}

			// default source is MULL
			String source = DEFAULT_VALUE;
			if ("2".equals(sysSource)) {
				source = "1";
			}

			id = CertificationFilter.evaluate(id, type);
			if (id == null) {
				return null;
			}

			if ((id.length() <= 1) || (id.startsWith("+")) || ("MULL".equals(id)) || ParseLineUtil.filter(id, FILTERS)
					|| id.startsWith(".") || id.startsWith("-")) {
				return null;
			}

			// company_id
			if (isEmpty(this.companyId)) {
				this.companyId = DEFAULT_COMPANY_ID;
			}

			String rowkey = id + "|" + type;
			if (versionIsFxj) { // 分县局版本
				rowkey = HbaseToolsUtil.addHashPrefix(rowkey, 8);
			}

			Put put = new Put(Bytes.toBytes(rowkey));
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("first_start_time"),
					Bytes.toBytes(Long.parseLong(columns[2])));
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("first_terminal_num"), Bytes.toBytes(columns[3]));
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("create_time"), Bytes.toBytes(Long.parseLong(columns[5])));
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("discover_times"), Bytes.toBytes(1L));

			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("last_start_time"),
					Bytes.toBytes(Long.parseLong(columns[2])));
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("last_terminal_num"), Bytes.toBytes(columns[3]));

			// source is MULL
			put.addColumn(Bytes.toBytes(CF), Bytes.toBytes("s" + sysSource + "_" + source + "_" + companyId),
					Bytes.toBytes(1));
			return put;
		}

		LOG.error("export data format error,expect 8 columns but only {},pls check ", Integer.valueOf(columns.length));

		return null;
	}

	private void insert2Hbase(List<Put> puts, String tableName) throws IOException {
		HBaseDao dao = HBaseDao.getInstance(this.hbaseParams);
		dao.insertDatas(tableName, puts);
	}

	private boolean isEmpty(String str) {
		return str == null || "".equals(str.trim()) || "MULL".equalsIgnoreCase(str.trim())
				|| "NULL".equalsIgnoreCase(str.trim()) || "\\N".equals(str.trim());
	}

}
