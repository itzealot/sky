package com.surfilter.mass.dao.db;

import java.sql.Connection;
import java.util.Properties;

import com.surfilter.commons.pool.PoolConfig;
import com.surfilter.commons.pool.jdbc.JdbcConnectionPool;

/**
 * jdbc pool
 * 
 * @author zealot
 *
 */
public class JdbcPool {

	private static JdbcPool instance = null;

	private JdbcConnectionPool pool;

	public static JdbcPool getInstance(JdbcConfig config) {
		if (instance == null) {
			synchronized (JdbcPool.class) {
				if (instance == null) {
					instance = new JdbcPool(config);
				}
			}
		}

		return instance;
	}

	public Connection getConnection() {
		return pool == null ? null : pool.getConnection();
	}

	public void returnConnection(Connection conn) {
		if (pool != null)
			pool.returnConnection(conn);
	}

	private JdbcPool(JdbcConfig config) {
		pool = new JdbcConnectionPool(initPoolConfig(), initProperties(config));
	}

	/**
	 * 初始化jdbc 连接池配置信息
	 * 
	 * @return
	 */
	private PoolConfig initPoolConfig() {
		PoolConfig poolConfig = new PoolConfig();

		poolConfig.setMaxTotal(20);
		poolConfig.setMaxIdle(5);
		poolConfig.setMaxWaitMillis(5000);
		poolConfig.setTestOnBorrow(true);

		return poolConfig;
	}

	/**
	 * 根据 JdbcConfig 对象初始化 Properties
	 * 
	 * @param config
	 * @return
	 */
	private Properties initProperties(JdbcConfig config) {
		Properties properties = new Properties();

		properties.setProperty(com.surfilter.commons.pool.jdbc.JdbcConfig.DRIVER_CLASS_PROPERTY,
				config.getDriverClassName());
		properties.setProperty(com.surfilter.commons.pool.jdbc.JdbcConfig.JDBC_URL_PROPERTY, config.getUrl());
		properties.setProperty(com.surfilter.commons.pool.jdbc.JdbcConfig.JDBC_USERNAME_PROPERTY, config.getUserName());
		properties.setProperty(com.surfilter.commons.pool.jdbc.JdbcConfig.JDBC_PASSWORD_PROPERTY, config.getPassword());

		return properties;
	}

}
