/**
 * 
 */
package com.surfilter.mass.services;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.junit.Assert;

import com.surfilter.mass.utils.Dates;

import junit.framework.TestCase;

/**
 * @author hapuer
 *
 */
public class TestStrings extends TestCase {

	public void testStrsCombine() {
		String[] aaa = { "aaa", "bbb", "ccc" };
		System.out.println(ArrayUtils.toString(aaa, ","));
		Assert.assertEquals("aaa,bbb,ccc", String.valueOf(aaa));
	}

	public void test() {
		int offset = 5;
		long value = 1464224786L * 1000;
		System.out.println(new Date(value));
		System.out.println(DateTime.now());
		System.out.println(DateTime.now().minusDays(offset));
		System.out.println(DateTime.now().minusDays(offset).isAfter(value));
		System.out.println(!DateTime.now().minusDays(offset).isAfter(value));
	}

	public void testDateTimeHours() {
		// 2015-6-28 08:08:08
		long value = 1467072488L * 1000;
		System.out.println(!DateTime.now().minusHours(5).isAfter(value));
		System.out.println(!DateTime.now().minusHours(6).isAfter(value));
		System.out.println(!DateTime.now().minusHours(7).isAfter(value));
		System.out.println(!DateTime.now().minusHours(9).isAfter(value));
		System.out.println(!DateTime.now().minusHours(12).isAfter(value));
	}

	public void testDateTimeMinutes() {
		// 2015-6-28 08:08:08
		long value = 1467072488L * 1000;
		System.out.println(!DateTime.now().minusMinutes(150).isAfter(value));
		System.out.println(!DateTime.now().minusMinutes(180).isAfter(value));
		System.out.println(!DateTime.now().minusMinutes(420).isAfter(value));
		System.out.println(!DateTime.now().minusMinutes(900).isAfter(value));
	}

	public void testParse() {
		System.out.println(Dates.getUnixTime("2012-12-21 23:22:45"));
		System.out.println(Dates.getUnixTime("2012-1221 23:22:45"));
	}
}
