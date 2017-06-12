package com.surfilter.gamass.util;

import junit.framework.TestCase;
import kafka.javaapi.consumer.SimpleConsumer;

public class KafkaUtilsTest extends TestCase {

	private String bootstrapServers = "192.168.1.110:9092,192.168.1.111:9092,192.168.1.112:9092";

	String topic = "wl_011";
	String kafkaHost = "192.168.1.110";
	private String clientName = "test";
	String host = "rzx110";

	public void testGetOffsetEarly() {
		try {
			KafkaUtils.offset(topic, kafkaHost, 9092, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetOffsetLast() {
		try {
			KafkaUtils.offset(topic, bootstrapServers, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	long lastest = kafka.api.OffsetRequest.LatestTime();
	long early = kafka.api.OffsetRequest.EarliestTime();

	String host110 = "192.168.1.110";
	String host111 = "192.168.1.111";
	String host112 = "192.168.1.112";

	public void testOffsetLast() {
		System.out.println(offset(new SimpleConsumer(host110, 9092, 100000, 64 * 1024, clientName), 0, lastest));
		System.out.println(offset(new SimpleConsumer(host110, 9092, 100000, 64 * 1024, clientName), 1, lastest));
		System.out.println(offset(new SimpleConsumer(host110, 9092, 100000, 64 * 1024, clientName), 2, lastest));

		System.out.println(offset(new SimpleConsumer(host111, 9092, 100000, 64 * 1024, clientName), 0, lastest));
		System.out.println(offset(new SimpleConsumer(host111, 9092, 100000, 64 * 1024, clientName), 1, lastest));
		System.out.println(offset(new SimpleConsumer(host111, 9092, 100000, 64 * 1024, clientName), 2, lastest));

		System.out.println(offset(new SimpleConsumer(host112, 9092, 100000, 64 * 1024, clientName), 0, lastest));
		System.out.println(offset(new SimpleConsumer(host112, 9092, 100000, 64 * 1024, clientName), 1, lastest));
		System.out.println(offset(new SimpleConsumer(host112, 9092, 100000, 64 * 1024, clientName), 2, lastest));
	}

	public void testOffsetEarly() {
		System.out.println(offset(new SimpleConsumer(host110, 9092, 100000, 64 * 1024, clientName), 0, early));
		System.out.println(offset(new SimpleConsumer(host110, 9092, 100000, 64 * 1024, clientName), 1, early));
		System.out.println(offset(new SimpleConsumer(host110, 9092, 100000, 64 * 1024, clientName), 2, early));

		System.out.println(offset(new SimpleConsumer(host111, 9092, 100000, 64 * 1024, clientName), 0, early));
		System.out.println(offset(new SimpleConsumer(host111, 9092, 100000, 64 * 1024, clientName), 1, early));
		System.out.println(offset(new SimpleConsumer(host111, 9092, 100000, 64 * 1024, clientName), 2, early));

		System.out.println(offset(new SimpleConsumer(host112, 9092, 100000, 64 * 1024, clientName), 0, early));
		System.out.println(offset(new SimpleConsumer(host112, 9092, 100000, 64 * 1024, clientName), 1, early));
		System.out.println(offset(new SimpleConsumer(host112, 9092, 100000, 64 * 1024, clientName), 2, early));
	}

	private long offset(SimpleConsumer consumer, int partition, long whichTime) {
		return KafkaUtils.getOffset(consumer, topic, partition, whichTime, clientName);
	}

	public void testfindLeader() throws Exception {
		System.out.println(KafkaUtils.findLeader(host110, 9092, "wl_011"));
		System.out.println(KafkaUtils.findLeader(host111, 9092, "wl_011"));
		System.out.println(KafkaUtils.findLeader(host112, 9092, "wl_011"));

		System.out.println(KafkaUtils.findLeader(host112, 9092, "wl_012"));
	}

	public void testfindLeaderSingle() throws Exception {
		System.out.println(KafkaUtils.findLeader(host110, 9092, "wl_011", 0));
		System.out.println(KafkaUtils.findLeader(host111, 9092, "wl_011", 1));
		System.out.println(KafkaUtils.findLeader(host112, 9092, "wl_011", 2));

		System.out.println(KafkaUtils.findLeader(host112, 9092, "wl_012", 0));
	}
}
