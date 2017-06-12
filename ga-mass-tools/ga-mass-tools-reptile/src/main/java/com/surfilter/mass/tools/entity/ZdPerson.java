package com.surfilter.mass.tools.entity;

import static com.surfilter.mass.tools.util.FileUtil.blank2NULL;
import static com.surfilter.mass.tools.util.FileUtil.isBlank;

import java.sql.Date;

import com.surfilter.mass.tools.conf.BaseInfoConfig;
import com.surfilter.mass.tools.conf.ZDType;

/**
 * mysql table(zd_person)
 * 
 * @author zealot
 *
 */
public class ZdPerson {

	private String certificateCode; // 身份证
	private String name; // 姓名
	private String mobile; // 手机号
	private String qq; // QQ号
	private String wx; // WX号
	private String mac; // MAC地址
	private String imei; // IMEI
	private String imsi; // IMSI
	private String carId; // 车牌
	private String address; // 居住地址
	private String type; // 类型：1:SD,2:SK,3:SF,4:SW
	private Date createTime; // 1970-01-01 00:00:00' COMMENT '入库日期
	private String creater; // 创建人
	private Date updateTime; // 更新时间
	private String updateUser; // 最后更新人
	private String provinceCode; // 所属省份编码
	private String cityCode; // 所属城市编码
	private String areaCode; // 所属分区
	private int matchType; // 类型：0总数 1命中 2活跃
	private String lastServiceCode; // 最后出现场所
	private Date lastCapTime; // 最后出现时间
	private String sysSource; // 来源大类
	private String source; // 来源小类
	private String policeCode; // 派出所来源
	private String floorAddr; // 楼栋地址
	private String department; // 提供部门, 多个以","分隔
	private String email; // 邮箱
	private String sex; // 性别
	private String nation; // 民族
	private String height; // 身高
	private String certArea; // 户籍地
	private String both; // 出生日期
	private int drupCount; // 涉毒次数
	private String remark; // 备注
	private String personPhoto; // 重点人照片

	public ZdPerson() {
		super();
	}

	public ZdPerson(ZtryData data, BaseInfoConfig info) {
		this.certificateCode = data.getCertificate();
		this.name = blank2NULL(data.getName());
		this.address = blank2NULL(data.getAddress());
		if (this.address == null || this.address.isEmpty()) {
			this.address = blank2NULL(data.getCensusAddr());
		}
		this.type = ZDType.ZT.getCode();

		this.creater = info.getCreater();
		this.provinceCode = info.getProvinceCode();
		this.cityCode = info.getCityCode();
		this.areaCode = info.getAreaCode();
		this.policeCode = blank2NULL(info.getPoliceCode());

		this.department = "1004";
		this.sex = fetchSex(data.getSex());
		this.nation = blank2NULL(data.getNation());
		this.height = blank2NULL(data.getHeight());
		this.certArea = blank2NULL(data.getCensusAddr());
		this.both = blank2NULL(data.getBirth());

		StringBuffer buffer = new StringBuffer(256);
		buffer.append("别名绰号:");
		if (!isBlank(data.getNickname())) {
			buffer.append(data.getNickname());
		}

		buffer.append(";口音:");
		if (!isBlank(data.getKouyin())) {
			buffer.append(data.getKouyin());
		}

		buffer.append(";体貌特征:");
		if (!isBlank(data.getFeatures())) {
			buffer.append(data.getFeatures());
		}

		buffer.append(";案件类别:");
		if (!isBlank(data.getCaseType())) {
			buffer.append(data.getCaseType());
		}

		buffer.append(";在逃类型:");
		if (!isBlank(data.getAwayType())) {
			buffer.append(data.getAwayType());
		}

		buffer.append(";逃跑日期:");
		if (!isBlank(data.getAwayDate())) {
			buffer.append(data.getAwayDate());
		}

		this.remark = buffer.toString();
	}

	private String fetchSex(String sex) {
		if (sex == null || sex.isEmpty()) {
			return "1";
		}
		return sex.contains("女") || sex.contains("girl") ? "2" : "1";
	}

	public String getCertificateCode() {
		return certificateCode;
	}

	public void setCertificateCode(String certificateCode) {
		this.certificateCode = certificateCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public int getMatchType() {
		return matchType;
	}

	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}

	public String getLastServiceCode() {
		return lastServiceCode;
	}

	public void setLastServiceCode(String lastServiceCode) {
		this.lastServiceCode = lastServiceCode;
	}

	public Date getLastCapTime() {
		return lastCapTime;
	}

	public void setLastCapTime(Date lastCapTime) {
		this.lastCapTime = lastCapTime;
	}

	public String getSysSource() {
		return sysSource;
	}

	public void setSysSource(String sysSource) {
		this.sysSource = sysSource;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getCertArea() {
		return certArea;
	}

	public void setCertArea(String certArea) {
		this.certArea = certArea;
	}

	public String getBoth() {
		return both;
	}

	public void setBoth(String both) {
		this.both = both;
	}

	public String getPoliceCode() {
		return policeCode;
	}

	public void setPoliceCode(String policeCode) {
		this.policeCode = policeCode;
	}

	public String getFloorAddr() {
		return floorAddr;
	}

	public void setFloorAddr(String floorAddr) {
		this.floorAddr = floorAddr;
	}

	public int getDrupCount() {
		return drupCount;
	}

	public void setDrupCount(int drupCount) {
		this.drupCount = drupCount;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPersonPhoto() {
		return personPhoto;
	}

	public void setPersonPhoto(String personPhoto) {
		this.personPhoto = personPhoto;
	}

	@Override
	public String toString() {
		return "ZdPerson [certificateCode=" + certificateCode + ", name=" + name + ", mobile=" + mobile + ", qq=" + qq
				+ ", wx=" + wx + ", mac=" + mac + ", imei=" + imei + ", imsi=" + imsi + ", carId=" + carId
				+ ", address=" + address + ", type=" + type + ", createTime=" + createTime + ", creater=" + creater
				+ ", updateTime=" + updateTime + ", updateUser=" + updateUser + ", provinceCode=" + provinceCode
				+ ", cityCode=" + cityCode + ", areaCode=" + areaCode + ", matchType=" + matchType
				+ ", lastServiceCode=" + lastServiceCode + ", lastCapTime=" + lastCapTime + ", sysSource=" + sysSource
				+ ", source=" + source + ", policeCode=" + policeCode + ", floorAddr=" + floorAddr + ", department="
				+ department + ", email=" + email + ", sex=" + sex + ", nation=" + nation + ", height=" + height
				+ ", certArea=" + certArea + ", both=" + both + ", drupCount=" + drupCount + ", remark=" + remark
				+ ", personPhoto=" + personPhoto + "]";
	}

}
