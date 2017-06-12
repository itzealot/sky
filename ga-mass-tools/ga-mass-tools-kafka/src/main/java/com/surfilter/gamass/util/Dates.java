package com.surfilter.gamass.util;

import java.util.Date;

public final class Dates {

	public static Long nowUnixTime() {
		return new Date().getTime() / 1000;
	}

	public static String nowUnixtimeStr() {
		return "" + nowUnixTime();
	}

	private Dates() {
	}
}
