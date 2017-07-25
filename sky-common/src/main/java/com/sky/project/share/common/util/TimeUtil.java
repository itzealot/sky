package com.sky.project.share.common.util;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 * 基于 JDK8 time 包的时间工具类
 *
 * @author zealot
 */
public class TimeUtil {

	/**
	 * 获取默认时间格式: yyyy-MM-dd HH:mm:ss
	 */
	private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = TimeFormat.LONG_DATE_PATTERN_LINE.formatter;
	private static final DateTimeFormatter DEFAULT_SHORT_DATETIME_FORMATTER = TimeFormat.SHORT_DATE_PATTERN_NONE.formatter;

	/**
	 * String 转时间
	 *
	 * @param timeStr
	 * @return
	 */
	public static LocalDateTime parseTime(String timeStr) throws DateTimeParseException {
		return LocalDateTime.parse(timeStr, DEFAULT_DATETIME_FORMATTER);
	}

	/**
	 * String 转时间
	 *
	 * @param timeStr
	 * @param format
	 *            时间格式
	 * @return
	 */
	public static LocalDateTime parseTime(String timeStr, TimeFormat format) throws DateTimeParseException {
		return LocalDateTime.parse(timeStr, format.formatter);
	}

	/**
	 * 时间转 String yyyy-MM-dd HH:mm:ss
	 *
	 * @param time
	 * @return
	 */
	public static String parseTime(LocalDateTime time) throws DateTimeException {
		return DEFAULT_DATETIME_FORMATTER.format(time);
	}

	/**
	 * 时间转 String
	 *
	 * @param time
	 * @param format
	 *            时间格式
	 * @return
	 */
	public static String parseTime(LocalDateTime time, TimeFormat format) throws DateTimeException {
		return format.formatter.format(time);
	}

	/**
	 * String 转时间
	 *
	 * @param timeStr
	 * @return
	 */
	public static LocalDate parseShortTime(String timeStr) throws DateTimeParseException {
		return LocalDate.parse(timeStr, DEFAULT_SHORT_DATETIME_FORMATTER);
	}

	/**
	 * String 转时间
	 *
	 * @param timeStr
	 * @param format
	 *            时间格式
	 * @return
	 */
	public static LocalDate parseShortTime(String timeStr, TimeFormat format) throws DateTimeParseException {
		return LocalDate.parse(timeStr, format.formatter);
	}

	/**
	 * 时间转 String yyyyMMdd
	 * 
	 * @param time
	 * @return
	 */
	public static String parseShortTime(LocalDate time) throws DateTimeException {
		return DEFAULT_SHORT_DATETIME_FORMATTER.format(time);
	}

	/**
	 * 时间转 String
	 * 
	 * @param time
	 * @return
	 */
	public static String parseShortTime(LocalDateTime time) {
		return DEFAULT_SHORT_DATETIME_FORMATTER.format(time);
	}

	/**
	 * 时间转 String
	 *
	 * @param time
	 * @param format
	 *            时间格式
	 * @return
	 */
	public static String parseShortTime(LocalDate time, TimeFormat format) throws DateTimeException {
		return format.formatter.format(time);
	}

	/**
	 * 获取当前时间, yyyy-MM-dd HH:mm:ss
	 *
	 * @return
	 */
	public static String getCurrentDatetime() throws DateTimeException {
		return DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now());
	}

	/**
	 * 获取当前时间, yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static LocalDateTime getCurrentDatetimeWithoutMilliSec() throws DateTimeException {
		return LocalDateTime.now().withSecond(0).withNano(0);
	}

	/**
	 * 获取当前时间
	 *
	 * @param format
	 *            时间格式
	 * @return
	 */
	public static String getCurrentDatetime(TimeFormat format) throws DateTimeException {
		return format.formatter.format(LocalDateTime.now());
	}

	/**
	 * 获取当前时间, yyyyMMdd
	 *
	 * @return
	 */
	public static String getCurrentShortDatetime() throws DateTimeException {
		return DEFAULT_SHORT_DATETIME_FORMATTER.format(LocalDate.now());
	}

	public static Instant toInstant(LocalDate localDate) {
		return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
	}

	public static Instant toInstant(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
	}

	public static long toEpochMilli(LocalDate localDate) {
		return toInstant(localDate).toEpochMilli();
	}

	public static long toEpochMilli(LocalDateTime localDateTime) {
		return toInstant(localDateTime).toEpochMilli();
	}

	/**
	 * 获取当前时间
	 *
	 * @param format
	 *            时间格式
	 * @return
	 */
	public static String getCurrentShortDatetime(TimeFormat format) throws DateTimeException {
		return format.formatter.format(LocalDate.now());
	}

	public static Date asDate(LocalDate localDate) {
		return Date.from(toInstant(localDate));
	}

	public static Date asDate(LocalDateTime localDateTime) {
		return Date.from(toInstant(localDateTime));
	}

	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static int getWeekOfWeekyear(LocalDate localDate) {
		return localDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
	}

	public static int getWeekOfWeekyear(LocalDateTime localDate) {
		return localDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
	}

	/**
	 * 时间格式
	 */
	public enum TimeFormat {
		/**
		 * 短时间格式
		 */
		SHORT_DATE_PATTERN_LINE("yyyy-MM-dd"), SHORT_DATE_PATTERN_SLASH("yyyy/MM/dd"), SHORT_DATE_PATTERN_DOUBLE_SLASH("yyyy\\MM\\dd"), SHORT_DATE_PATTERN_NONE("yyyyMMdd"), SHORT_DATE_PATTERN_YM("yyyyMM"),

		/**
		 * 长时间格式
		 */
		LONG_DATE_PATTERN_LINE("yyyy-MM-dd HH:mm:ss"), LONG_DATE_PATTERN_SLASH("yyyy/MM/dd HH:mm:ss"), LONG_DATE_PATTERN_DOUBLE_SLASH("yyyy\\MM\\dd HH:mm:ss"), LONG_DATE_PATTERN_NONE("yyyyMMdd HH:mm:ss"), LONG_DATE_PATTERN_NONE_SEPARATOR("yyyyMMddHHmmss"),

		/**
		 * 长时间格式 带毫秒
		 */
		LONG_DATE_PATTERN_WITH_MILSEC_LINE("yyyy-MM-dd HH:mm:ss.SSS"), LONG_DATE_PATTERN_WITH_MILSEC_SLASH("yyyy/MM/dd HH:mm:ss.SSS"), LONG_DATE_PATTERN_WITH_MILSEC_DOUBLE_SLASH("yyyy\\MM\\dd HH:mm:ss.SSS"), LONG_DATE_PATTERN_WITH_MILSEC_NONE("yyyyMMdd HH:mm:ss.SSS");

		DateTimeFormatter formatter;

		private TimeFormat(String pattern) {
			formatter = DateTimeFormatter.ofPattern(pattern);
		}
	}

	private TimeUtil() {
	}

}
