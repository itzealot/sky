package com.surfilter.mass.tools.entity;

import java.io.Serializable;

/**
 * 身份关系型数据实体，用于json序列化
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class SfData implements Serializable {
	private String MAC;
	private String PHONE;
	private String IMSI;
	private String IMEI;
	private String AUTH_TYPE;
	private String AUTH_CODE;
	private String CERTIFICATE_TYPE;
	private String CERTIFICATE_CODE;
	private String ID_TYPE;
	private String ACCOUNT;
	private int LAST_TIME = 0;
	private String LAST_PLACE;

	public SfData() {
		super();
	}

	public SfData(String mAC, String pHONE, String iMSI, String iMEI, String aUTH_TYPE, String aUTH_CODE,
			String cERTIFICATE_TYPE, String cERTIFICATE_CODE, String iD_TYPE, String aCCOUNT, String lAST_PLACE) {
		super();
		this.MAC = mAC;
		this.PHONE = pHONE;
		this.IMSI = iMSI;
		this.IMEI = iMEI;
		this.AUTH_TYPE = aUTH_TYPE;
		this.AUTH_CODE = aUTH_CODE;
		this.CERTIFICATE_TYPE = cERTIFICATE_TYPE;
		this.CERTIFICATE_CODE = cERTIFICATE_CODE;
		this.ID_TYPE = iD_TYPE;
		this.ACCOUNT = aCCOUNT;
		this.LAST_PLACE = lAST_PLACE;
	}

	public SfData(String mAC, String pHONE, String iMSI, String iMEI, String aUTH_TYPE, String aUTH_CODE,
			String cERTIFICATE_TYPE, String cERTIFICATE_CODE, String iD_TYPE, String aCCOUNT, int lAST_TIME,
			String lAST_PLACE) {
		this(mAC, pHONE, iMSI, iMEI, aUTH_TYPE, aUTH_CODE, cERTIFICATE_TYPE, cERTIFICATE_CODE, iD_TYPE, aCCOUNT,
				lAST_PLACE);
		LAST_TIME = lAST_TIME;
	}

	public String getMAC() {
		return MAC;
	}

	public void setMAC(String mAC) {
		MAC = mAC;
	}

	public String getPHONE() {
		return PHONE;
	}

	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}

	public String getIMSI() {
		return IMSI;
	}

	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public String getAUTH_TYPE() {
		return AUTH_TYPE;
	}

	public void setAUTH_TYPE(String aUTH_TYPE) {
		AUTH_TYPE = aUTH_TYPE;
	}

	public String getAUTH_CODE() {
		return AUTH_CODE;
	}

	public void setAUTH_CODE(String aUTH_CODE) {
		AUTH_CODE = aUTH_CODE;
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

	public String getID_TYPE() {
		return ID_TYPE;
	}

	public void setID_TYPE(String iD_TYPE) {
		ID_TYPE = iD_TYPE;
	}

	public String getACCOUNT() {
		return ACCOUNT;
	}

	public void setACCOUNT(String aCCOUNT) {
		ACCOUNT = aCCOUNT;
	}

	public int getLAST_TIME() {
		return LAST_TIME;
	}

	public void setLAST_TIME(int lAST_TIME) {
		LAST_TIME = lAST_TIME;
	}

	public String getLAST_PLACE() {
		return LAST_PLACE;
	}

	public void setLAST_PLACE(String lAST_PLACE) {
		LAST_PLACE = lAST_PLACE;
	}

	@Override
	public String toString() {
		return "SfData [MAC=" + MAC + ", PHONE=" + PHONE + ", IMSI=" + IMSI + ", IMEI=" + IMEI + ", AUTH_TYPE="
				+ AUTH_TYPE + ", AUTH_CODE=" + AUTH_CODE + ", CERTIFICATE_TYPE=" + CERTIFICATE_TYPE
				+ ", CERTIFICATE_CODE=" + CERTIFICATE_CODE + ", ID_TYPE=" + ID_TYPE + ", ACCOUNT=" + ACCOUNT
				+ ", LAST_TIME=" + LAST_TIME + ", LAST_PLACE=" + LAST_PLACE + "]";
	}

}
