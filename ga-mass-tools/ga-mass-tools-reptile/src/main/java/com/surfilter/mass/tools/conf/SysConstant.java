package com.surfilter.mass.tools.conf;

/**
 * constant
 * 
 * @author zealot
 *
 */
public final class SysConstant {

	public static final String TOOLS_SETTINGS = "tools.settings";

	public static final String LOGIN_USERNAME = "login.username";
	public static final String LOGIN_PASSWORD = "login.password";
	public static final String LOGIN_OTHERS = "login.others";

	public static final String COOKIE_SESSION_KEY = "cookie.session.key";
	public static final String COOKIE_SESSION_DEFAULT_VALUE = "JSESSIONID";
	public static final String COOKIE_USERNAME_KEY = "cookie.username.key";
	public static final String COOKIE_PASSWORD_KEY = "cookie.password.key";

	public static final String LOGIN_HTTPS_HOST = "login.https.host";
	public static final String LOGIN_HTTPS_PORT = "login.https.port";
	public static final String LOGIN_HTTPS_FILE = "login.https.file";

	public static final String LOGIN_HTTP_URL = "login.http.url";
	public static final String LOGIN_HTTPS_FLAG = "login.https.flag";

	/** config for catch */
	public static final String TIMER_INTERVAL = "timer.interval";
	public static final int TIMER_INTERVAL_DEFAULT = 10;
	public static final String CATCH_URL = "catch.url";

	/** params for page */
	public static final String PAGE_URL_FLAG = "page.url.flag";
	public static final String PAGE_URL_FILE = "page.url.file";
	public static final String PAGE_LABEL_TOTAL_COUNTS = "page.label.totalCount"; // 记录数
	/** */
	public static final String PAGE_LABEL_TOTAL_PAGE = "page.start";
	/** 分页大小的 label */
	public static final String PAGE_LABEL_PAGE_SIZE = "page.label.pageSize";

	/** config for jdbc */
	public static final String JDBC_URL = "jdbc.url";
	public static final String JDBC_USERNAME = "jdbc.username";
	public static final String JDBC_PASSWORD = "jdbc.password";

	private SysConstant() {
	}
}
