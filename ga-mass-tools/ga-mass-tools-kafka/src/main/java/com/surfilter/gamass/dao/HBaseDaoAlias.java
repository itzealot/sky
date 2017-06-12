package com.surfilter.gamass.dao;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.pool.PoolConfig;
import com.surfilter.commons.pool.hbase.HbaseConnectionPool;

/**
 * @author hapuer
 *
 */
public class HBaseDaoAlias {

	private static Logger LOG = LoggerFactory.getLogger(HBaseDaoAlias.class);

	private static volatile HBaseDaoAlias instance = null;

	private static HbaseConnectionPool pool;

	private HBaseDaoAlias() {
	}

	public static HBaseDaoAlias getInstance(String[] hbaseParamsArray) {
		HBaseDaoAlias inst = instance;
		if (inst == null) {
			synchronized (HBaseDaoAlias.class) {
				inst = instance;
				if (inst == null) {
					inst = new HBaseDaoAlias();
					instance = inst;
				}

				if (pool == null) {
					Properties props = new Properties();
					props.setProperty("hbase.zookeeper.quorum", hbaseParamsArray[0]);
					props.setProperty("hbase.zookeeper.property.clientPort", hbaseParamsArray[1]);
					props.setProperty("hbase.master", hbaseParamsArray[2]);
					props.setProperty("hbase.rootdir", hbaseParamsArray[3]);

					PoolConfig config = new PoolConfig();
					config.setMaxTotal(15);
					config.setMaxIdle(10);
					config.setMaxWaitMillis(5000);
					config.setTestOnBorrow(true);

					if (props != null) {
						pool = new HbaseConnectionPool(config, props);
					}
				}
			}
		}

		return instance;
	}

	public HConnection getConn() {
		if (pool != null) {
			return pool.getConnection();
		}
		return null;
	}

	public HTableInterface getTable(HConnection conn, String tableName) throws IOException {
		if (conn != null) {
			return conn.getTable(TableName.valueOf(tableName));
		}
		return null;
	}

	public void releaseConn(HConnection conn) {
		if (conn != null && pool != null) {
			pool.returnConnection(conn);
		}
	}

	/**
	 * 保存 hbase 数据
	 * 
	 * @param puts
	 * @throws IOException
	 */
	public void insertDatas(HConnection conn, String tableName, List<Put> puts) throws IOException {
		if (puts == null || puts.isEmpty()) {
			return;
		}

		if (conn != null) {
			HTableInterface table = null;
			try {
				table = this.getTable(conn, tableName);
				if (table != null) {
					table.setAutoFlushTo(false);
					table.put(puts);
					table.flushCommits();
					LOG.info("save {} data to hbase table {}", puts.size(), tableName);
					puts.clear();
				}
			} catch (Exception e) {
				LOG.error("hbase insertDatas fail!", e);
				if (table != null) {
					table.close();
				}
			}
		}
	}

	/**
	 * 保存单条数据
	 * 
	 * @param put
	 * @throws IOException
	 */
	public void insertData(HConnection conn, String tableName, Put put) throws IOException {
		if (conn != null) {
			HTableInterface table = this.getTable(conn, tableName);
			if (table != null) {
				table.setAutoFlushTo(false);
				table.put(put);
				table.flushCommits();
			}
		}
	}

	/**
	 * 字段值自增1
	 * 
	 * @param conn
	 * @param columnName
	 */
	public void increaseColumnValue(HConnection conn, String tableName, String cf, List<String> rowKeys,
			String columnName) throws IOException {
		if (conn != null) {
			HTableInterface table = null;
			try {
				table = this.getTable(conn, tableName);
				if (table != null) {
					table.setAutoFlushTo(false);
					byte[] cfBytes = Bytes.toBytes(cf);
					byte[] cnBytes = Bytes.toBytes(columnName);
					for (String rowkey : rowKeys) {
						table.incrementColumnValue(Bytes.toBytes(rowkey), cfBytes, cnBytes, 1L);
					}
					table.flushCommits();
				}
			} catch (Exception e) {
				LOG.error("hbase insertDatas fail!", e);
				if (table != null) {
					table.close();
				}
			}
		}
	}

	public long getIntColValue(HConnection conn, String tableName, String rowkey, String col) throws IOException {
		Get get = new Get(rowkey.getBytes());
		HTableInterface table = this.getTable(conn, tableName);
		Result rs = table.get(get);
		byte[] colVal = rs.getValue(Bytes.toBytes("cf"), Bytes.toBytes(col));
		if (colVal == null)
			return 0;
		else
			return Integer.parseInt(Bytes.toString(colVal));
	}

	public Boolean[] exists(HConnection conn, String tableName, List<Get> gets) {
		if (conn != null) {
			HTableInterface table = null;

			try {
				table = this.getTable(conn, tableName);
				if (table != null) {
					return table.exists(gets);
				}
			} catch (Exception e) {
				LOG.error("hbase exists fail!", e);
			} finally {
				close(table);
			}
		} else {
			LOG.error("get hbase connection error.");
		}

		return null;
	}

	/**
	 * 从 hbase 获取数据，对于行健不存在，Result返回的是一个EMPTY_RESULT；存在则返回
	 * 
	 * @param conn
	 * @param tableName
	 * @param gets
	 * @return
	 */
	public Result[] gets(HConnection conn, String tableName, List<Get> gets) {
		if (conn != null) {
			HTableInterface table = null;

			try {
				table = this.getTable(conn, tableName);
				if (table != null) {
					LOG.debug("gets size:{}", gets.size());
					return table.get(gets);
				}
			} catch (Exception e) {
				LOG.error("hbase gets fail!", e);
			} finally {
				close(table);
			}
		} else {
			LOG.error("get hbase connection error.");
		}

		return null;
	}

	private void close(HTableInterface table) {
		if (table != null) {
			try {
				table.close();
			} catch (IOException e) {
			}
		}
	}
}
