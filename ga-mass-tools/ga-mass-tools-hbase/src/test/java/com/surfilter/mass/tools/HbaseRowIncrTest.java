package com.surfilter.mass.tools;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import junit.framework.TestCase;

public class HbaseRowIncrTest extends TestCase {

	// HBASE TABLE
	public static final String CF_NAME = "cf";
	public static final byte[] CF_NAME_BYTES = Bytes.toBytes(CF_NAME);
	public static final byte[] FIRST_TERMINAL_NUM_BYTES = Bytes.toBytes("first_terminal_num");
	public static final byte[] FIRST_START_TIME_BYTES = Bytes.toBytes("first_start_time");
	public static final byte[] DISCOVER_TIMES_BYTES = Bytes.toBytes("discover_times");
	public static final byte[] TES_ROW_KEY_BYTES = Bytes.toBytes("test_row_key");

	private static final TableName RRELATION_TABLE = TableName.valueOf("relation");

	public void testIncrementColumnValue() throws Exception {
		Configuration configuration = new Configuration();

		configuration.set("hbase.zookeeper.quorum", "rzx168,rzx169,rzx177");
		configuration.set("hbase.zookeeper.property.clientPort", "2181");

		Connection conn = org.apache.hadoop.hbase.client.ConnectionFactory.createConnection(configuration);

		if (conn != null) {
			Table table = null;
			try {
				table = conn.getTable(RRELATION_TABLE);
				if (table != null) {
					table.incrementColumnValue(TES_ROW_KEY_BYTES, CF_NAME_BYTES, DISCOVER_TIMES_BYTES, 1L);
				}

				Get get = new Get(TES_ROW_KEY_BYTES);
				System.out.println(table.get(get));
			} finally {
				if (table != null)
					table.close();
			}
		}
	}
}
