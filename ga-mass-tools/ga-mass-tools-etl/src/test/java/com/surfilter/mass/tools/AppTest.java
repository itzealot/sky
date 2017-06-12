package com.surfilter.mass.tools;

import com.google.common.base.Splitter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {
	public AppTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testApp() {
		assertTrue(true);
		System.out.println("\"\t\"".replaceAll("\"", ""));
	}

	public void testDisplay() {
		System.out.println(",asdas,|".replace(",", " "));
		System.out.println(",asdas,|".replace(",", " ").trim());
	}

	public void testLiuShui() {
		System.out.println(
				"src_account,src_account_type,source,org_code,protocolType,src_account_entity,dst_account_type,dst_account,dst_account_entity,user_account,user_account_type,pay_id,pay_type,pay_content,pay_num,pay_currency_type,pay_money,pay_time,src_name,src_phone,src_addr,dst_name,dst_phone,dst_addr,sys_source,remark,pay_ip,pay_port,pay_mac,pay_imei,pay_imsi,pay_hardware,pay_longitude,pay_latitude,terminal_type,terminal_id,terminal_os,terminal_os_version,create_time,pay_time_p"
						.toUpperCase());
	}

	public void testReg() {
		System.out.println(
				"userId,orgCode,protocolType,userName,name,sex,account,accountType,idType,identification,identificationType,phone,fixedPhone,headerImg,regMail,regIpAddr,regMac,regPort,regIMEI,regHardwareStr,regLongitude,regLatitude,regTime,regUpdateTime,nation,area,realAddr,relationPhone,postCode,remark,createTime,CREATE_TIME_P"
						.toUpperCase());
	}

	public void testSplit() {
		display(" ", "ass bb  cc ");
		display(" ", "ass bb cc ");
	}

	public void display(String spliter, String line) {
		for (String s : Splitter.on(spliter).trimResults().split(line)) {
			System.out.println(s);
		}

		System.out.println("-----------");
	}

}
