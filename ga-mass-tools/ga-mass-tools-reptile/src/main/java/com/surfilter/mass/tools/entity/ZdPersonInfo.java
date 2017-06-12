package com.surfilter.mass.tools.entity;

/**
 * mysql table(zd_person_info)
 * 
 * @author zealot
 *
 */
public class ZdPersonInfo {

	private long zdPersonId; // 对应zdPerson表的id
	private String types; // zd types
	private String departments; // 提供部门、多个以,分隔
	private String provinceCode; // 所属省份编码
	private String cityCode; // 所属城市编码
	private String areaCode; // 所属地区
	private String policeCode; // 派出所来源
	private String creater; // 创建人,多个以","分隔(所属区域相同时，会有多个)

	public ZdPersonInfo(ZdPerson p, long id) {
		this.zdPersonId = id;
		this.types = p.getType();
		this.departments = p.getDepartment();
		this.provinceCode = p.getProvinceCode();
		this.cityCode = p.getCityCode();
		this.areaCode = p.getAreaCode();
		this.policeCode = p.getPoliceCode();
		this.creater = p.getCreater();
	}

	public long getZdPersonId() {
		return zdPersonId;
	}

	public void setZdPersonId(long zdPersonId) {
		this.zdPersonId = zdPersonId;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
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

	public String getPoliceCode() {
		return policeCode;
	}

	public void setPoliceCode(String policeCode) {
		this.policeCode = policeCode;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

}
