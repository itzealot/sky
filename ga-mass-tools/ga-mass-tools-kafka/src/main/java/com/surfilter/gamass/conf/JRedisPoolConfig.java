package com.surfilter.gamass.conf;

public final class JRedisPoolConfig {

	/** redis pool config */
	public static final String STR_DELIMIT = "\\|";
	public static final String VALUE_SPLITER = "|";
	public static final int MAX_ACTIVE = 50;
	public static final int MAX_IDLE = 20;
	public static final long MAX_WAIT = 1000 * 1000;
	public static final boolean TEST_ON_BORROW = true;
	public static final boolean TEST_ON_RETURN = true;

	/** certification redis key */
	public static final String CERTIFICATION_MAC_PREFIX_KEY = "m_";
	public static final String CERTIFICATION_MOBILE_KEY = "certification_mobile";
	public static final String CERTIFICATION_IMEI_KEY = "certification_imei";
	public static final String CERTIFICATION_QQ_KEY = "certification_qq";
	public static final String CERTIFICATION_WX_KEY = "certification_wx";
	public static final String CERTIFICATION_OTHER_KEY = "certification_other";

	private JRedisPoolConfig() {
	}
}
