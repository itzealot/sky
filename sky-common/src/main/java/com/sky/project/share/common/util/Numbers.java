package com.sky.project.share.common.util;

/**
 * NumberUtil
 * 
 * @author zealot
 *
 */
public final class Numbers {

	public static int parseValue(String item, int defaultValue) {
		if (isBlank(item)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(item);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static boolean isBlank(String item) {
		return item == null || item.isEmpty();
	}

	public static int parseInt(String item) {
		return parseValue(item, 0);
	}

	public static long parseValue(String item, long defaultValue) {
		if (isBlank(item)) {
			return defaultValue;
		}
		try {
			return Long.parseLong(item);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static long parseLong(String item) {
		return parseValue(item, 0L);
	}

	public static short parseValue(String item, short defaultValue) {
		if (isBlank(item)) {
			return defaultValue;
		}
		try {
			return Short.parseShort(item);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static short parseShort(String item) {
		return parseValue(item, (short) 0);
	}

	public static double parseValue(String item, double defaultValue) {
		if (isBlank(item)) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(item);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static double parseDouble(String item) {
		return parseValue(item, 0.0D);
	}

	public static float parseValue(String item, float defaultValue) {
		if (isBlank(item)) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(item);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float parseFloat(String item) {
		return parseValue(item, 0.0F);
	}

	private Numbers() {
	}
}
