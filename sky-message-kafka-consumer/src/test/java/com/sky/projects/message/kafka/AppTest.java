package com.sky.projects.message.kafka;

import java.lang.reflect.Method;

import com.sky.projects.message.function.Function;
import com.sky.projects.message.kafka.impl.KafkaMessageImpl;
import com.sky.projects.message.util.SkyReflectUtil;

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

	public void testReflect() throws ClassNotFoundException {
		Class<KafkaMessageImpl> clazz = SkyReflectUtil.reflect("com.sky.projects.message.kafka.impl.KafkaMessageImpl");
		System.out.println(clazz);

		Class<Function<byte[], String>> f = SkyReflectUtil
				.reflect("com.sky.projects.message.kafka.impl.KafkaMessageImpl");

		try {
			Method d = f.getDeclaredMethod("call", byte[].class);
			System.out.println(d);
			String val = (String) d.invoke(f.newInstance(), new String("hello").getBytes());
			System.out.println("result:" + val);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(f);
	}
}
