package com.sky.project.share.tool.hbase;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.project.share.common.pool.PoolConfig;
import com.sky.project.share.common.pool.hbase.HbaseConnectionPool;

/**
 * 升级hbase api为1.x
 * 
 * @author zealot
 */
public class HBaseDaoAlias {

	private static Logger LOG = LoggerFactory.getLogger(HBaseDaoAlias.class);
	private static HbaseConnectionPool pool;

	private HBaseDaoAlias() {
	}

	public static HBaseDaoAlias getInstance(Properties props) {
		if (pool == null) {
			synchronized (HBaseDaoAlias.class) {
				if (pool == null) {
					initPool(props);
				}
			}
		}
		return HBaseDaoAliasNest.dao;
	}

	public static HBaseDaoAlias getInstance(String[] hbaseParamsArray) {
		if (pool == null) {
			synchronized (HBaseDaoAlias.class) {
				if (pool == null) {
					Properties props = new Properties();
					props.setProperty("hbase.zookeeper.quorum", hbaseParamsArray[0]);
					props.setProperty("hbase.zookeeper.property.clientPort", hbaseParamsArray[1]);
					props.setProperty("hbase.master", hbaseParamsArray[2]);
					props.setProperty("hbase.rootdir", hbaseParamsArray[3]);

					PoolConfig config = new PoolConfig();
					config.setMaxTotal(20);
					config.setMaxIdle(12);
					config.setMaxWaitMillis(5000);
					config.setTestOnBorrow(true);

					if (props != null) {
						pool = new HbaseConnectionPool(config, props);
					}
				}
			}
		}
		return HBaseDaoAliasNest.dao;
	}

	public HConnection getConn() {
		if (pool != null) {
			return pool.getConnection();
		}
		return null;
	}

	public void increaseColumnValue(HConnection conn, TableName tableName, byte[] cf, List<byte[]> rowKeys,
			byte[] columnName, long times) throws IOException {
		if (conn != null) {
			HTableInterface table = null;
			try {
				table = conn.getTable(tableName);
				if (table != null) {
					for (byte[] rowkey : rowKeys) {
						table.incrementColumnValue(rowkey, cf, columnName, times);
					}
				}
			} finally {
				close(table);
			}
		}
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
	 * 保存hbase数据
	 * 
	 * @param puts
	 * @throws IOException
	 */
	public void insertDatas(HConnection conn, String tableName, List<Put> puts) {
		if (conn != null) {
			HTableInterface table = null;

			try {
				table = this.getTable(conn, tableName);
				if (table != null) {
					table.put(puts);
				} else {
					LOG.error("get table name error, table: {}", tableName);
				}
				if (LOG.isDebugEnabled())
					LOG.debug("save {} data to hbase table {}", puts.size(), tableName);
			} catch (IOException e) {
				LOG.error("insert data into hbase error, table: {}, {}", tableName, e);
			} finally {
				close(table);
			}
		}
	}

	/**
	 * 保存单条数据
	 * 
	 * @param put
	 * @throws IOException
	 */
	public void insertData(HConnection conn, String tableName, Put put) {
		if (conn != null) {
			HTableInterface table = null;
			try {
				table = this.getTable(conn, tableName);
				if (table != null) {
					table.put(put);
				}
			} catch (IOException e) {
				LOG.error("insert data error! table: {}, {}", tableName, e);
			} finally {
				close(table);
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
					byte[] columnNameBytes = Bytes.toBytes(columnName);
					byte[] cfBytes = Bytes.toBytes(cf);
					long times = 1l;
					for (String rowkey : rowKeys) {
						table.incrementColumnValue(Bytes.toBytes(rowkey), cfBytes, columnNameBytes, times);
					}
				}
			} catch (IOException e) {
				LOG.error("increase column value error! table: {}, columnName: {}, {}", tableName, columnName, e);
			} finally {
				close(table);
			}
		}
	}

	private static void initPool(Properties props) {
		PoolConfig config = new PoolConfig();
		config.setMaxTotal(20);
		config.setMaxIdle(12);
		config.setMaxWaitMillis(1000);
		config.setTestOnBorrow(true);

		if (props != null) {
			pool = new HbaseConnectionPool(config, props);
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

	static class HBaseDaoAliasNest {
		private static HBaseDaoAlias dao = new HBaseDaoAlias();
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
