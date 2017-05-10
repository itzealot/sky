package com.sky.projects.message.kafka;

public class JvmParamsTest {

	public static void main(String[] args) {
		// -Djava.net.preferIPv4Stack=true
		System.out.println(System.getProperty("java.net.preferIPv4Stack"));
		// -Dsky.test.jvm=false
		System.out.println(System.getProperty("sky.test.jvm"));
	}
}
