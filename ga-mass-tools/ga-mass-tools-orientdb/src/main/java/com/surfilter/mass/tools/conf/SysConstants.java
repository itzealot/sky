package com.surfilter.mass.tools.conf;

public final class SysConstants {
	public static final String HBASE_ZK_URL = "hbase.zookeeper.quorum";
	public static final String HBASE_ZK_PORT = "hbase.zookeeper.property.clientPort";
	public static final String HBASE_MASTER = "hbase.master";
	public static final String HBASE_ROOT_DIR = "hbase.rootdir";

	public static final String HBASE_KEY_SPLITER = "|";

	public static final String VERSION_IS_FXJ = "version.is.fxj";
	
	//帐号需要过滤的字符集
	public static final String ACCOUNT_FILTER = "\",\\,/,',>,<,|,?, ,=,+,[,],{,},%,;,&,^,!,(,)";
	
	//关系文件中字段分隔符
	public static final String RELATION_FILE_SPLITER = "\t";

	private SysConstants() {
	}
}
