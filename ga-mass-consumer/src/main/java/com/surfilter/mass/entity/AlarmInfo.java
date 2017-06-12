package com.surfilter.mass.entity;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.surfilter.mass.utils.ImcaptureUtil;

/**
 * 报警信息实体
 * 
 * @author zealot
 *
 */
public class AlarmInfo {

	private String matchValue;
	private String matchType;
	private String matchChildValue;

	private Long id;
	private Date createTime;

	private String serviceCode;
	private String xpoint;
	private String ypoint;
	private String apMac;
	private String apSsid;

	private Long storeId = 0L;
	private Long storePid = 0L;
	// 源数据类型 0:表示围栏，1：表示非经
	private int sourceType = 0;

	private Long startTime = 0L;
	private Long endTime = 0L;

	private String alarmType;
	private boolean isPhoneAlarm = false;
	private String phoneAccount;
	private boolean isEmailAlarm = false;
	private String mailAccount;

	private int dayAlarmCount;

	private int alarmInterval;
	private String certType;
	private String certCode;
	private Long zdPersonId;
	private String zdPersonMobile;
	private String userName; // 姓名
	private String zdType; // ZDR类型
	private String creatorArea; // ZDR下发者及所属的省市区

	public AlarmInfo() {
	}

	public AlarmInfo(String matchValue, String matchType, String matchChildValue, Long id, Long storeId, Long startTime,
			Long endTime, String phoneAccount, String mailAccount, String serviceCode) {
		super();

		this.matchChildValue = matchChildValue;
		this.matchType = matchType;
		this.matchValue = matchValue;

		this.id = id;
		this.storeId = storeId;

		this.startTime = startTime;
		this.endTime = endTime;

		this.phoneAccount = phoneAccount;
		this.mailAccount = mailAccount;
		this.serviceCode = serviceCode;
	}

	public AlarmInfo(String matchValue, String matchType, String matchChildValue, Long id, Long storeId, Long startTime,
			Long endTime, String phoneAccount, String mailAccount, String serviceCode, String alarmType,
			int dayAlarmCount, int alarmInterval, String certType, String certCode, Long zdPersonId,
			String zdPersonMobile, String userName) {
		this(matchValue, matchType, matchChildValue, id, storeId, startTime, endTime, phoneAccount, mailAccount,
				serviceCode);
		this.alarmType = alarmType;
		this.dayAlarmCount = dayAlarmCount;
		this.alarmInterval = alarmInterval;
		this.certType = certType;
		this.certCode = certCode;
		this.zdPersonId = zdPersonId;
		this.zdPersonMobile = zdPersonMobile;
		this.userName = userName;
	}

	public AlarmInfo(String matchValue, String matchType, String matchChildValue, Long id, Long storeId, Long startTime,
			Long endTime, String phoneAccount, String mailAccount, String serviceCode, String xpoint, String ypoint,
			String alarmType, int dayAlarmCount, int alarmInterval, String certType, String certCode, Long zdPersonId,
			String zdPersonMobile, String userName, String zdType, boolean isPhoneAlarm, boolean isEmailAlarm,
			String creatorArea) {
		this(matchValue, matchType, matchChildValue, id, storeId, startTime, endTime, phoneAccount, mailAccount,
				serviceCode, alarmType, dayAlarmCount, alarmInterval, certType, certCode, zdPersonId, zdPersonMobile,
				userName);
		this.xpoint = xpoint;
		this.ypoint = ypoint;
		this.zdType = zdType;
		this.isPhoneAlarm = isPhoneAlarm;
		this.isEmailAlarm = isEmailAlarm;
		this.creatorArea = creatorArea;
	}

	public String getMatchValue() {
		return matchValue;
	}

	public void setMatchValue(String matchValue) {
		this.matchValue = matchValue;
	}

	public String getMatchType() {
		return matchType;
	}

	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	public String getMatchChildValue() {
		return matchChildValue;
	}

	public void setMatchChildValue(String matchChildValue) {
		this.matchChildValue = matchChildValue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getApSsid() {
		return apSsid;
	}

	public void setApSsid(String apSsid) {
		this.apSsid = apSsid;
	}

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	public Long getStorePid() {
		return storePid;
	}

	public void setStorePid(Long storePid) {
		this.storePid = storePid;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getPhoneAccount() {
		return phoneAccount;
	}

	public void setPhoneAccount(String phoneAccount) {
		this.phoneAccount = phoneAccount;
	}

	public String getMailAccount() {
		return mailAccount;
	}

	public void setMailAccount(String mailAccount) {
		this.mailAccount = mailAccount;
	}

	public int getDayAlarmCount() {
		return dayAlarmCount;
	}

	public void setDayAlarmCount(int dayAlarmCount) {
		this.dayAlarmCount = dayAlarmCount;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		AlarmInfo o = null;
		if (obj instanceof AlarmInfo) {
			o = (AlarmInfo) obj;
		}

		// 还有身份证字段且身份证字段不为空且相等表明是同一个人
		if (!ImcaptureUtil.isEmpty(this.certCode) && !ImcaptureUtil.isEmpty(this.certType)
				&& this.certCode.equals(o.getCertCode()) && this.certType.equals(o.getCertType())) {
			return true;
		}

		// 若ZDRmobile不为空且相等则表明是同一个人
		if (!ImcaptureUtil.isEmpty(this.zdPersonMobile) && this.zdPersonMobile.equals(o.getZdPersonMobile())) {
			return true;
		}

		// 若ZDR id 不为空且相等，则是同一个人
		if (!this.zdPersonId.equals(0L) && !this.zdPersonId.equals(-1L) && this.zdPersonId.equals(o.getZdPersonId())) {
			return true;
		}

		return false;
	}

	public String getXpoint() {
		return xpoint;
	}

	public void setXpoint(String xpoint) {
		this.xpoint = xpoint;
	}

	public String getYpoint() {
		return ypoint;
	}

	public void setYpoint(String ypoint) {
		this.ypoint = ypoint;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
