package com.sky.projects.analysis.entity;

import java.io.Serializable;

/**
 * 设备信息
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class EquipLocation implements Serializable {

	private String equipNum; // 设备编码
	private String serviceCode; // 场所编码
	private double latitude; // 纬度
	private double longitude; // 经度

	public EquipLocation() {
		super();
	}

	public EquipLocation(String equipNum, String serviceCode, double latitude, double longitude) {
		super();
		this.equipNum = equipNum;
		this.serviceCode = serviceCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getEquipNum() {
		return equipNum;
	}

	public void setEquipNum(String equipNum) {
		this.equipNum = equipNum;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "EquipLocation [equipNum=" + this.equipNum + ", serviceCode=" + this.serviceCode + ", latitude="
				+ this.latitude + ", longitude=" + this.longitude + "]";
	}
}
