package com.surfilter.mass;

/**
 * 1:MAC地址,2:手机号,3:IMEI号,4:IMSI号,5:证件号码,6:虚拟身份,7:APP协议
 * 
 * @author zealot
 */
public enum MatchType {

	MAC(1, 'M'), PHONE(2, 'P'), IMEI(3, 'I'), IMSI(4, 'S'), CERT(5, 'C'), ACCOUNT(6, 'A'), PROTOCOL(7, 'X');

	private int code = 0;
	private char simCode;

	MatchType(int code, char simCode) {
		this.code = code;
		this.simCode = simCode;
	}

	public int getCode() {
		return this.code;
	}

	public char getSimCode() {
		return simCode;
	}

}
