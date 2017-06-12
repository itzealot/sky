package com.surfilter.mass.tools.conf;

public class JRedisPoolConfig {

	public static final String STR_DELIMIT = "\\|";
	public static final int MAX_ACTIVE = 100;
	public static final int MAX_IDLE = 100;
	public static final long MAX_WAIT = 1000;

	public static final boolean TEST_ON_BORROW = true;

	public static final boolean TEST_ON_RETURN = true;
}
