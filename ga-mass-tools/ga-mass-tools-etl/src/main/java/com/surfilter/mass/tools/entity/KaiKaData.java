package com.surfilter.mass.tools.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class KaiKaData implements Serializable {

	private String SERVICE_CODE;
	private String USER_NAME;
	private String USER_PASS;
	private String CARD_TYPE;
	private String CARD_NUM;
	private String CARD_COMPANY;
	private String USER_NUM;
	private String CERTIFICATE_TYPE;
	private String CERTIFICATE_CODE;
	private int SEX;
	private String WORK_COMPANY;
	private String COUNTRY;
	private long OPEN_CARD_TIME;
	private long VALIDATE_TIME;
	private String PHONE;

	public KaiKaData(String sERVICE_CODE, String uSER_NAME, String uSER_PASS, String cARD_TYPE, String cARD_NUM,
			String cARD_COMPANY, String uSER_NUM, String cERTIFICATE_TYPE, String cERTIFICATE_CODE, int sEX,
			String wORK_COMPANY, String cOUNTRY, long oPEN_CARD_TIME, long vALIDATE_TIME, String pHONE) {
		super();
		SERVICE_CODE = sERVICE_CODE;
		USER_NAME = uSER_NAME;
		USER_PASS = uSER_PASS;
		CARD_TYPE = cARD_TYPE;
		CARD_NUM = cARD_NUM;
		CARD_COMPANY = cARD_COMPANY;
		USER_NUM = uSER_NUM;
		CERTIFICATE_TYPE = cERTIFICATE_TYPE;
		CERTIFICATE_CODE = cERTIFICATE_CODE;
		SEX = sEX;
		WORK_COMPANY = wORK_COMPANY;
		COUNTRY = cOUNTRY;
		OPEN_CARD_TIME = oPEN_CARD_TIME;
		VALIDATE_TIME = vALIDATE_TIME;
		PHONE = pHONE;
	}

	public KaiKaData() {
		super();
	}

	public String getSERVICE_CODE() {
		return SERVICE_CODE;
	}

	public void setSERVICE_CODE(String sERVICE_CODE) {
		SERVICE_CODE = sERVICE_CODE;
	}

	public String getUSER_NAME() {
		return USER_NAME;
	}

	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}

	public String getUSER_PASS() {
		return USER_PASS;
	}

	public void setUSER_PASS(String uSER_PASS) {
		USER_PASS = uSER_PASS;
	}

	public String getCARD_TYPE() {
		return CARD_TYPE;
	}

	public void setCARD_TYPE(String cARD_TYPE) {
		CARD_TYPE = cARD_TYPE;
	}

	public String getCARD_NUM() {
		return CARD_NUM;
	}

	public void setCARD_NUM(String cARD_NUM) {
		CARD_NUM = cARD_NUM;
	}

	public String getCARD_COMPANY() {
		return CARD_COMPANY;
	}

	public void setCARD_COMPANY(String cARD_COMPANY) {
		CARD_COMPANY = cARD_COMPANY;
	}

	public String getUSER_NUM() {
		return USER_NUM;
	}

	public void setUSER_NUM(String uSER_NUM) {
		USER_NUM = uSER_NUM;
	}

	public String getCERTIFICATE_TYPE() {
		return CERTIFICATE_TYPE;
	}

	public void setCERTIFICATE_TYPE(String cERTIFICATE_TYPE) {
		CERTIFICATE_TYPE = cERTIFICATE_TYPE;
	}

	public String getCERTIFICATE_CODE() {
		return CERTIFICATE_CODE;
	}

	public void setCERTIFICATE_CODE(String cERTIFICATE_CODE) {
		CERTIFICATE_CODE = cERTIFICATE_CODE;
	}

	public int getSEX() {
		return SEX;
	}

	public void setSEX(int sEX) {
		SEX = sEX;
	}

	public String getWORK_COMPANY() {
		return WORK_COMPANY;
	}

	public void setWORK_COMPANY(String wORK_COMPANY) {
		WORK_COMPANY = wORK_COMPANY;
	}

	public String getCOUNTRY() {
		return COUNTRY;
	}

	public void setCOUNTRY(String cOUNTRY) {
		COUNTRY = cOUNTRY;
	}

	public long getOPEN_CARD_TIME() {
		return OPEN_CARD_TIME;
	}

	public void setOPEN_CARD_TIME(long oPEN_CARD_TIME) {
		OPEN_CARD_TIME = oPEN_CARD_TIME;
	}

	public long getVALIDATE_TIME() {
		return VALIDATE_TIME;
	}

	public void setVALIDATE_TIME(long vALIDATE_TIME) {
		VALIDATE_TIME = vALIDATE_TIME;
	}

	public String getPHONE() {
		return PHONE;
	}

	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}

}
