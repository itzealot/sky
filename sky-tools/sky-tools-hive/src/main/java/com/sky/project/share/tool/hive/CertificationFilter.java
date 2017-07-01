package com.sky.project.share.tool.hive;

import java.util.regex.Pattern;

/**
 * Certification Filter
 * 
 * @author zealot
 */
public final class CertificationFilter {

	public static final Pattern MAC_REGEX_048C = Pattern.compile("^[A-F0-9]{1}[048C]{1}(-[A-F0-9]{2}){5}$");
	public static final Pattern MAC_REGEX = Pattern.compile("([0-9A-F]{2}-){5}[0-9A-F]{2}");
	public static final Pattern MOBILE_REGEX = Pattern
			.compile("^(0|86|17951|086|0086|12593||\\+86)?(13[0-9]|15[0123456789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$");
	public static final String EMAIL_REG = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

	public static final Pattern IMSI_REGEXP = Pattern.compile("^460[0-2]([0-7]|9)\\d{10}$");
	public static final Pattern IMEI_REGEXP = Pattern.compile("^(\\d{15}|\\d{17})$");
	public static final Pattern EMAIL_REGEXP = Pattern.compile(EMAIL_REG);

	public static final String MOBILE_REGEXP = "^(0|86|17951|0086)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$";
	public static final String[] FILTERS = "\",\\,/,',>,<,|,?,=,+,[,],{,},%,;,&,^,!,(,)".split(",");
	public static final int FILTERS_LEN = FILTERS.length;

	public static final String CHINESE_REG = "([\u4e00-\u9fa5]+)";
	public static final Pattern CHINESE_PATTERN = Pattern.compile(CHINESE_REG);

	public static String evaluate(String id, String idType) {
		if (id == null) {
			return null;
		}
		switch (idType) {
		case "1020002": // MAC
			return isMac(id) ? id : null;
		case "1020004":// phone
			return isPhone(id) ? id : null;
		case "1021901": // IMEI
			return isImei(id) ? id : null;
		case "1020003": // IMSI
			return isImsi(id) ? id : null;
		case "1021111":// ID Card
			return Id15To18.id15Or18Filter(id);
		case "1030001":// QQ
			return isQQ(id) ? id : null;
		case "1021902":// Name
			return trimName(id);
		}

		return isVirtual(id, idType) ? id : null;
	}

	public static boolean isVirtual(String id, String idType) {
		if (id == null || "null".equalsIgnoreCase(id) || "MULL".equalsIgnoreCase(id) || "-1".equals(id)
				|| "0".equals(id) || id.contains(",") || id.startsWith(".")) {
			return false;
		}

		if (isEmail(id)) {
			return true;
		}

		if (id.length() < 4 || id.length() > 25) { // validate length
			return false;
		}

		for (int i = 0; i < FILTERS_LEN; i++) {
			if (id.contains(FILTERS[i])) {
				return false;
			}
		}

		return true;
	}

	public static boolean isMac(String id) {
		return MAC_REGEX.matcher(id).matches();
	}

	public static boolean isImeiOrImsi(String id) {
		if (id.length() == 15) {
			try {
				Long.parseLong(id);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static boolean isPhone(String id) {
		return MOBILE_REGEX.matcher(id).matches();
	}

	public static boolean isImei(String id) {
		return IMEI_REGEXP.matcher(id).matches();
	}

	public static boolean isImsi(String id) {
		return IMSI_REGEXP.matcher(id).matches();
	}

	public static boolean isQQ(String id) {
		if (id.length() >= 5 && id.length() <= 11) {
			try {
				Long.parseLong(id);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static String trimName(String src) {
		src = src.replaceAll(" ", "").trim();
		return matchChinese(src) ? src : null;
	}

	public static boolean isEmail(String str) {
		return EMAIL_REGEXP.matcher(str).matches();
	}

	public static boolean matchChinese(String src) {
		return src.length() >= 2 ? CHINESE_PATTERN.matcher(src).matches() : false;
	}

	private CertificationFilter() {
	}
}
