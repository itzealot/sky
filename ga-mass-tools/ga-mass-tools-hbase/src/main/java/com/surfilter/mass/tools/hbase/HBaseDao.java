package com.surfilter.mass.tools.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.surfilter.commons.pool.PoolConfig;
import com.surfilter.commons.pool.hbase.HbaseConnectionPool;
import com.surfilter.mass.tools.services.Table2TableService;

public class HBaseDao {
	private static Logger LOG = LoggerFactory.getLogger(HBaseDao.class);
	private static HbaseConnectionPool pool = null;

	public static HBaseDao getInstance(Properties props) {
		if (pool == null) {
			synchronized (HBaseDao.class) {
				if (pool == null)
					initPool(props);
			}
		}
		return HBaseDaoAliasNest.dao;
	}

	public static HBaseDao getInstance(String[] hbaseParamsArray) {
		if (pool == null) {
			synchronized (HBaseDao.class) {
				if (pool == null) {
					Properties props = new Properties();

					props.setProperty("hbase.zookeeper.quorum", hbaseParamsArray[0]);
					props.setProperty("hbase.zookeeper.property.clientPort", hbaseParamsArray[1]);
					props.setProperty("hbase.master", hbaseParamsArray[2]);
					props.setProperty("hbase.rootdir", hbaseParamsArray[3]);

					initPool(props);
				}
			}
		}

		return HBaseDaoAliasNest.dao;
	}

	public Connection getConn() {
		return pool != null ? pool.getConnection() : null;
	}

	private Table getTable(Connection conn, String tableName) throws IOException {
		return conn != null ? conn.getTable(TableName.valueOf(tableName)) : null;
	}

	public void releaseConn(Connection conn) {
		if ((conn != null) && (pool != null)) {
			pool.returnConnection(conn);
		}
	}

	public static void closePool() {
		if (pool != null) {
			pool.close();
		}
	}

	public void insertDatas(String tableName, List<Put> puts) throws IOException {
		Connection conn = getConn();
		try {
			if (conn != null) {
				Table table = getTable(conn, tableName);
				if (table != null) {
					table.put(puts);
				} else {
					LOG.error("can't get table, tableName:{}.", tableName);
				}
			} else {
				LOG.error("can't get hbase connection.");
			}
		} finally {
			releaseConn(conn);
		}
	}

	public void tableTransport(String source_table, String dest_table, String column_family, int batch, String rowStart) throws Exception {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.rpc.timeout", "60000");
		conf.set("hbase.client.operation.timeout", "60000");//单个操作执行时间总和
		conf.set("hbase.client.scanner.caching", "1000");//scan单次rpc缓存的数据量，默认是100
		conf.set("hbase.client.scanner.timeout.period", "60000");//scan单次rpc调用的过期时间
		conf.set("hbase.client.retries.number", "2");//失败重试次数
//		Connection conn = getConn();
		Connection conn = ConnectionFactory.createConnection(conf);
		List<Put> puts = new ArrayList<>(batch);
		String[] row_key_split;
//		Scan scan = new Scan();
		long break_point = 0L;
		byte[] cf = Bytes.toBytes(column_family);
		long putTimeTotal = 0L;
		try {
			if (conn != null) {
				Table table_src = getTable(conn, source_table);
				if (table_src != null) {
//					Iterator<Result> rst_it = table_src.getScanner(cf).iterator(); // 根据行健获取结果
					Iterator<Result> rst_it = table_src.getScanner(new Scan().setStartRow(rowStart.getBytes())).iterator(); // 根据行健获取结果
					// Iterator<Result> rst_it =
					// table_src.getScanner(scan).iterator(); // 根据行健获取结果
					long start_time = System.currentTimeMillis();
					boolean isCert = source_table.concat(dest_table).contains("certification")? true : false;
					boolean isRel = source_table.concat(dest_table).contains("relation")? true : false;
					if(!isCert && !isRel){
						LOG.warn("ATTENTION: SOURCE OR DEST TABLE IS NOT CERTIFICATION OR RELATION, IS GOING TO JUST COPY THE TABLE ! ! !");
					}
					byte[] rowKey = null;
					byte[] rowKeyNew = null;
					
					while (rst_it.hasNext()) {
						String rowKeyStr = null;
						// 读取数据
						try {
							Result rst = rst_it.next();
							rowKey = rst.getRow();
							rowKeyStr = new String(rowKey);
							row_key_split = rowKeyStr.split("\\|");
							LOG.debug("row key get: {}", rowKeyStr);
							//已加上前缀hash的key不转换，可能出现在更新streaming后未先预分区-导数据
							if (isCert) {
								rowKeyNew = row_key_split.length==3 ? rowKey : MD5Hash.getMD5AsHex(rowKey).substring(0, 8).concat("|").concat(rowKeyStr).getBytes();
							}else if(isRel){
								rowKeyNew = row_key_split.length==5 ? rowKey 
										: MD5Hash.getMD5AsHex(row_key_split[0].concat("|").concat(row_key_split[1]).getBytes()).substring(0, 8).concat("|").concat(rowKeyStr).getBytes();
							}else{
								rowKeyNew = rowKey;//非关系或身份表时，只是表复制
							}
							LOG.debug("hash row key get: {}", new String(rowKeyNew));
							Put put = new Put(rowKeyNew);
							NavigableMap<byte[], byte[]> col_val = rst.getFamilyMap(cf);
							for (byte[] col : col_val.keySet()) {
								put.addColumn(cf, col, col_val.get(col));
//								LOG.debug("cf, col, value: {},{},{}", cf, col, col_val.get(col));
							}
							puts.add(put);
							break_point++;
							LOG.debug("puts length: {}", puts.size());
						} catch (Exception e) {
							LOG.error("table:{},row:{} get error!{}", source_table, rowKeyStr);
							e.printStackTrace();
							throw e;
						}
						// 写入数据
						if (break_point % batch == 0) {
							long putStartTime = System.currentTimeMillis();
							try {
								insertDatas(dest_table, puts);
								puts.clear();
								LOG.info("rows have been put:{}, rowkey:{}", break_point,rowKeyStr);
								Table2TableService.stopRow = rowKeyStr;
								Table2TableService.rowsCount = break_point;
								Thread.sleep(500);
							} catch (Exception e) {
								LOG.error("insert hbase error,{}:{}-{} keys have inserted.exit...{}", source_table, break_point - batch, break_point, e);
								e.printStackTrace();
								throw e;
							}
							putTimeTotal=(putTimeTotal+System.currentTimeMillis()-putStartTime) ;
						}
					}
					insertDatas(dest_table, puts);
					Table2TableService.stopRow = null;
					LOG.info("Put time spended: {}s", putTimeTotal/1000);
					LOG.info("Total time spended: {}s", (System.currentTimeMillis()-start_time)/1000);
				} else {
					LOG.error("can't get table, tableName:{}.", source_table);
				}
			} else {
				LOG.error("can't get hbase connection.");
			}
		}catch (Exception e) {
			LOG.error("Hbase Scan failed!!!");
			e.printStackTrace();
//			return rowStop;
		} finally {
//			releaseConn(conn);
			conn.close();
		}
//		return null;
	}

	public Cell[] getByRowKey(String tableName, String columnFamily, String rowKey) throws IOException {
		Connection conn = getConn();
		byte[] family = Bytes.toBytes(columnFamily);

		try {
			if (conn != null) {
				Table table = getTable(conn, tableName);
				if (table != null) {
					Result r = table.get(new Get(Bytes.toBytes(rowKey)).addFamily(family)); // 根据行健获取结果
					if (r != null)
						return r.rawCells();
				} else {
					LOG.error("can't get table, tableName:{}.", tableName);
				}
			} else {
				LOG.error("can't get hbase connection.");
			}
		} finally {
			releaseConn(conn);
		}
		return null;
	}

	/**
	 * 根据行健,列族,列名获取 Cell 值
	 * 
	 * @param tableName
	 * @param columnFamily
	 * @param columnName
	 * @param rowKey
	 * @return
	 * @throws IOException
	 */
	public byte[] getByRowKey(String tableName, String columnFamily, String columnName, String rowKey) throws IOException {
		Connection conn = getConn();
		byte[] family = Bytes.toBytes(columnFamily);
		byte[] qualifier = Bytes.toBytes(columnName);

		try {
			if (conn != null) {
				Table table = getTable(conn, tableName);
				if (table != null) {
					Result r = table.get(new Get(Bytes.toBytes(rowKey)).addColumn(family, qualifier)); // 根据行健获取结果
					if (r != null)
						return r.getValue(family, qualifier);
				} else {
					LOG.error("can't get table, tableName:{}.", tableName);
				}
			} else {
				LOG.error("can't get hbase connection.");
			}
		} finally {
			releaseConn(conn);
		}
		return null;
	}

	/**
	 * 批量根据行健,列族,列名获取 Cell 值
	 * 
	 * @param tableName
	 * @param columnFamily
	 * @param columnName
	 * @param rowKeys
	 * @return
	 * @throws IOException
	 */
	public List<byte[]> getByRowKeys(String tableName, String columnFamily, String columnName, List<String> rowKeys) throws IOException {
		Connection conn = getConn();
		byte[] family = Bytes.toBytes(columnFamily);
		byte[] qualifier = Bytes.toBytes(columnName);
		List<byte[]> bytes = Lists.newArrayListWithCapacity(rowKeys.size());

		try {
			if (conn != null) {
				Table table = getTable(conn, tableName);
				List<Get> gets = Lists.newArrayListWithCapacity(rowKeys.size());
				if (table != null) {
					for (String row : rowKeys) {
						gets.add(new Get(Bytes.toBytes(row)).addColumn(family, qualifier));
					}

					Result[] rs = table.get(gets); // 根据行健获取结果
					if (rs != null) {
						for (Result r : rs) {
							if (r != null && !r.isEmpty()) {
								bytes.add(r.getValue(family, qualifier));
							} else {
								bytes.add(null);
							}
						}
					}
				} else {
					LOG.error("can't get table, tableName:{}.", tableName);
				}

			} else {
				LOG.error("can't get hbase connection.");
			}
		} finally {
			releaseConn(conn);
		}

		return bytes;
	}

	public void deleteCol(String tableName, List<Delete> deletes) throws IOException {
		Connection conn = getConn();
		try {
			if (conn != null) {
				Table table = getTable(conn, tableName);
				if (table != null) {
					table.delete(deletes);
				} else {
					LOG.error("can't get table, tableName:{}.", tableName);
				}
			} else {
				LOG.error("can't get hbase connection.");
			}
		} finally {
			releaseConn(conn);
		}
	}

	public void insertData(Connection conn, String tableName, Put put) throws IOException {
		if (conn != null) {
			Table table = getTable(conn, tableName);
			if (table != null) {
				table.put(put);
			} else {
				LOG.error("can't get table, tableName:{}.", tableName);
			}
		}
	}

	public void deleteByRowkeys(String tableName, String[] rowkeys) throws IOException {
		Connection conn = getConn();
		try {
			if (conn != null) {
				Table table = getTable(conn, tableName);
				if (table != null) {
					List<Delete> deletes = Lists.newArrayListWithCapacity(rowkeys.length);
					for (String s : rowkeys) {
						LOG.debug("delete rowkey:{}", s);
						deletes.add(new Delete(Bytes.toBytes(s)));
					}
					table.delete(deletes);
				} else {
					LOG.error("can't get table, tableName:{}.", tableName);
				}
			}
		} finally {
			releaseConn(conn);
		}
	}

	public void deleteByRowkeys(String tableName, List<String> rowkeys) throws IOException {
		Connection conn = getConn();
		try {
			if (conn != null) {
				Table table = getTable(conn, tableName);
				if (table != null) {
					List<Delete> deletes = Lists.newArrayListWithCapacity(rowkeys.size());
					for (String s : rowkeys) {
						deletes.add(new Delete(Bytes.toBytes(s)));
					}
					table.delete(deletes);
				} else {
					LOG.error("can't get table, tableName:{}.", tableName);
				}
			}
		} finally {
			releaseConn(conn);
		}
	}

	public void increaseColumnValue(Connection conn, String tableName, String cf, List<String> rowKeys, String columnName) throws IOException {
		if (conn != null) {
			Table table = getTable(conn, tableName);
			if (table != null) {
				for (String rowkey : rowKeys) {
					table.incrementColumnValue(Bytes.toBytes(rowkey), Bytes.toBytes(cf), Bytes.toBytes(columnName), Long.valueOf(1L).longValue());
				}
			} else {
				LOG.error("can't get table, tableName:{}.", tableName);
			}
		}
	}

	public void modifyColumn(String tableName, List<String> rowkeys, byte[] family, String deleteColumn, String addColumn) throws IOException {
		Connection conn = getConn();
		try {
			if (conn != null) {
				Table table = getTable(conn, tableName);
				if (table != null) {
					List<Delete> deletes = Lists.newArrayListWithCapacity(rowkeys.size());
					List<Put> puts = Lists.newArrayListWithCapacity(rowkeys.size());

					byte[] delQualifier = Bytes.toBytes(deleteColumn);
					byte[] addQualifier = Bytes.toBytes(addColumn);

					for (String s : rowkeys) {
						deletes.add(new Delete(Bytes.toBytes(s)).addColumn(family, delQualifier));
						puts.add(new Put(Bytes.toBytes(s)).addColumn(family, addQualifier, Bytes.toBytes(1)));
					}

					table.put(puts);
					table.delete(deletes);
				}
			}
		} finally {
			releaseConn(conn);
		}
	}

	private static void initPool(Properties props) {
		PoolConfig config = new PoolConfig();

		config.setMaxTotal(15);
		config.setMaxIdle(5);
		config.setMaxWaitMillis(1000L);
		config.setTestOnBorrow(true);

		if (props != null) {
			pool = new HbaseConnectionPool(config, props);
		}
	}

	static class HBaseDaoAliasNest {
		private static HBaseDao dao = new HBaseDao();
	}
}
