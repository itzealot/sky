package com.surfilter.mass.tools.util;

public final class Closeables {

	public static void close(AutoCloseable... closeables) {
		if (closeables != null) {
			for (AutoCloseable auto : closeables) {
				if (auto != null) {
					try {
						auto.close();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	private Closeables() {
	}
}
