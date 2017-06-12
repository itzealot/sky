package com.surfilter.mass.tools.conf;

import static com.surfilter.mass.tools.util.ValidateUtil.validate;
import static com.surfilter.mass.tools.util.ValidateUtil.validateOthers;
import static com.surfilter.mass.tools.util.ValidateUtil.validateProperty;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reptile Config
 * 
 * @author zealot
 *
 */
public class ReptileConfig {

	static final Logger LOG = LoggerFactory.getLogger(ReptileConfig.class);

	private MassConfiguration conf;

	private String[] username;
	private String[] password;
	private String[] others;
	private String sessionKey;
	private String loginUrl;
	private String host;
	private Integer port;
	private String file;
	private JdbcConfig jdbcConfig;
	private boolean https;

	private int timeInterval;
	private String catchUtl;

	public ReptileConfig(MassConfiguration conf) {
		this.conf = conf;
		init();
	}

	public void init() {
		/* init login info */
		this.username = validate(conf.get(SysConstant.LOGIN_USERNAME), SysConstant.LOGIN_USERNAME);
		this.password = validate(conf.get(SysConstant.LOGIN_PASSWORD), SysConstant.LOGIN_PASSWORD);
		this.others = validateOthers(conf.get(SysConstant.LOGIN_OTHERS), SysConstant.LOGIN_OTHERS);
		this.sessionKey = validateProperty(conf.get(SysConstant.COOKIE_SESSION_KEY),
				"empty setting for cookie.session.key config");
		this.https = conf.getBoolean(SysConstant.LOGIN_HTTPS_FLAG);
		if (this.https) {
			this.host = validateProperty(conf.get(SysConstant.LOGIN_HTTPS_HOST), SysConstant.LOGIN_HTTPS_HOST);
			this.port = Integer
					.parseInt(validateProperty(conf.get(SysConstant.LOGIN_HTTPS_PORT), SysConstant.LOGIN_HTTPS_PORT));
			this.file = validateProperty(conf.get(SysConstant.LOGIN_HTTPS_FILE), SysConstant.LOGIN_HTTPS_FILE);
		} else {
			this.loginUrl = validateProperty(conf.get(SysConstant.LOGIN_HTTP_URL), SysConstant.LOGIN_HTTP_URL);
		}

		/* init jdbc info */
		initJdbc();

		/* init catch info */
		this.catchUtl = validateProperty(conf.get(SysConstant.CATCH_URL), "empty setting for catch.url config");
		this.timeInterval = conf.getInt(SysConstant.TIMER_INTERVAL, SysConstant.TIMER_INTERVAL_DEFAULT);

		LOG.debug("settings for app:{}", this);
	}

	private void initJdbc() {
		String url = validateProperty(conf.get(SysConstant.JDBC_URL), SysConstant.JDBC_URL);
		String jdbcUsername = validateProperty(conf.get(SysConstant.JDBC_USERNAME), SysConstant.JDBC_USERNAME);
		String jdbcPassword = validateProperty(conf.get(SysConstant.JDBC_PASSWORD), SysConstant.JDBC_PASSWORD);
		this.jdbcConfig = new JdbcConfig(JdbcUtils.DRIVER_CLASS_NAME, url, jdbcUsername, jdbcPassword);
	}

	public String[] getUsername() {
		return username;
	}

	public String[] getPassword() {
		return password;
	}

	public String getUsernameKey() {
		return username[0];
	}

	public String getUsernameValue() {
		return username[1];
	}

	public String getPasswordKey() {
		return password[0];
	}

	public String getPasswordValue() {
		return password[1];
	}

	public String[] getOthers() {
		return others;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getFile() {
		return file;
	}

	public boolean isHttps() {
		return https;
	}

	public int getTimeInterval() {
		return timeInterval;
	}

	public String getCatchUtl() {
		return catchUtl;
	}

	public MassConfiguration getConf() {
		return conf;
	}

	public JdbcConfig getJdbcConfig() {
		return jdbcConfig;
	}

	@Override
	public String toString() {
		return "ReptileConfig [username=" + Arrays.toString(username) + ", password=" + Arrays.toString(password)
				+ ", others=" + Arrays.toString(others) + ", sessionKey=" + sessionKey + ", loginUrl=" + loginUrl
				+ ", host=" + host + ", port=" + port + ", file=" + file + ", jdbcConfig=" + jdbcConfig + ", https="
				+ https + ", timeInterval=" + timeInterval + ", catchUtl=" + catchUtl + "]";
	}

}
