package com.surfilter.mass.tools.pool;

import org.apache.hadoop.hbase.client.Connection;

import com.surfilter.commons.pool.hbase.HbaseConnectionPool;
import com.surfilter.mass.tools.util.PoolConfigUtil;

/**
 * Hbase pool
 * 
 * @author zealot
 *
 */
public class HBasePool {

	private static HBasePool instance = null;
	private HbaseConnectionPool pool;

	private HBasePool(String[] hbaseParams) {
		this.pool = new HbaseConnectionPool(PoolConfigUtil.initPoolConfig(),
				PoolConfigUtil.initHbaseProperties(hbaseParams));
	}

	/**
	 * [0]:hbase.zookeeper.quorum
	 * 
	 * [1]hbase.zookeeper.property.clientPort
	 * 
	 * [2]hbase.master
	 * 
	 * [3]hbase.rootdir
	 * 
	 * @param hbaseParams
	 * @return
	 */
	public static HBasePool getInstance(String[] hbaseParams) {
		if (instance == null) {
			synchronized (HBasePool.class) {
				if (instance == null) {
					instance = new HBasePool(hbaseParams);
				}
			}
		}

		return instance;
	}

	public Connection getConn() {
		return pool.getConnection();
	}

	public void releaseConn(Connection conn) {
		pool.returnConnection(conn);
	}
}
