package com.surfilter.mass.dao.db;

import org.apache.commons.lang3.StringUtils;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.ImcaptureConsts;

/**
 * JDBC 配置实体
 * 
 * @author hapuer
 */
public class JdbcConfig {

	private String driverClassName;
	private String url; // jdbc url
	private String password;
	private String userName;
	private int focusCounts;

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

					instance = new JdbcConfig(ImcaptureConsts.DRIVER_CLASS_NAME, url, userName, password,
							conf.getInt(ImcaptureConsts.FOCUS_MAC_INFO_COUNTS, 100));
				}
			}
		}
		return instance;
	}

	private JdbcConfig(String driverClassName, String url, String userName, String password, int focusCounts) {
		super();
		this.driverClassName = driverClassName;
		this.url = url;
		this.password = password;
		this.userName = userName;
		this.focusCounts = focusCounts;
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

	public int getFocusCounts() {
		return focusCounts;
	}
}
