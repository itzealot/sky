package com.surfilter.mass.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * 时间工具类
 * 
 * @author zealot
 *
 */
public final class Dates {

	public static long getUnixTime(String dateStr, String format, long defaultVal) {
		try {
			return DateTime.parse(dateStr, DateTimeFormat.forPattern(format)).getMillis() / 1000;
		} catch (Exception e) {
			return defaultVal;
		}
	}

	/***
	 * parse mysql date str like yyyy-MM-dd HH:mm:ss.0 has error
	 * 
	 * @param dateStr
	 * @return
	 */
	public static long getUnixTime(String dateStr) {
		return getUnixTime(dateStr, "yyyy-MM-dd HH:mm:ss", -1L);
	}

	public static long unixTime(String dateStr) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr).getTime() / 1000;
		} catch (ParseException e) {
			return -1L;
		}
	}

	private Dates() {
	}
}
