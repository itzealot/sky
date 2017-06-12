package com.surfilter.mass.tools.conf;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JdbcUtils {

	private static final Logger LOG = LoggerFactory.getLogger(JdbcUtils.class);

	public static final int BATCH_SIZE = 5000;
	public static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

	public static Connection getConn(String url, String userName, String passWord) {
		try {
			return DriverManager.getConnection(url, userName, passWord);
		} catch (Exception e) {
			LOG.error("can't get mysql connection.", e);
			return null;
		}
	}

	public static void validate(String url, String userName, String passWord) {
		if (StringUtils.isEmpty(url) || StringUtils.isEmpty(userName) || StringUtils.isEmpty(passWord)) {
			throw new IllegalArgumentException("jdbc.url, jdbc.userName or jdbc.password can't be empty");
		}
	}

}
