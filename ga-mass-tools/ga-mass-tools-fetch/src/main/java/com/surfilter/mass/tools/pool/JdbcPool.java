package com.surfilter.mass.tools.pool;

import java.sql.Connection;
import java.util.Properties;

import com.surfilter.commons.pool.ConnectionPool;
import com.surfilter.commons.pool.PoolConfig;
import com.surfilter.commons.pool.jdbc.JdbcConnectionPool;

/**
 * jdbc pool for (mysql,hive,impala)
 * 
 * @author zealot
 *
 */
public class JdbcPool {

	public JdbcPool(PoolConfig config, Properties props) {
		pool = new JdbcConnectionPool(config, props);
	}

	private ConnectionPool<Connection> pool;

	public Connection getConnection() {
		return pool.getConnection();
	}

	public void returnConnection(Connection conn) {
		pool.returnConnection(conn);
	}

}
