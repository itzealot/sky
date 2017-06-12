package com.surfilter.mass.tools.util;

/**
 * Number util
 * 
 * @author zealot
 *
 */
public final class NumberUtil {
	public static int parseValue(String item, int defaultValue) {
		try {
			return Integer.parseInt(item);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static int parseInt(String item) {
		return parseValue(item, 0);
	}

	public static long parseValue(String item, long defaultValue) {
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
		try {
			return Double.parseDouble(item);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static double parseDouble(String item) {
		return parseValue(item, 0.0D);
	}

	public static float parseValue(String item, float defaultValue) {
		try {
			return Float.parseFloat(item);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static float parseFloat(String item) {
		return parseValue(item, 0.0F);
	}

	public static String blank2MULL(String item) {
		return (item == null || item.isEmpty()) ? "MULL" : item;
	}

	public static String trimNull(String item) {
		return item == null ? "" : item;
	}

	/**
	 * 根据年与月份获取某一月的天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int fetchMonthLastDay(int year, int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;
		case 2:
			return isPriYear(year) ? 29 : 28;
		}
		return 30;
	}

	/**
	 * 是否为闰年
	 * 
	 * @param year
	 * @return
	 */
	public static boolean isPriYear(int year) {
		return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
	}

	public static String fillWith0(int j) {
		return j < 10 ? "0" + j : "" + j;
	}

	private NumberUtil() {
	}
}
