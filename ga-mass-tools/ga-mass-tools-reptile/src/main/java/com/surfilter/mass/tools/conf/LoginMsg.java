package com.surfilter.mass.tools.conf;

/**
 * Login msg for return
 * 
 * @author zealot
 *
 */
public final class LoginMsg {

	public static final LoginMsg LOGIN_SUCCESS = new LoginMsg("login success.");
	public static final LoginMsg LOGIN_FAILED = new LoginMsg("login failed.");

	private String msg;

	public LoginMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	@Override
	public String toString() {
		return "LoginMsg [msg=" + msg + "]";
	}

}
