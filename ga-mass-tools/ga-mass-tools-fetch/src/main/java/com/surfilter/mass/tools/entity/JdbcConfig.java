package com.surfilter.mass.tools.entity;

/**
 * JDBC 配置实体
 * 
 * @author zealot
 */
public class JdbcConfig {

	public static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
	public static final String HIVE_DRIVER_CLASS_NAME = "org.apache.hive.jdbc.HiveDriver";

	private String driverClassName;
	private String url; // jdbc url
	private String password;
	private String userName;

	public JdbcConfig(String driverClassName, String url, String userName, String password) {
		super();
		this.driverClassName = driverClassName;
		this.url = url;
		this.password = password;
		this.userName = userName;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public String getPassword() {
		return password;
	}

	public String getUserName() {
		return userName;
	}
}
