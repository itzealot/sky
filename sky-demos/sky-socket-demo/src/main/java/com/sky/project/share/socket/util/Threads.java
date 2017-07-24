package com.sky.project.share.socket.util;

/**
 * Threads
 * 
 * @author zealot
 */
public final class Threads {

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	private Threads() {
	}
}