/**
 * 
 */
package com.surfilter.mass.entity;

/**
 * @author hapuer
 *
 */
public class BasicAlarmInf {

	private Long startTime;
	private Long endTime;
	private String serviceCode;
	private String xpoint;
	private String ypoint;
		
	public BasicAlarmInf(){}
	
	public BasicAlarmInf(Long startTime, Long endTime, String serviceCode, String xpoint, String ypoint) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.serviceCode = serviceCode;
		this.xpoint = xpoint;
		this.ypoint = ypoint;
	}

	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getXpoint() {
		return xpoint;
	}

	public void setXpoint(String xpoint) {
		this.xpoint = xpoint;
	}

	public String getYpoint() {
		return ypoint;
	}

	public void setYpoint(String ypoint) {
		this.ypoint = ypoint;
	}
}
