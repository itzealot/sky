package com.sky.project.share.api.kafka;

import com.sky.project.share.api.kafka.util.KafkaUtils;

import junit.framework.TestCase;

public class KafkaUtilsTest extends TestCase {

	private String brokers = "host1:9092,host2:9092,host3:9092";

	String topic = "topicName";
	String broker = "host";

	public void testOffsetEarly() {
		System.out.println(KafkaUtils.offset(broker, 9092, topic, false));
	}

	public void testOffsetLast() {
		System.out.println(KafkaUtils.offset(broker, 9092, topic, true));
	}

	public void testAllOffsetLast() {
		System.out.println(KafkaUtils.offset(topic, brokers, true));
	}

	public void testAllOffsetEarly() {
		System.out.println(KafkaUtils.offset(topic, brokers, false));
	}

}
