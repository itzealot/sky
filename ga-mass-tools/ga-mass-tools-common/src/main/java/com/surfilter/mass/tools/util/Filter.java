package com.surfilter.mass.tools.util;

import java.util.regex.Pattern;

public final class Filter {
	static final char spliter = '-';
	public static final String EMAIL_REG = "/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$/";
	public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REG);

	public static final String CHINESE_REG = "([\u4e00-\u9fa5]+)";
	public static final Pattern CHINESE_PATTERN = Pattern.compile(CHINESE_REG);

	public static final String PHONE_REG = "^((\\+?86)|(\\(\\+86\\))|852)?(13[0-9][0-9]{8}|15[0-9][0-9]{8}|18[0-9][0-9]{8}|14[0-9][0-9]{8}|17[0-9][0-9]{8}|[0-9]{8})$";
	public static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REG);

	public static boolean matchChinese(String src) {
		return src.length() >= 2 ? CHINESE_PATTERN.matcher(src).matches() : false;
	}

	public static String imeiOrImsiFilter(String src) {
		if (isBlank(src) || src.length() != 15) {
			return "";
		}

		try {
			Long.parseLong(src);
			return src;
		} catch (NumberFormatException e) {
			return "";
		}
	}

	public static String mobileFilter(String src) {
		if (isBlank(src)) {
			return "";
		}
		return isMobile(src) ? src : extractMobile0(src);
	}

	public static String extractMobile(String src) {
		if (isBlank(src)) {
			return "";
		}
		return isMobile(src) ? src : extractMobile0(src);
	}

	private static String extractMobile0(String src) {
		char[] chs = { '|', '/', ';', ',' };

		for (int i = 0, len = chs.length; i < len; i++) {
			int index = -1;
			String str = trimNULL(src);
			String last = str;
			while ((index = str.indexOf(chs[i])) != -1) {
				String subStr = str.substring(0, index);
				str = str.substring(index + 1);
				last = str;

				if (isMobile(subStr)) {
					return subStr;
				}
			}

			if (isMobile(last)) {
				return last;
			}
		}

		return "";
	}

	public static String trimNULL(String src) {
		return src == null ? "" : src.replace("\\N", "").replace("NULL", "").replace("MULL", "").trim();
	}

	public static boolean isMobile(String src) {
		return isBlank(src) ? false : isMobile0(src);
	}

	private static boolean isMobile0(String src) {
		return src.length() == 11 ? isPhone(src) : false;
	}

	public static boolean isPhone(String src) {
		return PHONE_PATTERN.matcher(src).matches();
	}

	public static String trimEmail(String str) {
		if (isBlank(str)) {
			return "";
		}
		str = str.trim().toLowerCase();
		return EMAIL_PATTERN.matcher(str).matches() ? "" : str;
	}

	public static String trim(String src) {
		return src == null ? ""
				: src.replace("\\N", "").replace("NULL", "")
						.replaceAll("[,|;|\\-|\\=|\\?|\\*|\\+|\\(|\\)|（|）|\\'|\\t]*", "").trim();
	}

	public static String trimName(String src) {
		return matchChinese(src) ? src : "";
	}

	public static String emailProtocol(String src) {
		String protocol = "";
		if (src.endsWith("@qq.com") || src.endsWith("@qq.cn") || src.endsWith("@qq.com.cn")) {// qq
			protocol = "1019020";
		} else if (src.endsWith("@gmail.com") || src.endsWith("@gmail.cn") || src.endsWith("@gmail.com.cn")) {// gmail
			protocol = "1019036";
		} else if (src.endsWith("@163.com") || src.endsWith("@163.cn") || src.endsWith("@163.com.cn")) {// 163
			protocol = "1019004";
		} else if (src.endsWith("@126.com") || src.endsWith("@126.cn") || src.endsWith("@126.com.cn")) {// 126
			protocol = "1019023";
		} else if (src.endsWith("@sina.com.cn") || src.endsWith("@sina.com") || src.endsWith("@sina.cn")) {// sina
			protocol = "1019001";
		} else if (src.endsWith("@tom.com") || src.endsWith("@tom.cn") || src.endsWith("@tom.com.cn")) {// TOM邮箱
			protocol = "1019003";
		} else if (src.endsWith("@263.net") || src.endsWith("@263.com") || src.endsWith("@263.net.cn")) {// 263邮箱
			protocol = "1019005";
		} else if (src.endsWith("@139.cn") || src.endsWith("@139.com") || src.endsWith("@139.com.cn")) {// 139邮箱
			protocol = "1019044";
		} else if (src.endsWith("@189.com") || src.endsWith("@189.com.cn") || src.endsWith("@189.com.cn")) {// 189邮箱
			protocol = "1019045";
		} else if (src.endsWith("@aliyun.com") || src.endsWith("@aliyun.cn") || src.endsWith("@aliyun.com.cn")) {// aliyun邮箱
			protocol = "1019040";
		} else if (src.endsWith("@yeah.net") || src.endsWith("@yeah.com")) {// aliyun邮箱
			protocol = "1019042";
		} else if (src.endsWith("@sohu.com") || src.endsWith("@sohu.cn") || src.endsWith("@sohu.com.cn")) {// aliyun邮箱
			protocol = "1019002";
		} else if (src.endsWith("@yahoo.com") || src.endsWith("@yahoo.cn") || src.endsWith("@yahoo.com.cn")
				|| src.endsWith("@yahoo.com.hk")) {// yahoo 邮箱
			protocol = "1019019";
		} else if (src.endsWith("@careland.com") || src.endsWith("@careland.cn") || src.endsWith("@careland.com.cn")) {// kailide
			protocol = "careland";
		}

		return protocol;
	}

	public static String macFilter(String str) {
		if (str == null) {
			return "";
		}

		String s = str.replace("\t", "").trim();
		if (s.length() == 12) {
			return append(str);
		}

		return s.length() == 17 ? sub(str) : "";
	}

	private static String append(final String mac) {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < 10; i += 2) {
			buffer.append(mac.charAt(i)).append(mac.charAt(i + 1)).append(spliter);
		}

		return buffer.append(mac.charAt(10)).append(mac.charAt(11)).toString().toUpperCase();
	}

	private static String sub(final String mac) {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < 15; i += 3) {
			buffer.append(mac.charAt(i)).append(mac.charAt(i + 1)).append(spliter);
		}

		return buffer.append(mac.charAt(15)).append(mac.charAt(16)).toString().toUpperCase();
	}

	public static boolean isBlank(String str) {
		return str == null ? true : "".equals(str.replace("\\N", "").replace("NULL", "").replace("MULL", "").trim());
	}

	private Filter() {
	}
}
