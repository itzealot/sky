/*
 * Copyright 2015-2016 Dark Phoenixs (Open-Source Organization).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sky.project.share.common.pool.jdbc;

import java.sql.Connection;
import java.util.Properties;

import com.sky.project.share.common.pool.ConnectionException;
import com.sky.project.share.common.pool.ConnectionPool;
import com.sky.project.share.common.pool.PoolBase;
import com.sky.project.share.common.pool.PoolConfig;

/**
 * JdbcConnectionPool
 * 
 * @author zealot
 *
 */
public class JdbcConnectionPool extends PoolBase<Connection> implements ConnectionPool<Connection> {

	private static final long serialVersionUID = -782450959510771063L;

	public JdbcConnectionPool() {
		this(JdbcConfig.MYSQL_DEFAULT_DRIVER_CLASS, JdbcConfig.MYSQL_DEFAULT_JDBC_URL,
				JdbcConfig.MYSQL_DEFAULT_JDBC_USERNAME, JdbcConfig.MYSQL_DEFAULT_JDBC_PASSWORD);
	}

	/**
	 * @param properties
	 *            JDBC参数
	 */
	public JdbcConnectionPool(final Properties properties) {
		this(new PoolConfig(), properties);
	}

	/**
	 *
	 * @param driverClass
	 *            驱动类
	 * @param jdbcUrl
	 *            数据库URL
	 * @param username
	 *            数据库用户名
	 * @param password
	 *            数据密码
	 */
	public JdbcConnectionPool(final String driverClass, final String jdbcUrl, final String username,
			final String password) {
		this(new PoolConfig(), driverClass, jdbcUrl, username, password);
	}

	/**
	 *
	 * @param poolConfig
	 *            池配置
	 * @param properties
	 *            JDBC参数
	 */
	public JdbcConnectionPool(final PoolConfig poolConfig, final Properties properties) {
		super(poolConfig, new JdbcConnectionFactory(properties));
	}

	/**
	 *
	 * @param poolConfig
	 *            池配置
	 * @param driverClass
	 *            驱动类
	 * @param jdbcUrl
	 *            数据库URL
	 * @param username
	 *            数据库用户名
	 * @param password
	 *            数据密码
	 */
	public JdbcConnectionPool(final PoolConfig poolConfig, final String driverClass, final String jdbcUrl,
			final String username, final String password) {
		super(poolConfig, new JdbcConnectionFactory(driverClass, jdbcUrl, username, password));
	}

	@Override
	public Connection getConnection() throws ConnectionException {
		return super.getResource();
	}

	@Override
	public void returnConnection(Connection conn) throws ConnectionException {
		super.returnResource(conn);
	}

	@Override
	public void invalidateConnection(Connection conn) throws ConnectionException {
		super.invalidateResource(conn);
	}

}
