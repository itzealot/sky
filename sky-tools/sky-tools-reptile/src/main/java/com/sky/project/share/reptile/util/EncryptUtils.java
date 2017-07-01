package com.sky.project.share.reptile.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptUtils {

	private static Logger LOG = LoggerFactory.getLogger(EncryptUtils.class);

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
		} catch (NoSuchAlgorithmException e) {
			LOG.error("加密失败, 找不到相应的算法!", e);
		} catch (UnsupportedEncodingException e) {
			LOG.error("加密失败, 不支持字符编码!", e);
		}

		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	/**
	 * MD5加密
	 * 
	 * @param str
	 * @return
	 */
	public static String getMD5Str16(String inputStr) {
		if (StringUtils.isBlank(inputStr)) {
			return null;
		}

		return getMD5Str(inputStr).substring(8, 24);
	}

}
