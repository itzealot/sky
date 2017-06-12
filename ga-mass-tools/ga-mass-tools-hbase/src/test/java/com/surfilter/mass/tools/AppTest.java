package com.surfilter.mass.tools;

import org.apache.hadoop.hbase.util.Bytes;

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
		sys(8);
		sys(18);
		sys(12);
	}

	public void sys(int sysSource) {
		for (int i = 1; i < 10; i++) {
			int pow = (int) (Math.pow(2, i));
			if ((sysSource & pow) != 0)
				System.out.println(i);
		}
		System.out.println("=============");
	}

	public void testT() {
		System.out.println("\t".replaceAll("\"|'", "").equals("\t") ? "\t" : "|");
	}

	public void testSys() {
		int a = 3;
		int b = 2;
		System.out.println((a & b) != 0);
		a = 4;
		System.out.println((a & b) != 0);

		a = 5;
		System.out.println((a & b) != 0);

		a = 6;
		System.out.println((a & b) != 0);
	}

	public void testLastTime() {
		byte[] bytes = Bytes.toBytes(1473209891);
		display(bytes);

		System.out.println(Bytes.toInt(bytes));
	}

	public void display(byte[] bytes) {
		for (byte b : bytes) {
			System.out.println(b);
		}
	}
}
