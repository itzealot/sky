package com.sky.project.share.jdk.syn;

/**
 * Task
 * 
 * @author zealot
 */
public final class Task {

	/**
	 * print message
	 * 
	 * @param msg
	 * @param printCounts
	 */
	public static void print(String msg, int printCounts) {
		for (int i = 1; i <= printCounts; i++) {
			// TODO
		}
		System.out.println("Thread " + Thread.currentThread().getName() + " finish print, counts:" + printCounts);
	}

	private Task() {
	}
}