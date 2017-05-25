package com.surfilter.mass.utils;

import static com.surfilter.mass.utils.ImcaptureUtil.isEmpty;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.conf.UserType;
import com.surfilter.mass.dao.KeyPerDao;
import com.surfilter.mass.dao.db.JdbcConfig;
import com.surfilter.mass.dao.db.KeyPerDaoImpl;
import com.surfilter.mass.entity.AlarmInfo;
import com.surfilter.mass.entity.ClusterAlarmResult;
import com.surfilter.mass.entity.ServiceInfo;
import com.surfilter.mass.services.support.AlarmInfoAnalysis;

import junit.framework.TestCase;

public class ImcaptureUtilTest extends TestCase {

	// set the conf dir for test
	private static MassConfiguration conf;
	static {
		System.setProperty("user.dir", "D:/code/fxj/ga-mass/ga-mass-nimcapture/src/main/assembly");
		conf = new MassConfiguration();
	}

	String driverClassName = ImcaptureConsts.DRIVER_CLASS_NAME;
	String url = "jdbc:mysql://192.168.0.112:3306/gacenter_shiju?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull";
	String userName = "root";
	String password = "surfilter1218";
	JdbcConfig confg = JdbcConfig.getInstance(conf);

	public void testAbs() {
		System.out.println(ImcaptureUtil.abs(1l));
		System.out.println(ImcaptureUtil.abs(0));
		System.out.println(ImcaptureUtil.abs(-12l));
	}

	public void testBetween() {
		System.out.println(ImcaptureUtil.between(100l, 80l, 20));
		System.out.println(ImcaptureUtil.between(101l, 80l, 20));
		System.out.println(ImcaptureUtil.between(100l, 110l, 20));
	}

	public void testGetProtocolType() {
		System.out.println(ImcaptureUtil.getProtocolType(new AlarmInfo(null, "M", "1020002", 1l, 1l, 1l, null, null,
				null, null, null, 1, 1, null, null, null, null, null)));
		System.out.println(ImcaptureUtil.getProtocolType(new AlarmInfo(null, "1", "", 1l, 1l, 1l, null, null, null,
				null, null, 1, 1, null, null, null, null, null)));
		System.out.println(ImcaptureUtil.getProtocolType(new AlarmInfo(null, "2", "", 1l, 1l, 1l, null, null, null,
				null, null, 1, 1, null, null, null, null, null)));
		System.out.println(ImcaptureUtil.getProtocolType(new AlarmInfo(null, "3", "", 1l, 1l, 1l, null, null, null,
				null, null, 1, 1, null, null, null, null, null)));
		System.out.println(ImcaptureUtil.getProtocolType(new AlarmInfo(null, "4", "", 1l, 1l, 1l, null, null, null,
				null, null, 1, 1, null, null, null, null, null)));
		System.out.println(ImcaptureUtil.getProtocolType(new AlarmInfo(null, "5", "1030036", 1l, 1l, 1l, null, null,
				null, null, null, 1, 1, null, null, null, null, null)));
	}

	public void testSaveClusterAlarmResults() {
		Map<String, Set<ClusterAlarmResult>> map = new HashMap<>();
		KeyPerDao keyPerDao = new KeyPerDaoImpl(confg);
		Set<ClusterAlarmResult> results = new HashSet<>();

		long startAlarmTime = new Date().getTime() / 1000;
		String serviceCode = "44030635511002";
		ClusterAlarmResult result = new ClusterAlarmResult(serviceCode, "440000", "440300", "440303", "44030611",
				"A;B;C", startAlarmTime, startAlarmTime + 300, 1,
				"1494921005|1494922115,1494922015|1494922115,1494922105|1494922105");
		result.setClusterTime(10);
		results.add(result);
		map.put(serviceCode, results);

		serviceCode = "44030635511003";

		Set<ClusterAlarmResult> results2 = new HashSet<>();
		results2.add(new ClusterAlarmResult("44030635511003", "440000", "440300", "440303", "44030611", "1;2;3",
				startAlarmTime, startAlarmTime + 300L, 1,
				"1494922005|1494922115,1494922005|1494922115,1494922105|1494922105"));
		map.put(serviceCode, results2);

		keyPerDao.saveClusterAlarmResults(map, 3600);
	}

	public void testGetAlarmInfos() {
		KeyPerDao keyPerDao = new KeyPerDaoImpl(confg);
		System.out.println(keyPerDao.getAlarmInfos(300));
	}

	public void testAlarmInfoAnalysis() {
		KeyPerDao keyPerDao = new KeyPerDaoImpl(confg);
		Map<String, ServiceInfo> serviceInfoMap = keyPerDao.querySerCodeType();

		new AlarmInfoAnalysis(keyPerDao).alalysis(serviceInfoMap, 3 * 24 * 60, 120L, 3, 3600);
	}

	public void testAnalysisCluster() {
		KeyPerDao keyPerDao = new KeyPerDaoImpl(confg);
		Map<String, ServiceInfo> serviceInfoMap = keyPerDao.querySerCodeType();
		// 出现时间在指定时间范围内
		System.out.println(ImcaptureUtil.analysisCluster(keyPerDao.getAlarmInfos(300), serviceInfoMap, 119L, 3));

		// 出现时间在指定时间范围内
		System.out.println(ImcaptureUtil.analysisCluster(keyPerDao.getAlarmInfos(300), serviceInfoMap, 120L, 3));
	}

	public void testGetUnixTime() {
		System.out.println(Dates.getUnixTime("1970-01-01 08:00:00"));
		System.out.println(Dates.getUnixTime("2016-10-18 13:57:40"));
		System.out.println(Dates.unixTime("2016-10-18 13:57:40"));
		System.out.println(Dates.unixTime("2016-10-18 13:57:40.0"));
		System.out.println(Dates.unixTime("2016-10-18 13:57:40.00"));
		System.out.println(Dates.unixTime("2016-10-18 13:57:40.000"));
		DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		DateTime dateTime = DateTime.parse("2012-12-21 23:22:45", format);
		System.out.println(dateTime.getMillis() / 1000);
	}

	public void testIsEmpty() {
		System.out.println(isEmpty("0"));
		System.out.println(isEmpty("0-1"));
		System.out.println(isEmpty("-1"));
		System.out.println(isEmpty(""));
		System.out.println(isEmpty(null));
		System.out.println(isEmpty(" "));

		System.out.println(isEmpty(1L));
		System.out.println(isEmpty(-1L));
		System.out.println(isEmpty(-1));
		System.out.println(isEmpty(0L));
		System.out.println(isEmpty(0));
	}

	AlarmInfo a = new AlarmInfo("matchValue", "5", "1030036", 1l, 1l, 10000l, 100020l, "phoneAccount", "mailAccount",
			"serviceCode-1", "1", 1, 30, "1020002", "AAAAAA", 1l, "13888888888", "张三");
	AlarmInfo b = new AlarmInfo("身份证", "5", "1021111", 1l, 1l, 11001l, 100021l, "phoneAccount", "mailAccount",
			"serviceCode-1", "1", 1, 30, "1020002", "AAAAAA", 1l, "13888888888", "张三");

	AlarmInfo src = new AlarmInfo("matchValue", "5", "1030036", 1l, 1l, 10000l, 100020l, "phoneAccount", "mailAccount",
			"serviceCode-1", "1", 1, 30, "1021111", "身份证", 1l, "13888888888", "张三");
	AlarmInfo dst = new AlarmInfo("AAAAAA", "5", "1020002", 1l, 1l, 11001l, 100021l, "phoneAccount", "mailAccount",
			"serviceCode-1", "1", 1, 30, "", "", 1l, "13888888888", "李四");

	AlarmInfo src1 = new AlarmInfo("matchValue", "5", "1030036", 1l, 1l, 10000l, 100020l, "phoneAccount", "mailAccount",
			"serviceCode-1", "1", 1, 30, "", "", 1l, "13888888888", "");
	AlarmInfo dst1 = new AlarmInfo("AAAAAA", "5", "1020002", 1l, 1l, 11001l, 100021l, "phoneAccount", "mailAccount",
			"serviceCode-1", "1", 1, 30, "", "", 1l, "13888888888", "李四");

	public void testDistanceIsBetween() {
		System.out.println(ImcaptureUtil.distanceIsBetween(a, b, 1000l));
	}

	public void testJoinAlarmInfo() {
		System.out.println(ImcaptureUtil.joinAlarmInfo(a));
		System.out.println(ImcaptureUtil.joinAlarmInfo(b));
	}

	public void testUpdateWithPriority() {
		System.out.println("src:" + src);
		System.out.println("dst:" + dst);
		ImcaptureUtil.updateZdInfoWithPriority(src, dst);
		System.out.println(dst);
		System.out.println(ImcaptureUtil.joinAlarmInfo(dst));

		System.out.println("src1:" + src1);
		System.out.println("dst1:" + dst1);
		ImcaptureUtil.updateZdInfoWithPriority(src1, dst1);
		System.out.println(dst1);
		System.out.println(ImcaptureUtil.joinAlarmInfo(dst1));
	}

	public void testCompareWithIdType() {
		System.out.println(ImcaptureUtil.compareWithIdType("1021111", "1021111"));
		System.out.println(ImcaptureUtil.compareWithIdType("1020002", "1021111"));
		System.out.println(ImcaptureUtil.compareWithIdType("1020004", "1021111"));
		System.out.println(ImcaptureUtil.compareWithIdType("1020004", "1020002"));
		System.out.println(ImcaptureUtil.compareWithIdType("1020002", "1020004"));
		System.out.println(ImcaptureUtil.compareWithIdType(UserType.WX.getValue(), UserType.QQ.getValue()));
		System.out.println(ImcaptureUtil.compareWithIdType(UserType.IMSI.getValue(), UserType.IMEI.getValue()));
		System.out.println(ImcaptureUtil.compareWithIdType(UserType.IMSI.getValue(), UserType.WX.getValue()));
	}

	public void testContainsGangList() {
		System.out.println(ImcaptureUtil.containsGangList("4577,4576,4572,4575,4574", "4577,4576,4572,4575,4574"));
		System.out.println(ImcaptureUtil.containsGangList("4577,4576,4572,4575,4574", "4574,4577,4572,4576,4575"));
		System.out.println(ImcaptureUtil.containsGangList("4577,4576,4572,4575,4574", "4572,4576,4574,4577"));
		System.out.println(ImcaptureUtil.containsGangList("4572,4576,4574,4577", "4577,4576,4572,4575,4574"));
	}

	public void testSortAsc() {
		String[] ganlist = "1,3,2".split(",");
		String[] gangTime = "10:10,12:00,11:00".split(",");

		ImcaptureUtil.sortAsc(ganlist, gangTime);

		System.out.println(Arrays.asList(ganlist));
		System.out.println(Arrays.asList(gangTime));
	}

}
