package com.sky.project.share.api.kafka;

import org.I0Itec.zkclient.ZkClient;

import com.sky.project.share.api.kafka.util.ZkUtils;

import junit.framework.TestCase;

public class ZkUtilsTest extends TestCase {

	private static final int CONNECTION_TIMEOUT = 20 * 1000;
	private static final int SESSION_TIMEOUT = 10 * 1000;

	String connString = "192.168.0.168:2181,rzx169:2181,rzx177:2181/kafka";

	String topic = "wl_006";
	String group = "xxxGroupIDTest";
	String brokerId = "107";
	ZkClient zkClient = new ZkClient(connString, SESSION_TIMEOUT, CONNECTION_TIMEOUT, new ZkUtils.StringSerializer());

	public void testBrokers() {
		System.out.println(ZkUtils.brokers(zkClient));
	}

	public void testBrokerInfo() {
		System.out.println(ZkUtils.brokerInfo(zkClient, brokerId));
	}

	public void testBrokerPartitions() {
		System.out.println(ZkUtils.brokerPartitions(zkClient, topic));
	}

	public void testGetOffset() {
		for (int i = 0; i < 3; i++) {
			System.out.println("partition=" + i + ",offset=" + ZkUtils.getOffset(zkClient, group, topic, i));
		}
	}
}
