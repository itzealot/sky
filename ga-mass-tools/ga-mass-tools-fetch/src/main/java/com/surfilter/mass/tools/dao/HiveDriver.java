package com.surfilter.mass.tools.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.pool.JdbcPool;

public class HiveDriver {
	private static Logger LOG = LoggerFactory.getLogger(HiveDriver.class);

	private JdbcPool pool;

	public HiveDriver(JdbcPool pool) {
		super();
		this.pool = pool;
	}

	public void execute(String sql) {
		Connection conn = pool.getConnection();

		if (conn != null) {
			try {
				conn.createStatement().execute(sql);
			} catch (SQLException e) {
				LOG.error(String.format("execute sql:{} fail.", sql), e);
			} finally {
				pool.returnConnection(conn);
			}
		}
	}
}
