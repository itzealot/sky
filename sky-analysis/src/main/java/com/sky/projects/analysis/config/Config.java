package com.sky.projects.analysis.config;

import com.sky.projects.analysis.util.SystemConfig;

public abstract interface Config {

	public static final String MAINCLASS = SystemConfig.getString("mainclass");
	public static final String ZOOKEEPER_ADDRESS = SystemConfig.getString("dg.zookeeper.address");
	public static final String SPARK_URL = SystemConfig.getString("dg.spark.url");
	public static final String SHUFFLE_TYPE = SystemConfig.getString("spark.shuffle.blockTransferService");
	public static final int EXECUTE_INTERVAL = Integer.parseInt(SystemConfig.getString("dg.spark.duration"));
	public static final String KAFKA_BROKERS = SystemConfig.getString("dg.broker.list");
	public static final String TOPICS = SystemConfig.getString("dg.kafka.topics");
	public static final int KAFKA_MAX_PARTITIONS = Integer.parseInt(SystemConfig.getString("kafka.max.partitions"));
	public static final int IPHONE_RATIO = Integer.parseInt(SystemConfig.getString("dg.iphone.ratio"));
	public static final String DB_IP = SystemConfig.getString("dg.jdbc.ip");
	public static final int DB_PORT = Integer.parseInt(SystemConfig.getString("dg.jdbc.port"));
	public static final String DB = SystemConfig.getString("dg.jdbc.database");
	public static final String DB_USER = SystemConfig.getString("dg.jdbc.user");
	public static final String DB_PWD = SystemConfig.getString("dg.jdbc.password");
	public static final String KAFKA_GROUP_HEATANDWATCH = SystemConfig.getString("dg.kafkagroup.heatAndWatch");
	public static final String WAREHOUSE_BASE_DIR = SystemConfig.getString("dg.hive.track.hdfsDir");
	public static final int WINDOW_CAPACITY = Integer.parseInt(SystemConfig.getString("dg.spark.window.capacity"));
	public static final String isDistinctByEach = SystemConfig.getString("dg.spark.distinctByEach");
	public static final int DELAY_START_TIME_SECONDS = Integer
			.parseInt(SystemConfig.getString("dg.delayminute.starttime")) * 60;
	public static final int MIGRATED_DESC = Integer.parseInt(SystemConfig.getString("dg.spark.migrate.desc"));
}
