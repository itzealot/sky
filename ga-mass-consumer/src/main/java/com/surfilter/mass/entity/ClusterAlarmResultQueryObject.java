package com.surfilter.mass.entity;

/**
 * ClusterAlarmResult Query Object
 * 
 * @author zealot
 *
 */
public class ClusterAlarmResultQueryObject {

	private long firstAlarmTime; // 初次报警时间(团伙第一个人出现在场所的时间)
	private long lastAlarmTime; // 最后报警时间(团伙成员最后一个人离开场所的时间)
	private String gangTime; // 团伙中成员出现的时间列表(startTime|endTime)，多个使用逗号(',')分隔

	public ClusterAlarmResultQueryObject(long firstAlarmTime, long lastAlarmTime, String gangTime) {
		super();
		this.firstAlarmTime = firstAlarmTime;
		this.lastAlarmTime = lastAlarmTime;
		this.gangTime = gangTime;
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

	public String getGangTime() {
		return gangTime;
	}

	public void setGangTime(String gangTime) {
		this.gangTime = gangTime;
	}

}
