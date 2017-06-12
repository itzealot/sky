package com.surfilter.gamass.util;

import junit.framework.TestCase;

public class KafkaUtilTest extends TestCase {
	
	private  String brokerlist = "192.168.1.110:9092,192.168.1.111:9092,192.168.1.112:9092";
	private String topic = "wl_011";
	private String group = "yhyGroupID";
	
	public void testTopicMetadataRequest() {
		System.out.println(KafkaUtil.topicMetadataRequest(brokerlist, topic, group));
	}
	
	public void testGetLastOffsetByTopic() {
		System.out.println(KafkaUtil.getLastOffsetByTopic(brokerlist, topic, group));
	}
	
	public void testGetEarlyOffsetByTopic() {
		System.out.println(KafkaUtil.getEarlyOffsetByTopic(brokerlist, topic, group));
	}
}
