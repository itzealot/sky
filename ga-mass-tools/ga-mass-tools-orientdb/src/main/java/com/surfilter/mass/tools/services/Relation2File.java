package com.surfilter.mass.tools.services;

import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.SysConstants;
import com.surfilter.mass.tools.dao.FileDao;
import com.surfilter.mass.tools.dao.MdssIdrillerDao;
import com.surfilter.mass.tools.utils.DateUtil;

public class Relation2File {

	private static final Logger LOG = LoggerFactory.getLogger(Relation2File.class);

	private String startTime;
	private String endTime;
	private MdssIdrillerDao mdssIdriller;
	private String impalaUrl;


	public Relation2File(String startTime, String endTime, String impalaUrl) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.impalaUrl = impalaUrl;
	}

	// 创建库及表结构
	private void initTable() {
		String sql = "";
		try {
			sql = "CREATE DATABASE IF NOT EXISTS ZDR;";
			mdssIdriller.execute(sql);

			sql = "CREATE TABLE IF NOT EXISTS ZDR.RELATION_PARQUET1(FROM_TO_CONCAT_STRING STRING, FOUND_COUNT BIGINT) STORED AS PARQUET;";
			mdssIdriller.execute(sql);

			sql = "CREATE TABLE IF NOT EXISTS ZDR.RELATION_PARQUET2(ID_FROM STRING, FROM_TYPE STRING, ID_TO STRING, TO_TYPE STRING, "
					+ "FIRST_START_TIME BIGINT, FIRST_TERMINAL_NUM STRING, SOURCE  INT, SYS_SOURCE TINYINT, COMPANY_ID STRING, "
					+ "FROM_TO_CONCAT_STRING STRING)STORED AS PARQUET;";
			mdssIdriller.execute(sql);

			sql = "CREATE TABLE IF NOT EXISTS ZDR.RELATION_PARQUET3(ID_FROM STRING, FROM_TYPE STRING, ID_TO STRING, TO_TYPE STRING, "
					+ "FIRST_START_TIME BIGINT, FIRST_TERMINAL_NUM STRING, SOURCE  INT, SYS_SOURCE TINYINT, COMPANY_ID STRING, "
					+ "FOUND_COUNT BIGINT)STORED AS PARQUET;";
			mdssIdriller.execute(sql);

			sql = "CREATE TABLE IF NOT EXISTS ZDR.RELATION_PARQUET4(ID_FROM STRING, FROM_TYPE STRING, ID_TO STRING, TO_TYPE STRING, "
					+ "FIRST_START_TIME BIGINT, FIRST_TERMINAL_NUM STRING, SOURCE  INT, SYS_SOURCE TINYINT, COMPANY_ID STRING, "
					+ "FOUND_COUNT BIGINT)STORED AS PARQUET;";
			mdssIdriller.execute(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS ZDR.RELATION_PARQUET_ALL(ID_FROM STRING, FROM_TYPE STRING, ID_TO STRING, TO_TYPE STRING, "
					+ "FIRST_START_TIME BIGINT, FIRST_TERMINAL_NUM STRING, SOURCE  INT, SYS_SOURCE TINYINT, COMPANY_ID STRING, "
					+ "FOUND_COUNT BIGINT)STORED AS PARQUET;";
			mdssIdriller.execute(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS ZDR.RELATION_PARQUET_FILTER1(ID_FROM STRING, FROM_TYPE STRING, ID_TO STRING, TO_TYPE STRING, "
					+ "FIRST_START_TIME BIGINT, FIRST_TERMINAL_NUM STRING, SOURCE  INT, SYS_SOURCE TINYINT, COMPANY_ID STRING, "
					+ "FOUND_COUNT BIGINT)STORED AS PARQUET;";
			mdssIdriller.execute(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS ZDR.RELATION_PARQUET_FILTER2(ID_FROM STRING, FROM_TYPE STRING, ID_TO STRING, TO_TYPE STRING, "
					+ "FIRST_START_TIME BIGINT, FIRST_TERMINAL_NUM STRING, SOURCE  INT, SYS_SOURCE TINYINT, COMPANY_ID STRING, "
					+ "FOUND_COUNT BIGINT)STORED AS PARQUET;";
			mdssIdriller.execute(sql);
			
			sql = "CREATE TABLE IF NOT EXISTS ZDR.RELATION_PARQUET_FILTER3(ID_FROM STRING, FROM_TYPE STRING, ID_TO STRING, TO_TYPE STRING, "
					+ "FIRST_START_TIME BIGINT, FIRST_TERMINAL_NUM STRING, SOURCE  INT, SYS_SOURCE TINYINT, COMPANY_ID STRING, "
					+ "FOUND_COUNT BIGINT)STORED AS PARQUET;";
			mdssIdriller.execute(sql);
						
			sql = "CREATE TABLE IF NOT EXISTS ZDR.RELATION_PARQUET_ODB(ID_FROM STRING, FROM_TYPE STRING, ID_TO STRING, TO_TYPE STRING, "
					+ "FIRST_START_TIME BIGINT, FIRST_TERMINAL_NUM STRING, SOURCE  INT, SYS_SOURCE TINYINT, COMPANY_ID STRING, "
					+ "FOUND_COUNT BIGINT)STORED AS PARQUET;";
			mdssIdriller.execute(sql);			
			
		} catch (Exception e) {
			LOG.info("initTable exception : " + e + ", sql : " + sql);
		}

	}

	
	public void init() {
		mdssIdriller = new MdssIdrillerDao();
		LOG.info("impalaUrl : " + impalaUrl);
		mdssIdriller.setUrl(impalaUrl);
		
		mdssIdriller.initConnection();

		initTable();
	}
	
	public void close() {
		mdssIdriller.close();
	}

	public void convertTable() {
		int days = DateUtil.daysBetween(startTime, endTime);
		String tmpTime = startTime;
		
		LOG.info("start convert table ...");

		mdssIdriller.refresh("DEFAULT.RELATION_PARQUET");

		StringBuffer sqlsb = new StringBuffer();
		
		for (int i = 0; i < days; i++) {
			
			try {
				// 第一步：按ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,SYS_SOURCE,SOURCE,COMPANY_ID分组统计发现次数
				sqlsb.append("INSERT OVERWRITE TABLE ZDR.RELATION_PARQUET1(FROM_TO_CONCAT_STRING, FOUND_COUNT) SELECT "
						+ "CONCAT( ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,CAST(MAX(FIRST_START_TIME) AS STRING),IF(SYS_SOURCE IS NOT NULL, "
						+ "CAST(SYS_SOURCE AS STRING),''),IF (SOURCE IS NOT NULL,CAST(SOURCE AS STRING),''),IF (COMPANY_ID IS NOT NULL,"
						+ "COMPANY_ID,'')), COUNT(*) AS NN FROM DEFAULT.RELATION_PARQUET WHERE CREATE_TIME_P='"
						+ tmpTime + "' GROUP BY " + "ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,SYS_SOURCE,SOURCE,COMPANY_ID;");

				LOG.info("\nstep1 sql:" + sqlsb.toString());
				mdssIdriller.execute(sqlsb.toString());
				sqlsb.setLength(0);

				// 第二步：将分组各字段数据插入中间表

				sqlsb.append(
						"INSERT OVERWRITE TABLE ZDR.RELATION_PARQUET2(ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,FIRST_START_TIME,"
								+ "SYS_SOURCE,SOURCE,COMPANY_ID,FIRST_TERMINAL_NUM,FROM_TO_CONCAT_STRING) SELECT DISTINCT ID_FROM,"
								+ "FROM_TYPE,ID_TO,TO_TYPE,FIRST_START_TIME,SYS_SOURCE,SOURCE,COMPANY_ID,FIRST_TERMINAL_NUM,CONCAT( "
								+ "ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,CAST(FIRST_START_TIME AS STRING),IF(SYS_SOURCE IS NOT NULL, "
								+ "CAST(SYS_SOURCE AS STRING),''),IF (SOURCE IS NOT NULL,CAST(SOURCE AS STRING),''),IF (COMPANY_ID IS NOT NULL,"
								+ "COMPANY_ID,'')) FROM DEFAULT.RELATION_PARQUET WHERE CREATE_TIME_P='" + tmpTime
								+ "' AND CONCAT(ID_FROM,"
								+ "FROM_TYPE,ID_TO,TO_TYPE,CAST(FIRST_START_TIME AS STRING),IF(SYS_SOURCE IS NOT NULL, "
								+ "CAST(SYS_SOURCE AS STRING),''),IF (SOURCE IS NOT NULL,CAST(SOURCE AS STRING),''),IF (COMPANY_ID IS NOT NULL,"
								+ "COMPANY_ID,'')) IN (SELECT FROM_TO_CONCAT_STRING FROM ZDR.RELATION_PARQUET1);");
				LOG.info("\nstep2 sql:" + sqlsb.toString());
				mdssIdriller.execute(sqlsb.toString());
				sqlsb.setLength(0);

				// 第三步：分组各字段与总数合并

				sqlsb.append(
						"INSERT OVERWRITE TABLE ZDR.RELATION_PARQUET3(ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,FIRST_START_TIME,SYS_SOURCE,"
								+ "SOURCE,COMPANY_ID,FIRST_TERMINAL_NUM,FOUND_COUNT) SELECT DISTINCT B.ID_FROM,B.FROM_TYPE,B.ID_TO,B.TO_TYPE,"
								+ "B.FIRST_START_TIME,B.SYS_SOURCE,B.SOURCE,B.COMPANY_ID,B.FIRST_TERMINAL_NUM,A.FOUND_COUNT FROM "
								+ "ZDR.RELATION_PARQUET1 A JOIN ZDR.RELATION_PARQUET2 B ON A.FROM_TO_CONCAT_STRING = B.FROM_TO_CONCAT_STRING;");
				LOG.info("\nstep3 sql:" + sqlsb.toString());
				mdssIdriller.execute(sqlsb.toString());
				sqlsb.setLength(0);

				// 第四步：合并到总表
				
				sqlsb.append(
						"INSERT INTO ZDR.RELATION_PARQUET4(ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,FIRST_START_TIME,SYS_SOURCE,SOURCE,"
								+ "COMPANY_ID,FIRST_TERMINAL_NUM,FOUND_COUNT) SELECT DISTINCT ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,FIRST_START_TIME,"
								+ "SYS_SOURCE,SOURCE,COMPANY_ID,FIRST_TERMINAL_NUM,FOUND_COUNT FROM ZDR.RELATION_PARQUET3;");
				LOG.info("\nstep4 sql:" + sqlsb.toString());
				mdssIdriller.execute(sqlsb.toString());
				sqlsb.setLength(0);

				tmpTime = DateUtil.getBeforeDay(startTime, i+1);
			} catch (Exception e) {
				LOG.error("convertTable exception: " + e);
				//发生异常重新建立连接
				mdssIdriller.initConnection();
			}
		}
		
		//合并到RELATION_PARQUET4后，还需要进行将所有天的进行汇总，以减少操作图的次数
		sqlsb.append(
				"INSERT OVERWRITE TABLE ZDR.RELATION_PARQUET1(FROM_TO_CONCAT_STRING, FOUND_COUNT) SELECT CONCAT( ID_FROM,"
				+ "FROM_TYPE,ID_TO,TO_TYPE,CAST(MAX(FIRST_START_TIME) AS STRING),IF(SYS_SOURCE IS NOT NULL, "
				+ "CAST(SYS_SOURCE AS STRING),''),IF (SOURCE IS NOT NULL,CAST(SOURCE AS STRING),''),IF (COMPANY_ID "
				+ "IS NOT NULL,COMPANY_ID,'')), SUM(FOUND_COUNT) AS NN FROM ZDR.RELATION_PARQUET4 GROUP BY ID_FROM,FROM_TYPE,"
				+ "ID_TO,TO_TYPE,SYS_SOURCE,SOURCE,COMPANY_ID;");
		LOG.info("\nall step1 sql:" + sqlsb.toString() + ", pls waiting...");
		mdssIdriller.execute(sqlsb.toString());
		sqlsb.setLength(0);
		
		sqlsb.append(
				"INSERT OVERWRITE TABLE ZDR.RELATION_PARQUET2 SELECT DISTINCT ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,"
				+ "FIRST_START_TIME,FIRST_TERMINAL_NUM,SOURCE,SYS_SOURCE,COMPANY_ID,CONCAT( ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,"
				+ "CAST(FIRST_START_TIME AS STRING),IF(SYS_SOURCE IS NOT NULL, CAST(SYS_SOURCE AS STRING),''),IF (SOURCE "
				+ "IS NOT NULL,CAST(SOURCE AS STRING),''),IF (COMPANY_ID IS NOT NULL,COMPANY_ID,'')) FROM "
				+ "ZDR.RELATION_PARQUET4;");
		LOG.info("\nall step2 sql:" + sqlsb.toString() + ", pls waiting...");
		mdssIdriller.execute(sqlsb.toString());
		sqlsb.setLength(0);
		
		sqlsb.append(
				"INSERT OVERWRITE TABLE ZDR.RELATION_PARQUET3 SELECT DISTINCT B.ID_FROM,B.FROM_TYPE,B.ID_TO,B.TO_TYPE,"
				+ "B.FIRST_START_TIME,B.FIRST_TERMINAL_NUM,B.SOURCE,B.SYS_SOURCE,B.COMPANY_ID,A.FOUND_COUNT FROM "
				+ "ZDR.RELATION_PARQUET1 A JOIN ZDR.RELATION_PARQUET2 B ON A.FROM_TO_CONCAT_STRING = "
				+ "B.FROM_TO_CONCAT_STRING;");
		LOG.info("\nall step3 sql:" + sqlsb.toString() + ", pls waiting...");
		mdssIdriller.execute(sqlsb.toString());
		sqlsb.setLength(0);
		
		sqlsb.append(
				"INSERT INTO ZDR.RELATION_PARQUET_ALL(ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,FIRST_START_TIME,SYS_SOURCE,SOURCE,COMPANY_ID,"
				+ "FIRST_TERMINAL_NUM,FOUND_COUNT) SELECT DISTINCT ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,FIRST_START_TIME,SYS_SOURCE,SOURCE,"
				+ "COMPANY_ID,FIRST_TERMINAL_NUM,FOUND_COUNT FROM ZDR.RELATION_PARQUET3;");
		LOG.info("\nall step3 sql:" + sqlsb.toString() + ", pls waiting...");
		mdssIdriller.execute(sqlsb.toString());		
		sqlsb.setLength(0);
		
		
		
		
		//汇总后，还需要对同一个身份关联同一类型帐号过多的进行过滤，否则太多脏关系
		sqlsb.append(
				"INSERT OVERWRITE TABLE ZDR.RELATION_PARQUET_FILTER1(ID_FROM,TO_TYPE,FIRST_START_TIME) SELECT "
				+ "ID_FROM,CONCAT(ID_FROM,FROM_TYPE,TO_TYPE),COUNT(*) AS NN FROM ZDR.RELATION_PARQUET_ALL GROUP BY "
				+ "ID_FROM,FROM_TYPE,TO_TYPE HAVING NN<6;");
		LOG.info("\nfilter step1 sql:" + sqlsb.toString() + ", pls waiting...");
		mdssIdriller.execute(sqlsb.toString());
		sqlsb.setLength(0);
		
		sqlsb.append(
				"INSERT OVERWRITE TABLE ZDR.RELATION_PARQUET_FILTER2 SELECT DISTINCT * FROM ZDR.RELATION_PARQUET_ALL WHERE "
				+ "CONCAT(ID_FROM,FROM_TYPE,TO_TYPE) IN (SELECT DISTINCT TO_TYPE FROM ZDR.RELATION_PARQUET_FILTER1);");
		LOG.info("\nfilter step2 sql:" + sqlsb.toString() + ", pls waiting...");
		mdssIdriller.execute(sqlsb.toString());
		sqlsb.setLength(0);
		
		sqlsb.append(
				"INSERT OVERWRITE TABLE ZDR.RELATION_PARQUET_FILTER3(ID_TO,TO_TYPE,FIRST_START_TIME) SELECT ID_TO,"
				+ "CONCAT(ID_TO,TO_TYPE,FROM_TYPE),COUNT(*) AS NN FROM ZDR.RELATION_PARQUET_FILTER2 GROUP BY ID_TO,TO_TYPE,"
				+ "FROM_TYPE HAVING NN<6;");
		LOG.info("\nfilter step3 sql:" + sqlsb.toString() + ", pls waiting...");
		mdssIdriller.execute(sqlsb.toString());
		sqlsb.setLength(0);
		
		sqlsb.append(
				"INSERT OVERWRITE TABLE ZDR.RELATION_PARQUET_ODB SELECT DISTINCT * FROM ZDR.RELATION_PARQUET_FILTER2 "
				+ "WHERE CONCAT(ID_TO,TO_TYPE,FROM_TYPE) IN (SELECT DISTINCT TO_TYPE FROM ZDR.RELATION_PARQUET_FILTER3);");
		LOG.info("\nfilter step4 sql:" + sqlsb.toString() + ", pls waiting...");
		mdssIdriller.execute(sqlsb.toString());
		sqlsb.setLength(0);
			
		
		LOG.info("\nEnd convert table to ZDR.RELATION_PARQUET_ODB !!!");
	}

	public void export2File(String fileName) {

		String sql = "SELECT ID_FROM, FROM_TYPE, ID_TO, TO_TYPE, FIRST_START_TIME, FIRST_TERMINAL_NUM, SOURCE, SYS_SOURCE, "
				+ "COMPANY_ID, FOUND_COUNT FROM ZDR.RELATION_PARQUET_ODB";

		LOG.info("export2File sql:" + sql);

		ResultSet rs = null;

		rs = mdssIdriller.query(sql);

		writeToFiles(rs, fileName);

		LOG.info("File export success : " + fileName);
	}

	private void writeToFiles(ResultSet resultSets, String fileName) {
		FileDao fileDao = new FileDao(fileName);
		try {
			if (resultSets.wasNull()) {
				LOG.info("ResultSet为null");
				return;
			}
			fileDao.init();
			while (resultSets.next()) {
				String idFrom = getString(resultSets.getString(1));
				String fromType = getString(resultSets.getString(2));
				String idTo = getString(resultSets.getString(3));
				String toType = getString(resultSets.getString(4));
				String firstStartTime = getString(resultSets.getString(5));
				String firstTerminalNum = getString(resultSets.getString(6));
				String source = getString(resultSets.getString(7));
				String sysSource = getString(resultSets.getString(8));
				String companyId = getString(resultSets.getString(9));
				String foundCount = getString(resultSets.getString(10));
				String data = "";
				data = idFrom + SysConstants.RELATION_FILE_SPLITER + fromType + SysConstants.RELATION_FILE_SPLITER
						+ idTo + SysConstants.RELATION_FILE_SPLITER + toType + SysConstants.RELATION_FILE_SPLITER
						+ firstStartTime + SysConstants.RELATION_FILE_SPLITER + firstTerminalNum
						+ SysConstants.RELATION_FILE_SPLITER + source + SysConstants.RELATION_FILE_SPLITER + sysSource
						+ SysConstants.RELATION_FILE_SPLITER + companyId + SysConstants.RELATION_FILE_SPLITER
						+ foundCount + "\n";
				fileDao.write(data);
			}
		} catch (Exception e) {
			LOG.error("writeToFiles relation error", e);
		} finally {
			fileDao.close();
		}

	}

	private String getString(String str) {
		if (str == null || str.equals("null") || str.equals("MULL")) {
			return "";
		}
		return str;
	}

}
