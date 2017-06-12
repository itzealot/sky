package com.surfilter.mass.tools.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Emoji {
	
	private static final Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
	        Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
	
	public static boolean valid(char ch) {
		return !((ch == 0x0) || (ch == 0x9) || (ch == 0xA) || (ch == 0xD)
				|| ((ch >= 0x20) && (ch <= 0xD7FF))
				|| ((ch >= 0xE000) && (ch <= 0xFFFD)) || ((ch >= 0x10000) && (ch <= 0x10FFFF)));
	}
	
	public static boolean valid(String ch) {
		Matcher matcher = pattern.matcher(ch);
		return matcher.matches();
	}
	
}
