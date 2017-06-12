package com.surfilter.mass.tools.util;

import java.util.Arrays;
import java.util.List;

import com.surfilter.mass.tools.dao.RedisDaoAlias;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RedisUtilTest extends TestCase {

	public RedisUtilTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(RedisUtilTest.class);
	}

	public void testApp() {
		assertTrue(true);
	}

	public void testJoin() {
		System.out.println(RedisUtil.join("|", "A", "B", "C"));
		System.out.println(RedisUtil.join("||", "A", "B", "C"));
	}

	public void testCertificationHashValue() {
		System.out.println(RedisUtil.certificationHashValue("1", "001", 122l, 5));
	}

	public void testModifyCertificationSaveModel() {
		RedisUtil.modifyCertificationSaveModel(Arrays.asList("4E-EA-DD-A8-E9-09|1020002"), "192.168.1.109|6379", 6);
	}

	public void testDisplay() {
		byte[][] macKeyBytes = MacTransferUtil.macKeyBytes("4E-EA-DD-A8-E9-09", 6);
		byte[] redisKey = macKeyBytes[0];
		byte[] hashKey = macKeyBytes[1];
		display(redisKey);
		display(hashKey);
	}

	String serversInfo = "192.168.0.166|6379";
	RedisDaoAlias dao = RedisDaoAlias.getInstance(serversInfo);

	public void testHgetBytes() {
		byte[][] macKeyBytes = MacTransferUtil.macKeyBytes("4E-EA-DD-A8-E9-09", 6);
		byte[] redisKey = macKeyBytes[0];
		byte[] hashKey = macKeyBytes[1];

		display(redisKey);
		display(hashKey);

//		dao.hincByBytes(Arrays.asList(redisKey), Arrays.asList(hashKey));

		List<byte[]> bytes = dao.hgetByBytes(Arrays.asList(redisKey), Arrays.asList(hashKey));

		for (byte[] bs : bytes) {
			System.out.println(new String(bs));
			System.out.println(bs.length);
		}

	}

	public void testMacKeyBytes() {
		byte[][] macKeyBytes = MacTransferUtil.macKeyBytes("FC-3F-7C-50-CD-EC", 5);
		byte[] redisKey = macKeyBytes[0];
		byte[] hashKey = macKeyBytes[1];

		display(redisKey);
		display(hashKey);
	}

	public static void display(byte[] bytes) {
		for (byte b : bytes) {
			System.out.print(b);
		}
		System.out.println();
	}
}
