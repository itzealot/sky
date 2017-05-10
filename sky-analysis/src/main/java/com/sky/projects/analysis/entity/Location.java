package com.sky.projects.analysis.entity;

import java.io.Serializable;

/**
 * 位置信息 Location(longitude, latitude)
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class Location implements Serializable {
	private double longitude; // 经度
	private double latitude; // 纬度

	public Location() {
	}

	public Location(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String toString() {
		return "Location{longitude=" + this.longitude + ", latitude=" + this.latitude + '}';
	}
}
