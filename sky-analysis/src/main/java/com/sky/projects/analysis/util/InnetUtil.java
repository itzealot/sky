package com.sky.projects.analysis.util;

import java.net.InetAddress;

public final class InnetUtil {

	public static String hostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			return "unknown";
		}
	}

	public static String ip() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return "0.0.0.0";
		}
	}

	private InnetUtil() {
	}
}