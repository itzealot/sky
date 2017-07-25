package com.sky.project.share.streaming.analysis;

/**
 * CertificationTrack，身份的轨迹数据
 * 
 * @author zealot
 */
public class CertificationTrack {

	private final String idNo; // 身份
	private final String idType; // 身份对应的协议
	private final String serviceCode; // 场所编码
	private long startTime; // 最小区间，绝对秒数
	private long endTime; // 最大区间，绝对秒数

	public CertificationTrack(String idNo, String idType, String serviceCode, long startTime, long endTime) {
		super();
		this.idNo = idNo;
		this.idType = idType;
		this.serviceCode = serviceCode;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getIdNo() {
		return idNo;
	}

	public String getIdType() {
		return idType;
	}

	public String getServiceCode() {
		return serviceCode;
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

}
