package com.surfilter.mass.entity;

public class FilterInfo {
	private String alarmMac;
	private String power;
	private String macType;
	private String endTime;

	public FilterInfo(String[] original) {
		this.alarmMac = original[0];
		this.power = original[4];
		this.macType = original[1];
		this.endTime = original[3];
	}

	public void setAlarmMac(String alarmMac) {
		this.alarmMac = alarmMac;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public void setMacType(String macType) {
		this.macType = macType;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAlarmMac() {
		return this.alarmMac;
	}

	public String getPower() {
		return this.power;
	}

	public String getMacType() {
		return this.macType;
	}

	public String getEndTime() {
		return this.endTime;
	}

	@Override
	public String toString() {
		return "FilterInfo [alarmMac=" + alarmMac + ", power=" + power + ", macType=" + macType + ", endTime=" + endTime
				+ "]";
	}

}
