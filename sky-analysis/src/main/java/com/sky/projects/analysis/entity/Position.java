package com.sky.projects.analysis.entity;

import java.io.Serializable;

/**
 * MAC 对应的经纬度信息
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class Position implements Serializable {
	private String mac;
	private double latitude;
	private double longitude;

	public Position() {
		super();
	}

	public Position(String mac, double latitude, double longitude) {
		super();
		this.mac = mac;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
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
		return "Position [mac=" + mac + ",latitude=" + latitude + ",longitude=" + longitude + "]";
	}

}
