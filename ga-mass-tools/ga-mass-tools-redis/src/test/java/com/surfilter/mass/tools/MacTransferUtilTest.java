package com.surfilter.mass.tools;

import org.apache.hadoop.hbase.util.Bytes;

import com.surfilter.mass.tools.util.MacTransferUtil;

import junit.framework.TestCase;

public class MacTransferUtilTest extends TestCase {

	public void testEncodeMac() {
		System.out.println(MacTransferUtil.encodeMac("9E-78-79-CA-FC-83"));
		System.out.println(MacTransferUtil.encodeMac("9E-78-79-CA-FC-83", 62));
		System.out.println(MacTransferUtil.encodeMac("2A-43-1D-A1-BC-FA"));
		System.out.println(MacTransferUtil.encodeMac("2A-43-1D-A1-BC-FA", 62));
		System.out.println(MacTransferUtil.encodeMac("EA-7B-13-99-35-39"));
		System.out.println(MacTransferUtil.encodeMac("EA-7B-13-99-35-39", 62));
		System.out.println(MacTransferUtil.encodeMac("00-00-00-00-35-39"));
		System.out.println(MacTransferUtil.encodeMac("35-39-00-00-00-00"));
		System.out.println(MacTransferUtil.encodeMac("35-39-00-00-00-00", 62));
		System.out.println(MacTransferUtil.convert(10, 8));
		System.out.println(MacTransferUtil.convert(100, 8));
	}

	public void testGenerateChars() {
		for (int i = 0; i < 9; i++) {
			System.out.print("'" + i + "'" + ",");
		}
		System.out.print("'" + 9 + "'" + ",");
		for (char c = 'A'; c < 'Z'; c++) {
			System.out.print("'" + c + "'" + ",");
		}

		System.out.print("'" + 'Z' + "'" + ",");

		for (char c = 'a'; c < 'z'; c++) {
			System.out.print("'" + c + "'" + ",");
		}
		System.out.print("'" + 'z' + "'");
	}

	public void testMacHashKeyBytes() {
		System.out.println(MacTransferUtil.macHashKeyBytes("2A-43-1D-A1-BC-FA").length);
		display(MacTransferUtil.macHashKeyBytes("2A-43-1D-A1-BC-FA"));
	}

	public static void display(byte[] bytes) {
		System.out.println(Bytes.toInt(bytes));
		System.out.println(Integer.parseInt("A1-BC-FA".replace("-", ""), 16));
		for (byte b : bytes) {
			System.out.print(b);
			System.out.print(" ");
		}
		System.out.println();
	}
}
