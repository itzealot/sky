package com.surfilter.mass.tools.entity;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * 入住信息，对应 mysql 的 InnList 表
 * 
 * @author zealot
 *
 */
public class InnList {

	private String serviceCode; // 场所编码
	private String username; // 用户名
	private String certificateType; // 证件类型
	private String certificateCode; // 证件号码
	private Date InnTime; // 入住时间(datetime)
	private Date offTime; // 离开时间(datetime)
	private String roomNo; // 房间号
	private String floor; // 楼层
	private String memo; // 备注
	private String orgName; // 机构代码
	private String country; // 国籍
	private String mobile; // 手机号
	private String cityCode; // 城市
	private Timestamp innTimeServer; // 服务器录入时间(timestamp)
	private Timestamp offTimeServer; // 服务器离开时间(timestamp)

	public InnList() {
		super();
	}

	public InnList(String serviceCode, String username, String certificateType, String certificateCode, Date innTime,
			Date offTime, String roomNo, String floor, String memo, String orgName, String country, String mobile,
			String cityCode, Timestamp innTimeServer, Timestamp offTimeServer) {
		super();
		this.serviceCode = serviceCode;
		this.username = username;
		this.certificateType = certificateType;
		this.certificateCode = certificateCode;
		InnTime = innTime;
		this.offTime = offTime;
		this.roomNo = roomNo;
		this.floor = floor;
		this.memo = memo;
		this.orgName = orgName;
		this.country = country;
		this.mobile = mobile;
		this.cityCode = cityCode;
		this.innTimeServer = innTimeServer;
		this.offTimeServer = offTimeServer;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCertificateType() {
		return certificateType;
	}

	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}

	public String getCertificateCode() {
		return certificateCode;
	}

	public void setCertificateCode(String certificateCode) {
		this.certificateCode = certificateCode;
	}

	public Date getInnTime() {
		return InnTime;
	}

	public void setInnTime(Date innTime) {
		InnTime = innTime;
	}

	public Date getOffTime() {
		return offTime;
	}

	public void setOffTime(Date offTime) {
		this.offTime = offTime;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public Timestamp getInnTimeServer() {
		return innTimeServer;
	}

	public void setInnTimeServer(Timestamp innTimeServer) {
		this.innTimeServer = innTimeServer;
	}

	public Timestamp getOffTimeServer() {
		return offTimeServer;
	}

	public void setOffTimeServer(Timestamp offTimeServer) {
		this.offTimeServer = offTimeServer;
	}

}
