package com.surfilter.mass.tools.conf;

import static com.surfilter.mass.tools.util.ValidateUtil.validateProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hapuer
 */
public class JdbcConfig {

	static final Logger LOG = LoggerFactory.getLogger(JdbcConfig.class);

	private String driverClassName;
	private String url;
	private String password;
	private String userName;

	public JdbcConfig(String driverClassName, String url, String userName, String password) {
		super();
		this.driverClassName = driverClassName;
		this.url = url;
		this.password = password;
		this.userName = userName;
	}

	public JdbcConfig(MassConfiguration conf) {
		super();
		this.driverClassName = JdbcUtils.DRIVER_CLASS_NAME;
		this.url = validateProperty(conf.get(SysConstant.JDBC_URL), SysConstant.JDBC_URL);
		this.userName = validateProperty(conf.get(SysConstant.JDBC_USERNAME), SysConstant.JDBC_USERNAME);
		this.password = validateProperty(conf.get(SysConstant.JDBC_PASSWORD), SysConstant.JDBC_PASSWORD);

		LOG.debug("{}", this);
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "JdbcConfig [driverClassName=" + driverClassName + ", url=" + url + ", password=" + password
				+ ", userName=" + userName + "]";
	}

}
