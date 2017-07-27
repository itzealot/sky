package com.sky.project.share.algorithm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {

	public AppTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testApp() {
		assertTrue(true);
	}

	public void testIntegerMaxAndMin() {
		assertEquals(Integer.MIN_VALUE, 1 << 31); // 左移31位
		assertEquals(Integer.MIN_VALUE, 1 << -1); // 左移-1位

		assertEquals(Integer.MAX_VALUE, ~(-1 << 31)); // 左移31位
		assertEquals(Integer.MAX_VALUE, ~(1 << 31)); // 左移31位
	}

	public void testMove() {
		int m = 2;

		m <<= 3; // 左移3位，等价于 m = m << 3;
		m >>= 2; // 右移2位，等价于 m = m >> 2;

		assertEquals(4, m);

		// 有符号右移运算符
		assertEquals(-1, -1 >> 2);

		// 无符号右移运算符
		assertEquals(1073741823, -1 >>> 2);
		assertEquals(4194303, -1 >>> 10);
	}
}
