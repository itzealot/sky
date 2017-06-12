package com.surfilter.mass.tools.util;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Certification Filter
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class CertificationFilter implements Serializable {

	public static final Pattern MAC_REGEX_048C = Pattern.compile("^[A-F0-9]{1}[048C]{1}(-[A-F0-9]{2}){5}$");
	public static final Pattern MAC_REGEX = Pattern.compile("([0-9A-F]{2}-){5}[0-9A-F]{2}");
	public static final Pattern MOBILE_REGEX = Pattern
			.compile("^(0|86|17951|086|0086|12593||\\+86)?(13[0-9]|15[0123456789]|17[0-9]|18[0-9]|14[457])[0-9]{8}$");

	public static final Pattern IMSI_REGEXP = Pattern.compile("^460[0-2]([0-7]|9)\\d{10}$");
	public static final Pattern IMEI_REGEXP = Pattern.compile("^(\\d{15}|\\d{17})$");

	public static final String EMAIL_REG = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	public static final Pattern EMAIL_REGEXP = Pattern.compile(EMAIL_REG);

	public static final String MOBILE_REGEXP = "^(0|86|17951|0086)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$";

	public static final String CHINESE_REG = "([\u4e00-\u9fa5]+)";
	public static final Pattern CHINESE_PATTERN = Pattern.compile(CHINESE_REG);

	public static final String QQ_REG = "^[1-9][0-9]{4,10}$";
	public static final Pattern QQ_PATTERN = Pattern.compile(QQ_REG);

	// 特殊字符表达式
	public static final String SPECIAL_CHAR_REG = "[\"|\\|/|\\'|>|<|\\\\||?|=|+|[|]|{|}|%|;|&|^|!|(|)|,| ]";
	public static final Pattern SPECIAL_CHAR_REGEXP = Pattern.compile(SPECIAL_CHAR_REG);

	public static final String[] FILTERS = "\",\\,/,',>,<,|,?,=,+,[,],{,},%,;,&,^,!,(,)".split(",");
	public static final int FILTERS_LEN = FILTERS.length;

	// 场所编码正则表达式
	public static final String SERVICECODE_REGEXP = "^(\\d{6}[a-zA-Z0-9]{8})|(00000000000000)|(00000000000001)";
	public static final Pattern SERVICECODE_PATTERN = Pattern.compile(SERVICECODE_REGEXP);

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
		case "1030036": // Weixin
			return isWeiXin(id) ? id : null;
		case "1021353": // 车牌
			return isCarNumber(id) ? id : null;
		}

		return isVirtual(id, idType) ? id : null;
	}

	public static boolean isVirtual(String id, String idType) {
		if (id == null || "null".equalsIgnoreCase(id) || "MULL".equalsIgnoreCase(id) || "-1".equals(id)
				|| "0".equals(id) || id.startsWith(".")) {
			return false;
		}

		if (isEmail(id)) {
			return true;
		}

		if (id.length() < 4 || id.length() > 20) { // validate length
			return false;
		}

		return !containsSpecialChar(id);
	}

	public static boolean isMac(String id) {
		return MAC_REGEX.matcher(id).matches();
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
		return QQ_PATTERN.matcher(id).matches();
	}

	public static boolean isCarNumber(String id) {
		if (id == null) {
			return false;
		}
		return id.length() >= 6 && id.length() <= 9;
	}

	public static String trimName(String id) {
		id = id.replaceAll(" ", "").trim();
		return matchChinese(id) ? id : null;
	}

	public static boolean containsSpecialChar(String str) {
		return SPECIAL_CHAR_REGEXP.matcher(str).find();
	}

	public static boolean containsSpecialCharWithArray(String str) {
		for (int i = 0; i < FILTERS_LEN; i++) {
			if (str.contains(FILTERS[i])) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEmail(String id) {
		if (id == null) {
			return false;
		}
		return id.contains("@") && id.contains(".") ? EMAIL_REGEXP.matcher(id).matches() : false;
	}

	public static boolean isWeiXin(String id) {
		if (id == null || id.length() < 4 || id.length() > 20) {
			return false;
		}
		return !containsSpecialChar(id);
	}

	public static boolean matchChinese(String id) {
		return id.length() >= 2 ? CHINESE_PATTERN.matcher(id).matches() : false;
	}

	public static boolean isServiceCode(String str) {
		if (str == null) {
			return false;
		}
		return SERVICECODE_PATTERN.matcher(str).matches();
	}
}
