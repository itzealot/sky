package com.surfilter.gamass.util;

import java.util.Arrays;

import com.surfilter.gamass.entity.Relation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ParseRelationUtilTest extends TestCase {

	private String idFrom = "11-22-22-22-22-22";
	private String fromType = "1020002";
	private String idTo = "13888888888";
	private String toType = "1020004";
	private String firstStartTime = "1482724013";
	private String firstTerminalNum = "44030200000001";
	private String source = "1";
	private String createTime = "1482724015";
	private String sysSource = "2";
	private String companyId = "111111111";
	private String createTimeP = "20161226";

	private Relation r = new Relation(idFrom, fromType, idTo, toType, firstStartTime, firstTerminalNum, source,
			createTime, sysSource, companyId, createTimeP);

	private Relation rBig = new Relation(idFrom, fromType, idTo, toType, "1482724025", "44030200000025", source,
			createTime, sysSource, companyId, createTimeP);

	private Relation rSma = new Relation(idFrom, fromType, idTo, toType, "1482724011", "44030200000011", source,
			createTime, sysSource, companyId, createTimeP);

	public ParseRelationUtilTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(ParseRelationUtilTest.class);
	}

	public void testApp() {
		assertTrue(true);
	}

	public void testJoinRelationRowKey() {
		System.out.println(ParseRelationUtil.joinRelationRowKey(r, true));
		System.out.println(ParseRelationUtil.joinRelationReverseRowKey(r, true));
	}

	public void testJoinRelationValues() {
		System.out.println(ParseRelationUtil.joinRelationValues(r));
	}

	public void testParse2Certification() {
		System.out.println(ParseRelationUtil.parse2Certification(r)[0]);
		System.out.println(ParseRelationUtil.parse2Certification(r)[1]);
	}

	public void testparse2CertificationTrack() {
		System.out.println(ParseRelationUtil.parse2CertificationTrack(r)[0]);
		System.out.println(ParseRelationUtil.parse2CertificationTrack(r)[1]);
	}

	public void testParseAndWriteCertificationTrack() {
		ParseRelationUtil.parseAndWriteCertificationTrack(Arrays.asList(r), dstDir);
	}

	String hbaseParams = "rzx168,rzx169,rzx177|2181|rzx168:60000|hdfs://rzx168:9000/hbase";
	String cetificationTableName = "certification";
	String serversInfo = "192.168.0.104|6379";
	String dstDir = "D:/test";

	public void testParseAndWriteCertification() {
		ParseRelationUtil.parseAndWriteCertification(Arrays.asList(r), dstDir, hbaseParams, cetificationTableName,
				serversInfo, true);
		ParseRelationUtil.parseAndWriteCertification(Arrays.asList(rBig), dstDir, hbaseParams, cetificationTableName,
				serversInfo, true);
		ParseRelationUtil.parseAndWriteCertification(Arrays.asList(rSma), dstDir, hbaseParams, cetificationTableName,
				serversInfo, true);
	}

	public void testparseAndWriteRelation() {
		ParseRelationUtil.parseAndWriteRelation(Arrays.asList(r), dstDir, hbaseParams, "relation", true);
		ParseRelationUtil.parseAndWriteRelation(Arrays.asList(rBig), dstDir, hbaseParams, "relation", true);
		ParseRelationUtil.parseAndWriteRelation(Arrays.asList(rSma), dstDir, hbaseParams, "relation", true);
	}
}
