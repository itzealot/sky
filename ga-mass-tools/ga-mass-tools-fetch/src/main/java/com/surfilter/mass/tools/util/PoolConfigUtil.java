package com.surfilter.mass.tools.util;

import java.util.Properties;

import com.surfilter.commons.pool.PoolConfig;
import com.surfilter.commons.pool.hbase.HbaseConfig;
import com.surfilter.commons.pool.hive.HiveJdbcConfig;
import com.surfilter.commons.pool.jdbc.JdbcConfig;

/**
 * PoolConfigUtil
 * 
 * @author zealot
 *
 */
public final class PoolConfigUtil {

	/**
	 * 初始化连接池配置信息
	 * 
	 * @return
	 */
	public static PoolConfig initPoolConfig() {
		PoolConfig poolConfig = new PoolConfig();

		poolConfig.setMaxTotal(15);
		poolConfig.setMaxIdle(5);
		poolConfig.setMaxWaitMillis(5000);
		poolConfig.setTestOnBorrow(true);

		return poolConfig;
	}

	/**
	 * 根据 JdbcConfig 对象初始化 Properties
	 * 
	 * @param config
	 * @return
	 */
	public static Properties initJdbcProperties(com.surfilter.mass.tools.entity.JdbcConfig config) {
		Properties props = new Properties();

		props.setProperty(JdbcConfig.DRIVER_CLASS_PROPERTY, config.getDriverClassName());
		props.setProperty(JdbcConfig.JDBC_URL_PROPERTY, config.getUrl());
		props.setProperty(JdbcConfig.JDBC_USERNAME_PROPERTY, config.getUserName());
		props.setProperty(JdbcConfig.JDBC_PASSWORD_PROPERTY, config.getPassword());

		return props;
	}

	public static Properties initHiveProperties(com.surfilter.mass.tools.entity.JdbcConfig config) {
		Properties props = new Properties();

		props.setProperty(HiveJdbcConfig.DRIVER_CLASS_PROPERTY, config.getDriverClassName());
		props.setProperty(HiveJdbcConfig.JDBC_URL_PROPERTY, config.getUrl());

		return props;
	}

	public static Properties initImpalaProperties(com.surfilter.mass.tools.entity.JdbcConfig config) {
		Properties props = new Properties();

		props.setProperty(HiveJdbcConfig.DRIVER_CLASS_PROPERTY, config.getDriverClassName());
		props.setProperty(HiveJdbcConfig.JDBC_URL_PROPERTY, config.getUrl());

		if (config.getUrl().contains(",")) {
			props.put("client-type", "load-balance");
		}

		return props;
	}

	public static Properties initKafkaProducerProperties(String brokers) {
		Properties props = new Properties();

		props.setProperty("metadata.broker.list", brokers);
		// props.setProperty("producer.type", "async");
		props.setProperty("request.required.acks", "-1");
		// props.setProperty("compression.codec", "snappy"); // gzip
		props.setProperty("topic.metadata.refresh.interval.ms", "60000");
		props.setProperty("batch.num.messages", "2000"); // for async

		return props;
	}

	public static Properties initHbaseProperties(String[] hbaseParams) {
		Properties props = new Properties();

		props.setProperty(HbaseConfig.ZOOKEEPER_QUORUM_PROPERTY, hbaseParams[0]);
		props.setProperty(HbaseConfig.ZOOKEEPER_CLIENTPORT_PROPERTY, hbaseParams[1]);
		props.setProperty(HbaseConfig.MASTER_PROPERTY, hbaseParams[2]);
		props.setProperty(HbaseConfig.DEFAULT_ROOTDIR, hbaseParams[3]);

		return props;
	}

	private PoolConfigUtil() {
	}
}
