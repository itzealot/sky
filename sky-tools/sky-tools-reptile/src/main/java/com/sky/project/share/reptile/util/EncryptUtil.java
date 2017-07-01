package com.sky.project.share.reptile.util;

import java.security.MessageDigest;

import org.apache.commons.lang.StringUtils;

public final class EncryptUtil {

	/**
	 * MD5加密
	 * 
	 * @param str
	 * @return
	 */
	public static String getMD5Str(String inputStr) {
		if (StringUtils.isBlank(inputStr)) {
			return null;
		}

		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(inputStr.getBytes("UTF-8"));
		} catch (Exception e) {
			return null;
		}

		byte[] byteArray = messageDigest.digest();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				builder.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				builder.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return builder.toString();
	}

}
