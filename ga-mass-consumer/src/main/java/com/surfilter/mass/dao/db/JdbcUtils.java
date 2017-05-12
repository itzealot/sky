package com.surfilter.mass.dao.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC 工具类
 * 
 * @author hapuer
 */
public class JdbcUtils {

	private final static Logger LOG = LoggerFactory.getLogger(JdbcUtils.class);

	/**
	 * 获取JDBC 连接
	 * 
	 * @param url
	 * @param userName
	 * @param passWord
	 * @return
	 */
	public static Connection getConn(String url, String userName, String passWord) {
		try {
			return DriverManager.getConnection(url, userName, passWord);
		} catch (SQLException e) {
			LOG.error("get mysql connection fail!", e);
			return null;
		}
	}

	/**
	 * 关闭连接
	 * 
	 * @param closeables
	 */
	public static void close(AutoCloseable... closeables) {
		if (closeables != null) {
			for (int i = 0, len = closeables.length; i < len; i++) {
				if (closeables[i] != null) {
					try {
						closeables[i].close();
					} catch (Exception e) {
					}
				}
			}
		}
	}
}
