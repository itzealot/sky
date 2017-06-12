package com.surfilter.gamass;

import com.surfilter.gamass.util.RedisUtil;

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
}
