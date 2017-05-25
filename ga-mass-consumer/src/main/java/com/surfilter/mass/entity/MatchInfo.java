package com.surfilter.mass.entity;

import com.surfilter.mass.utils.ImcaptureUtil;

/**
 * 匹配信息构建实体
 * 
 * @author hapuer
 *
 */
public class MatchInfo {

	private Long storeId;
	private Long macId;

	private String matchType;
	private String matchValue;
	private String matchChildValue;

	private boolean isPhoneAlarm = false;
	private String alarmPhones;
	private boolean isEmailAlarm = false;
	private String alarmEmails;

	private String serviceRange;
	private String areaRange;
	private int dayAlarmCount;

	private String serviceType;
	private int alarmInterval;
	private String certType;
	private String certCode;
	private Long zdPersonId;
	private String zdPersonMobile;
	private String userName; // 姓名
	private String zdType; // ZDR类型,如果有多种类型，用","拼接
	private String creatorArea; // ZDR下发者及所属的省市区

	public MatchInfo(Long storeId, Long macId, String matchType, String matchValue, String matchChildValue,
			String alarmPhones, String alarmEmails, String serviceRange, String areaRange, Integer dayAlarmCount,
			String serviceType, String alarmInterval, String certType, String certCode, String zdPersonId,
			String zdPersonMobile, String userName, String zdType, boolean isPhoneAlarm, boolean isEmailAlarm,
			String creatorArea) {
		super();
		this.storeId = storeId;
		this.macId = macId;
		this.matchType = matchType;
		this.matchValue = matchValue;
		this.matchChildValue = matchChildValue;
		this.alarmPhones = alarmPhones;
		this.alarmEmails = alarmEmails;
		this.serviceRange = serviceRange;
		this.areaRange = areaRange;
		this.dayAlarmCount = dayAlarmCount;
		this.serviceType = serviceType;
		this.alarmInterval = ImcaptureUtil.getValue(alarmInterval, 30);
		this.certType = certType;
		this.certCode = certCode;
		this.zdPersonId = ImcaptureUtil.getValue(zdPersonId, -1l);
		this.zdPersonMobile = "0".equals(zdPersonMobile) ? null : zdPersonMobile;
		this.userName = "0".equals(userName) ? null : userName;
		this.zdType = zdType;
		this.isPhoneAlarm = isPhoneAlarm;
		this.isEmailAlarm = isEmailAlarm;
		this.setCreatorArea(creatorArea);
	}

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	public Long getMacId() {
		return macId;
	}

	public void setMacId(Long macId) {
		this.macId = macId;
	}

	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	public String getMatchValue() {
		return matchValue;
	}

	public void setMatchValue(String matchValue) {
		this.matchValue = matchValue;
	}

	public String getMatchChildValue() {
		return matchChildValue;
	}

	public void setMatchChildValue(String matchChildValue) {
		this.matchChildValue = matchChildValue;
	}

	public String getAlarmPhones() {
		return alarmPhones;
	}

	public void setAlarmPhones(String alarmPhones) {
		this.alarmPhones = alarmPhones;
	}

	public String getAlarmEmails() {
		return alarmEmails;
	}

	public void setAlarmEmails(String alarmEmails) {
		this.alarmEmails = alarmEmails;
	}

	public String getServiceRange() {
		return serviceRange;
	}

	public void setServiceRange(String serviceRange) {
		this.serviceRange = serviceRange;
	}

	public String getAreaRange() {
		return areaRange;
	}

	public void setAreaRange(String areaRange) {
		this.areaRange = areaRange;
	}

	public int getDayAlarmCount() {
		return dayAlarmCount;
	}

	public void setDayAlarmCount(int dayAlarmCount) {
		this.dayAlarmCount = dayAlarmCount;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public int getAlarmInterval() {
		return alarmInterval;
	}

	public void setAlarmInterval(int alarmInterval) {
		this.alarmInterval = alarmInterval;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertCode() {
		return certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	public Long getZdPersonId() {
		return zdPersonId;
	}

	public void setZdPersonId(Long zdPersonId) {
		this.zdPersonId = zdPersonId;
	}

	public String getZdPersonMobile() {
		return zdPersonMobile;
	}

	public void setZdPersonMobile(String zdPersonMobile) {
		this.zdPersonMobile = zdPersonMobile;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getZdType() {
		return zdType;
	}

	public void setZdType(String zdType) {
		this.zdType = zdType;
	}

	public boolean isPhoneAlarm() {
		return isPhoneAlarm;
	}

	public void setPhoneAlarm(boolean phoneAlarm) {
		isPhoneAlarm = phoneAlarm;
	}

	public boolean isEmailAlarm() {
		return isEmailAlarm;
	}

	public void setEmailAlarm(boolean emailAlarm) {
		isEmailAlarm = emailAlarm;
	}

	public String getCreatorArea() {
		return creatorArea;
	}

	public void setCreatorArea(String creatorArea) {
		this.creatorArea = creatorArea;
	}
}
