package com.surfilter.mass.dao.db;

import org.apache.commons.lang3.StringUtils;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.ImcaptureConsts;

/**
 * JDBC 配置实体
 * 
 * @author zealot
 */
public class JdbcConfig {

	private String driverClassName;
	private String url; // jdbc url
	private String password;
	private String userName;

	public static JdbcConfig instance = null;

	public static JdbcConfig getInstance(MassConfiguration conf) {
		if (instance == null) {
			synchronized (JdbcConfig.class) {
				if (instance == null) {
					String url = conf.get(ImcaptureConsts.JDBC_URL);
					String userName = conf.get(ImcaptureConsts.JDBC_USER_NAME);
					String password = conf.get(ImcaptureConsts.JDBC_PASS_WORD);

					if (StringUtils.isEmpty(url) || StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
						throw new IllegalArgumentException("jdbc.url, jdbc.userName or jdbc.password can't be empty");
					}

					instance = new JdbcConfig(ImcaptureConsts.DRIVER_CLASS_NAME, url, userName, password);
				}
			}
		}
		return instance;
	}

	private JdbcConfig(String driverClassName, String url, String userName, String password) {
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
