package com.surfilter.mass.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * zd person cluster alarm
 * 
 * @table cluster_alarm_result
 * 
 * @author zealot
 *
 */
public class ClusterAlarmResult {

	private String serviceCode;
	private String provinceCode;
	private String cityCode;
	private String areaCode;
	private String policeCode; // 派出所编码
	private String gangList; // 团伙列表
	private long firstAlarmTime; // 初次报警时间(团伙第一个人出现在场所的时间)
	private long lastAlarmTime; // 最后报警时间(团伙成员最后一个人离开场所的时间)
	private int zdType; // ZDR类型
	private String gangTime; // 团伙中成员出现的时间列表(startTime|endTime)，多个使用逗号(',')分隔
	private int clusterTime; // 聚集时长

	public ClusterAlarmResult(String serviceCode, String provinceCode, String cityCode, String areaCode,
			String policeCode, String gangList, long firstAlarmTime, long lastAlarmTime, int zdType, String gangTime) {
		this(serviceCode, provinceCode, cityCode, areaCode, policeCode, gangList, firstAlarmTime, lastAlarmTime,
				zdType);
		this.gangTime = gangTime;
	}

	private ClusterAlarmResult(String serviceCode, String provinceCode, String cityCode, String areaCode,
			String policeCode, String gangList, long firstAlarmTime, long lastAlarmTime, int zdType) {
		super();
		this.serviceCode = serviceCode;
		this.provinceCode = provinceCode;
		this.cityCode = cityCode;
		this.areaCode = areaCode;
		this.policeCode = policeCode;
		this.gangList = gangList;
		this.firstAlarmTime = firstAlarmTime;
		this.lastAlarmTime = lastAlarmTime;
		this.zdType = zdType;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getPoliceCode() {
		return policeCode;
	}

	public void setPoliceCode(String policeCode) {
		this.policeCode = policeCode;
	}

	public String getGangList() {
		return gangList;
	}

	public void setGangList(String gangList) {
		this.gangList = gangList;
	}

	public long getFirstAlarmTime() {
		return firstAlarmTime;
	}

	public void setFirstAlarmTime(long firstAlarmTime) {
		this.firstAlarmTime = firstAlarmTime;
	}

	public long getLastAlarmTime() {
		return lastAlarmTime;
	}

	public void setLastAlarmTime(long lastAlarmTime) {
		this.lastAlarmTime = lastAlarmTime;
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

	public int getZdType() {
		return zdType;
	}

	public void setZdType(int zdType) {
		this.zdType = zdType;
	}

	public String getGangTime() {
		return gangTime;
	}

	public void setGangTime(String gangTime) {
		this.gangTime = gangTime;
	}

	public int getClusterTime() {
		return clusterTime;
	}

	public void setClusterTime(int clusterTime) {
		this.clusterTime = clusterTime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
