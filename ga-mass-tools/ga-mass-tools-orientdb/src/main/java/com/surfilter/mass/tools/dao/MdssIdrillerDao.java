package com.surfilter.mass.tools.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class MdssIdrillerDao {
	private Logger logger = Logger.getLogger(this.getClass());

	private String url;

	private ThreadLocal<Connection> localConn = new ThreadLocal<Connection>();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void initConnection() {
		try {
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			Connection conn = DriverManager.getConnection(url);
			localConn.set(conn);
		} catch (Exception e) {
			localConn.set(null);
			logger.error("open connection failed!", e);
		}
	}

	public boolean close() {
		try {
			Connection conn = localConn.get();
			if (conn != null) {
				conn.close();
				localConn.set(null);
			}
		} catch (Exception e) {
			logger.error("close connection failed!", e);
			return false;
		}
		return true;
	}

	public ResultSet query(String sql) {
		ResultSet rs = null;
		try {
			initConnection();
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			Connection conn = localConn.get();
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			logger.error("query failed!", e);
		} catch (Exception e) {
			logger.error("query failed!", e);
		}
		return rs;
	}

	public boolean refresh(String tableName) {
		boolean returnFlag = false;
		try {
			//initConnection();
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			Connection conn = localConn.get();
			Statement stmt = conn.createStatement();
			String sql = "refresh " + tableName;
			logger.info("refresh : " + sql);
			returnFlag = stmt.execute(sql);
		} catch (SQLException e) {
			logger.error("query failed!", e);
		} catch (Exception e) {
			logger.error("query failed!", e);
		}
		return returnFlag;
	}
	
	public boolean execute(String sql) {
		boolean returnFlag = false;
		try {			
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			Connection conn = localConn.get();
			Statement stmt = conn.createStatement();
			returnFlag = stmt.execute(sql);
		} catch (SQLException e) {
			logger.error("query failed!", e);
		} catch (Exception e) {
			logger.error("query failed!", e);
		}
		return returnFlag;
	}	
}
