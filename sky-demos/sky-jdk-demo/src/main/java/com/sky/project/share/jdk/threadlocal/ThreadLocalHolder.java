package com.sky.project.share.jdk.threadlocal;

import java.util.Random;

public class ThreadLocalHolder {

	private int value;

	// 初始化 ThreadLocal 实例，覆盖初始化方法 initialValue
	private static final ThreadLocal<ThreadLocalHolder> LOCAL = new ThreadLocal<ThreadLocalHolder>() {
		@Override
		protected ThreadLocalHolder initialValue() {
			return new ThreadLocalHolder(new Random().nextInt());
		}
	};

	public ThreadLocalHolder(int value) {
		this.value = value;
	}

	public static ThreadLocalHolder get() {
		return LOCAL.get();
	}

	public static void remove() {
		LOCAL.remove();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
