package com.sky.project.share.reptile.util;

/**
 * Validate util
 * 
 * @author zealot
 *
 */
public final class ValidateUtil {

	public static boolean isBlank(String val) {
		return val == null || val.isEmpty();
	}

	public static String[] validate(String val, String property) {
		if (isBlank(val) || val.split(":").length != 2) {
			throw new RuntimeException("error setting for " + property + " config");
		}
		return val.split(":");
	}

	public static String validateProperty(String val, String property) {
		if (isBlank(val)) {
			throw new IllegalArgumentException("error setting for " + property + " config");
		}
		return val;
	}

	public static String[] validateOthers(String val, String msg) {
		if (isBlank(val)) {
			return null;
		}

		String[] arrays = val.split(";");
		int len = arrays.length;
		String[] results = new String[len * 2];

		for (int i = 0; i < len; i++) {
			String[] keyWithValue = validate(arrays[i], msg);
			results[2 * i] = keyWithValue[0];
			results[2 * i + 1] = keyWithValue[1];
		}

		return results;
	}

	public static <T> T validate(T obj, String property) {
		if (obj == null) {
			throw new IllegalArgumentException("error setting for " + property + " config");
		}
		return obj;
	}

	private ValidateUtil() {
	}
}
