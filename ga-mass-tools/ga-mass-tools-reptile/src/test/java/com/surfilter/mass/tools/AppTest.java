package com.surfilter.mass.tools;

import com.google.gson.Gson;
import com.surfilter.mass.tools.util.EncryptUtils;
import com.surfilter.mass.tools.util.FileUtil;

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

	public void testTest() {
		System.out.println("b\nb\nv".replaceAll("[\n]", ""));
	}

	public void testGetMD5Str16() {
		System.out.println(EncryptUtils.getMD5Str16("AAAAAAA"));
		System.out.println(EncryptUtils.getMD5Str("AAAAAAA"));
	}

	public void testJson() {
		System.out.println(new Gson().toJson(new Person("name", null)));
	}

	class Person {
		String name;
		String remark;

		public Person(String name, String remark) {
			super();
			this.name = name;
			this.remark = remark;
		}

	}

	public void test() {
		System.out.println(FileUtil.pathWithSuffix("/appslog/ztry/ztry"));
		System.out.println(FileUtil.pathWithSuffix("/appslog/ztry/ztry/"));
	}
}
