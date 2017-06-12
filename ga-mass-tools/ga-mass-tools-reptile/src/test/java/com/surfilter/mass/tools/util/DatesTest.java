package com.surfilter.mass.tools.util;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DatesTest extends TestCase {
	public DatesTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(DatesTest.class);
	}

	public void testApp() {
		assertTrue(true);
	}

	public void testTormorrow() {
		Date date = new Date();
		System.out.println(date);
		System.out.println(Dates.tormorrow(date));
	}

	public void testDate2Str() {
		Date date = new Date();
		System.out.println(Dates.date2Str(date, "yyyy-MM-dd"));
	}

	public void testCalendar() {
		Calendar instance = Calendar.getInstance();
		instance.setTime(new Date());
		int startYear = instance.get(Calendar.YEAR);
		int startMonth = instance.get(Calendar.MONTH) + 1;
		int startDay = instance.get(Calendar.DAY_OF_MONTH);

		System.out.println("startYear=" + startYear);
		System.out.println("startMonth=" + startMonth);
		System.out.println("startDay=" + startDay);

		instance.setTimeInMillis(new Date().getTime() - 86400000);
		int endYear = instance.get(Calendar.YEAR);
		int endMonth = instance.get(Calendar.MONTH) + 1;
		int endDay = instance.get(Calendar.DAY_OF_MONTH);

		System.out.println("endYear=" + endYear);
		System.out.println("endMonth=" + endMonth);
		System.out.println("endDay=" + endDay);
	}

	public void testStr2Date() {
		System.out.println(Dates.str2Year("2015"));
		System.out.println(Dates.str2Year("2016"));
	}
}
