package com.surfilter.mass.tools.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtil {

	public static String date2Str(String str) throws ParseException {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str));
	}

	public static String dateToStr(Date date, String fmt) {
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

	private DateUtil() {
	}
}
