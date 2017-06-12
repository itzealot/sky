package com.surfilter.mass.tools.util;

import org.apache.hadoop.hbase.util.MD5Hash;

public final class HbaseToolsUtil {
	private static final String CP = "|";
	private static final String DP = "\\|";

	public static String addRelationHashPrefix(String rowkey, int lenth) {
		String[] rk = rowkey.split(DP);
		String from = rk[0] + CP + rk[1];
		return MD5Hash.getMD5AsHex(from.getBytes()).substring(0, lenth) + CP + rowkey;
	}

	public static String addHashPrefix(String rowkey, int lenth) {
		return MD5Hash.getMD5AsHex(rowkey.getBytes()).substring(0, lenth) + CP + rowkey;
	}

	private HbaseToolsUtil() {
	}
}
