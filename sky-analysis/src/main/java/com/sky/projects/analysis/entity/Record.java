package com.sky.projects.analysis.entity;

import java.io.Serializable;

/**
 * Record
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class Record implements Serializable, Comparable<Record> {

	private String deviceNum;
	private String serviceCode;
	private int type;
	private String mac;
	private String phone;
	private int power;
	private long startTime;
	private long endTime;
	private double latitude;
	private double longitude;

	public Record() {
		super();
	}

	public Record(String deviceNum, String serviceCode, int type, String mac, String phone, int power,
			long startTime, long endTime) {
		super();
		this.deviceNum = deviceNum;
		this.serviceCode = serviceCode;
		this.type = type;
		this.mac = mac;
		this.phone = phone;
		this.power = power;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(String deviceNum) {
		this.deviceNum = deviceNum;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
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

	public int compareTo(Record other) {
		return this.power - other.power;
	}

	public String toString() {
		return "KafkaRecord{deviceNum='" + this.deviceNum + '\'' + ", serviceCode='" + this.serviceCode + '\''
				+ ", type=" + this.type + ", mac='" + this.mac + '\'' + ", phone='" + this.phone + '\'' + ", power="
				+ this.power + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", latitude="
				+ this.latitude + ", longitude=" + this.longitude + '}';
	}
}
