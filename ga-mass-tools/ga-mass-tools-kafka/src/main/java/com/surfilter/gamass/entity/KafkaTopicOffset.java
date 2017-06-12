package com.surfilter.gamass.entity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @function:kafka记录类
 */
@SuppressWarnings("serial")
public class KafkaTopicOffset implements Serializable {
	private String topicName;
	private HashMap<Integer, Long> offsetList = new HashMap<Integer, Long>();
	private HashMap<Integer, String> leaderList = new HashMap<Integer, String>();

	public KafkaTopicOffset(String topicName) {
		this.topicName = topicName;
	}

	public String getTopicName() {
		return topicName;
	}

	public HashMap<Integer, Long> getOffsetList() {
		return offsetList;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public void setOffsetList(HashMap<Integer, Long> offsetList) {
		this.offsetList = offsetList;
	}

	public HashMap<Integer, String> getLeaderList() {
		return leaderList;
	}

	public void setLeaderList(HashMap<Integer, String> leaderList) {
		this.leaderList = leaderList;
	}

	public String toString() {
		return "topic:" + topicName + ",offsetList:" + this.offsetList + ",leaderList:" + this.leaderList;
	}
}