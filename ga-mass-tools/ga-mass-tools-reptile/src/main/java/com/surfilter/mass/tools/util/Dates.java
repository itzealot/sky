package com.surfilter.mass.tools.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 * 
 * @author zealot
 *
 */
public final class Dates {

	public static String date2Str(String str) {
		try {
			return new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str));
		} catch (ParseException e) {
			return null;
		}
	}

	public static String date2Str(Date date, String fmt) {
		return new SimpleDateFormat(fmt).format(date);
	}

	public static int unixTime(String str, String fmt, Locale l, int defaultValue) {
		try {
			Date date = new SimpleDateFormat(fmt, l).parse(str);
			return Integer.valueOf(date.getTime() / 1000 + "");
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int unixTime(String value, int defaultValue) {
		try {
			return Integer.parseInt(value.trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static long unixTime(String dateStr) {
		return unixTime(dateStr, "yyyy-MM-dd HH:mm:ss", 0L);
	}

	public static long unixTime(String dateStr, String format) {
		return unixTime(dateStr, format, 0L);
	}

	public static long unixTime(String dateStr, String format, long defaultValue) {
		try {
			return new SimpleDateFormat(format).parse(dateStr).getTime() / 1000;
		} catch (ParseException e) {
			return defaultValue;
		}
	}

	public static long mills(String dateStr, String format, long defaultValue) {
		try {
			return new SimpleDateFormat(format).parse(dateStr).getTime() / 1000;
		} catch (ParseException e) {
			return defaultValue;
		}
	}

	public static long mills(String dateStr, String format) {
		return mills(dateStr, format, 0L);
	}

	public static Time sqlTime(String dateStr, String format) {
		return new Time(mills(dateStr, format));
	}

	public static Time sqlTime(String dateStr) {
		return sqlTime(dateStr, "yyyy-MM-dd HH:mm:ss");
	}

	public static Timestamp timestamp(String dateStr, String format) {
		return new Timestamp(mills(dateStr, format));
	}

	public static Timestamp timestamp(String dateStr) {
		return timestamp(dateStr, "yyyy-MM-dd HH:mm:ss");
	}

	public static Date str2Date(String dateStr) {
		return str2Date(dateStr, "yyyy-MM-dd");
	}

	public static Date str2DateWithPlusHour(String dateStr) {
		return str2Date(dateStr, "yyyy-MM-dd+HH");
	}

	public static Date str2Year(String dateStr) {
		return str2Date(dateStr, "yyyy");
	}

	public static Date str2Date(String dateStr, String format) {
		try {
			return new SimpleDateFormat(format).parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

	public static boolean between(Date start, Date end) {
		return end == null ? true : start.getTime() <= end.getTime();
	}

	public static Date tormorrow(Date date) {
		return new Date(date.getTime() + 86400000);
	}

	public static Date yesterday(Date date) {
		return new Date(date.getTime() - 86400000);
	}

	private Dates() {
	}

}
