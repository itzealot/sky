package com.surfilter.mass.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 场所信息缓存实体
 * 
 * @author zealot
 *
 */
public class ServiceInfo {

	private String serviceType; // 场所类型
	private String policeCode; // 归属派出所编码
	private String provinceCode; // 省编码
	private String cityCode; // 市编码
	private String areaCode; // 区域编码

	public ServiceInfo(String serviceType, String policeCode, String provinceCode, String cityCode, String areaCode) {
		super();
		this.serviceType = serviceType;
		this.policeCode = policeCode;
		this.provinceCode = provinceCode;
		this.cityCode = cityCode;
		this.areaCode = areaCode;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getPoliceCode() {
		return policeCode;
	}

	public void setPoliceCode(String policeCode) {
		this.policeCode = policeCode;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
