package com.surfilter.mass.tools.util;

import org.apache.hadoop.hbase.util.Bytes;

public final class MacTransferUtil {

	// 进制编码字符串
	public static final char[] CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
			'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', '-', '=' };

	public static final int RADIX_16 = 16;
	public static final int RADIX_64 = 64;

	public static String encodeMac(String mac) {
		return encodeMac(mac, RADIX_64, 8);
	}

	public static String encodeMac(String str, int radix) {
		return convert(Long.parseLong(reverse(str.replace("-", "")), RADIX_16), radix);
	}

	public static String encodeMac(String mac, int radix, int len) {
		return convert(Long.parseLong(reverse(mac.replace("-", "")), RADIX_16), radix, len);
	}

	/**
	 * 字符串反转
	 * 
	 * @param str
	 * @return
	 */
	public static String reverse(String str) {
		int length = str.length() - 1;
		StringBuffer buffer = new StringBuffer(length);
		for (int i = length; i >= 0; i--) {
			buffer.append(str.charAt(i));
		}
		return buffer.toString();
	}

	public static byte[] macRedisKeyBytes(String mac) {
		return Bytes.toBytes(Integer.parseInt(mac.replace("-", "").substring(0, 6), 16));
	}

	public static byte[] macHashKeyBytes(String mac) {
		return Bytes.toBytes(Integer.parseInt(mac.replace("-", "").substring(6), 16));
	}

	/**
	 * 
	 * @param mac
	 * @return [0]:macRedisKeyBytes, [1]:macHashKeyBytes
	 */
	public static byte[][] macKeyBytes(String mac) {
		return macKeyBytes(mac, 6);
	}

	public static byte[][] macKeyBytes(String mac, int prefix) {
		String replace = mac.replace("-", "");
		byte[][] results = new byte[2][];
		results[0] = Bytes.toBytes(Integer.parseInt(replace.substring(0, prefix), 16));
		results[1] = Bytes.toBytes(Integer.parseInt(replace.substring(prefix), 16));
		return results;
	}

	public static byte[] parse2IntBytes(String val, int radix) {
		return Bytes.toBytes(Integer.parseInt(val, radix));
	}

	/**
	 * 进制转换
	 * 
	 * @param val
	 *            值
	 * @param radix
	 *            进制
	 * @return
	 */
	public static String convert(long val, int radix) {
		StringBuffer buffer = new StringBuffer();
		long newVal = val;
		while (newVal != 0) {
			int remainder = (int) (newVal % radix);
			buffer.append(CHARS[remainder]);
			newVal = newVal / radix;
		}
		return reverse(buffer.toString());
	}

	/**
	 * 进制转换，不够补0
	 * 
	 * @param val
	 *            值
	 * @param radix
	 *            进制
	 * @param len
	 *            补齐后的长度
	 * @return
	 */
	public static String convert(long val, int radix, int len) {
		StringBuffer buffer = new StringBuffer();
		long newVal = val;
		while (newVal != 0) {
			int remainder = (int) (newVal % radix);
			buffer.append(CHARS[remainder]);
			newVal = newVal / radix;
		}

		return reverse(fillWith0(buffer, len));
	}

	/**
	 * 填充 0
	 * 
	 * @param buffer
	 * @param len
	 * @return
	 */
	public static String fillWith0(StringBuffer buffer, int len) {
		int size = buffer.length();
		if (size >= len) {
			return buffer.toString();
		}

		for (int i = 0; i < len - size; i++) {
			buffer.append("0");
		}
		return buffer.toString();
	}

}
