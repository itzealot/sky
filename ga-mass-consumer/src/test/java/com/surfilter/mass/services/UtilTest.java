package com.surfilter.mass.services;

import java.util.Arrays;

import com.surfilter.mass.utils.ImcaptureUtil;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

	public void testIsEmpty() {
		System.out.println(ImcaptureUtil.isEmpty("0"));
		System.out.println(ImcaptureUtil.isEmpty(""));
		System.out.println(ImcaptureUtil.isEmpty("-1"));
		System.out.println(ImcaptureUtil.isEmpty("-11"));
	}

	public void testStringBuffer() {
		StringBuffer buffer = new StringBuffer();
		String sp = "|";
		buffer.append("a").append(sp).append("b").append(sp);
		System.out.println(buffer.deleteCharAt(buffer.length() - 1).toString());
	}

	public void testSort() {
		String[] gangLists = "1,4,3,7,5".split(",");
		Arrays.sort(gangLists);
		System.out.println(Arrays.asList(gangLists));
	}
}
