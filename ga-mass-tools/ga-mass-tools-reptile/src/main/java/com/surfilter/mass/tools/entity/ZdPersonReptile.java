package com.surfilter.mass.tools.entity;

/**
 * zd person by Reptile
 * 
 * @author zealot
 *
 */
public class ZdPersonReptile {

	private String personType; // 人员类型
	private String provideDept; // 提供部门
	private String name; // 姓名
	private String sex; // 性别
	private String nation; // 民族
	private String height; // 身高
	private String birthDate; // 出生日期
	private int times; // 次数
	private String certificate; // 身份证
	private String mobile; // 手机号
	private String qq; // QQ
	private String wx; // 微信
	private String email; // 邮箱
	private String censusAddr; // 户籍地址
	private String ownArea; // 所属区域
	private String addr; // 现居地
	private String floorAddr; // 楼栋地址
	private String creator; // 创建人
	private String createTime; // 创建时间
	private String census; // 籍贯
	private String caseType; // 涉案类别
	private String dataFrom; // 数据来源
	private String picuture; // zd person照片
	private String carNumber; // 车牌号
	private String mac; // MAC
	private String imsi; // IMSI
	private String imei; // IMEI

	public ZdPersonReptile(String personType, String provideDept, String name, String sex, String nation, String height,
			String birthDate, int times, String certificate, String mobile, String qq, String wx, String email,
			String censusAddr, String ownArea, String addr, String floorAddr, String creator, String createTime,
			String census, String caseType, String dataFrom, String picuture, String carNumber, String mac, String imsi,
			String imei) {
		super();
		this.personType = personType;
		this.provideDept = provideDept;
		this.name = name;
		this.sex = sex;
		this.nation = nation;
		this.height = height;
		this.birthDate = birthDate;
		this.times = times;
		this.certificate = certificate;
		this.mobile = mobile;
		this.qq = qq;
		this.wx = wx;
		this.email = email;
		this.censusAddr = censusAddr;
		this.ownArea = ownArea;
		this.addr = addr;
		this.floorAddr = floorAddr;
		this.creator = creator;
		this.createTime = createTime;
		this.census = census;
		this.caseType = caseType;
		this.dataFrom = dataFrom;
		this.picuture = picuture;
		this.carNumber = carNumber;
		this.mac = mac;
		this.imsi = imsi;
		this.imei = imei;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public String getProvideDept() {
		return provideDept;
	}

	public void setProvideDept(String provideDept) {
		this.provideDept = provideDept;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWx() {
		return wx;
	}

	public void setWx(String wx) {
		this.wx = wx;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCensusAddr() {
		return censusAddr;
	}

	public void setCensusAddr(String censusAddr) {
		this.censusAddr = censusAddr;
	}

	public String getOwnArea() {
		return ownArea;
	}

	public void setOwnArea(String ownArea) {
		this.ownArea = ownArea;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getFloorAddr() {
		return floorAddr;
	}

	public void setFloorAddr(String floorAddr) {
		this.floorAddr = floorAddr;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCensus() {
		return census;
	}

	public void setCensus(String census) {
		this.census = census;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public String getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}

	public String getPicuture() {
		return picuture;
	}

	public void setPicuture(String picuture) {
		this.picuture = picuture;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	@Override
	public String toString() {
		return "ZdPersonReptile [personType=" + personType + ", provideDept=" + provideDept + ", name=" + name
				+ ", sex=" + sex + ", nation=" + nation + ", height=" + height + ", birthDate=" + birthDate + ", times="
				+ times + ", certificate=" + certificate + ", mobile=" + mobile + ", qq=" + qq + ", wx=" + wx
				+ ", email=" + email + ", censusAddr=" + censusAddr + ", ownArea=" + ownArea + ", addr=" + addr
				+ ", floorAddr=" + floorAddr + ", creator=" + creator + ", createTime=" + createTime + ", census="
				+ census + ", caseType=" + caseType + ", dataFrom=" + dataFrom + ", picuture=" + picuture
				+ ", carNumber=" + carNumber + ", mac=" + mac + ", imsi=" + imsi + ", imei=" + imei + "]";
	}

}
