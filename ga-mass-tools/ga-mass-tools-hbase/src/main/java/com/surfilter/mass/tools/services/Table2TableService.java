package com.surfilter.mass.tools.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.hbase.HBaseDao;

public class Table2TableService {
	private static Logger LOG = LoggerFactory.getLogger(Table2TableService.class);
	private static final int BATCH_SIZE = 30000;
	String rowStart = " ";
	private static final String CF = "cf";
	private String[] hbaseParams = null;
	
	public static String stopRow;
	public static long rowsCount;

	public Table2TableService() {
		MassConfiguration conf = new MassConfiguration();
		String hbaseZkUrl = conf.get("hbase.zk.quorum");
		String hbasePort = conf.get("hbase.zk.port");
		String hbaseMaster = conf.get("hbase.master");
		String hbaseRootDir = conf.get("hbase.rootdir");
		this.hbaseParams = new String[] { hbaseZkUrl, hbasePort, hbaseMaster, hbaseRootDir };
	}
	
/*	public void stopRowSet(String stopRow){
		this.stopRow = stopRow;
	}
	public void rowsCountSet(long  rowsCount){
		this.rowsCount = rowsCount;
	}*/

	public void table2table(String src_table, String dst_table, String startRow) throws Exception {
		HBaseDao dao = HBaseDao.getInstance(this.hbaseParams);
		long startTime = System.currentTimeMillis();
		int sleepTime = 300000;
		int tryTimes = 1;
		stopRow = startRow;
		while(true){
			try {
				LOG.info("Hbase Scan the {} time, start  scan from row {}", tryTimes++, stopRow);
				dao.tableTransport(src_table, dst_table, CF, BATCH_SIZE, stopRow);
				if(stopRow != null){
					LOG.info("Hbase Scan sleep for {} seconds..............", sleepTime/1000);
					Thread.sleep(sleepTime);//休息sleepTime/1000/60分钟
				}else{
					LOG.info("******************** {} transform to {} done********************,time spended: {}s",
							src_table, dst_table, (System.currentTimeMillis() - startTime)/1000);
					break;
				}
			} catch (Exception e) {
				LOG.error("Hbase Connection Got error! try again in {} mins", sleepTime/1000/60, e);
			}
		}

	}

}
