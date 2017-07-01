package com.sky.project.share.common.pool.hive;

import java.sql.Connection;
import java.util.Properties;

import com.sky.project.share.common.pool.ConnectionException;
import com.sky.project.share.common.pool.ConnectionPool;
import com.sky.project.share.common.pool.PoolBase;
import com.sky.project.share.common.pool.PoolConfig;

/**
 * HiveConnectionPool
 * 
 * @author zealot
 *
 */
public class HiveConnectionPool extends PoolBase<Connection> implements ConnectionPool<Connection> {

	private static final long serialVersionUID = 5775575856179092552L;

	public HiveConnectionPool() {
		this(HiveConfig.DEFAULT_HIVE_URL);
	}

	public HiveConnectionPool(final String hiveUrl) {
		this(new PoolConfig(), hiveUrl);
	}

	public HiveConnectionPool(final Properties properties) {
		this(new PoolConfig(), properties);
	}

	public HiveConnectionPool(final PoolConfig poolConfig, final String hiveUrl) {
		super(poolConfig, new HiveConnectionFactory(hiveUrl));
	}

	public HiveConnectionPool(final PoolConfig poolConfig, final Properties properties) {
		super(poolConfig, new HiveConnectionFactory(properties));
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
