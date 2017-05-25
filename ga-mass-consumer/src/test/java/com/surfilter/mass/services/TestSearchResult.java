package com.surfilter.mass.services;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.dao.KeyPerDao;
import com.surfilter.mass.dao.db.JdbcConfig;
import com.surfilter.mass.dao.db.KeyPerDaoImpl;
import com.surfilter.mass.dao.redis.JedisHelper;
import com.surfilter.mass.entity.MatchInfo;
import com.surfilter.mass.services.match.ACHelper;
import com.surfilter.mass.services.match.ServiceInfoHelper;
import com.surfilter.mass.services.match.algthm.AhoCorasick;
import com.surfilter.mass.services.match.algthm.SearchResult;
import com.surfilter.mass.utils.ImcaptureUtil;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TestSearchResult extends TestCase {

	String driverClassName = ImcaptureConsts.DRIVER_CLASS_NAME;
	String url = "jdbc:mysql://192.168.0.112:3306/gacenter_gd?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull";
	String userName = "root";
	String password = "surfilter1218";

	public void testAssertRow() {
		// 22-22-22-22-22-22M in alarm mac: result=false
		assertRow("22-22-22-22-22-22M|MULLI|MULLS|13888888987P|182293811721009999A|");

		// 22-22-22-22-22-22M in alarm mac: result=false
		assertRow("22-22-22-22-22-22M|MULLI|MULLS|13888888987P|182293811721009989A|");

		// 22-22-22-22-22-22M in alarm mac: result=true
		assertRow("22-22-22-22-22-22M|MULLI|MULLS|13888888888P|182293811721009999A|");

		// 99-99-99-99-99-88 in alarm mac, 13888888888 in alarm phone:
		// result=true
		assertRow("99-99-99-99-99-88M|MULLI|MULLS|13888888888P|182293811721009999A|");

		// 99-99-99-99-99-88 in alarm mac: result=false
		assertRow("99-99-99-99-99-88M|MULLI|MULLS|13888888889P|182293811721009999A|");

		// 66-66-66-66-66-66 in alarm mac: result=true
		assertRow("66-66-66-66-66-66M|MULLI|MULLS|13888888889P|182293811721009999A|");

		// 66-66-66-66-66-66 in alarm mac: result=true
		assertRow("66-66-66-66-66-66M|MULLI|MULLS|13888888889P|182293811721009989A|");

		// 66-66-66-66-66-66 in alarm mac: result=true
		assertRow("66-66-66-66-66-66M|MULLI|MULLS|13888888667P|182293811721009989A|");

		assertRow("22-22-22-22-22-23M|MULLI|MULLS|13888888987P|182293811721009999A|");

		assertRow("99-99-99-99-99-88M|MULLI|MULLS|13888888987P|182293811721009999A|");
	}

	public void testDisplay() {
		System.out.println(new HashMap<>(0));
	}

	public boolean assertRow(String matchRow) {
		JdbcConfig confg = JdbcConfig.getInstance(new MassConfiguration());
		KeyPerDao keyPerDao = new KeyPerDaoImpl(confg);
		AhoCorasick<MatchInfo> ac = ACHelper.getInstance(5).getAC(keyPerDao);

		Iterator<SearchResult<MatchInfo>> scResults = ac.search(matchRow.getBytes());

		System.out.println(scResults.hasNext());

		return scResults.hasNext();
	}

	public void testQueryAlarmInfos(int hours) {
		JdbcConfig confg = JdbcConfig.getInstance(new MassConfiguration());
		KeyPerDao keyPerDao = new KeyPerDaoImpl(confg);
		System.out.println(keyPerDao.getAlarmInfos(hours));
	}

	@Test
	public void testQueryAlarmInfos() {
		testQueryAlarmInfos(1440);
		testQueryAlarmInfos(10000);
	}

	public void testGetMacFilterConf() {
		JdbcConfig confg = JdbcConfig.getInstance(new MassConfiguration());
		KeyPerDao keyPerDao = new KeyPerDaoImpl(confg);
		System.out.println(keyPerDao.getMacFilterConf());
	}

	public void testServiceInfoHelper() {
		ServiceInfoHelper instance = ServiceInfoHelper.getInstance(new MassConfiguration());
		instance.getServiceInfo();
		System.out.println(instance.getMacFilterMap());
	}

	public void testFilterMac() {
		System.out.println(filterByMysql(new String[] { "34-A6-8C", "-1", "", "1", "0" })); // true
		System.out.println(filterByMysql(new String[] { "14-A6-8C", "-1", "", "1", "0" })); // true
		System.out.println(isFilterByInitMap(new String[] { "34-A6-8C", "-1", "", "1", "0" })); // true
		System.out.println(isFilterByInitMap(new String[] { "34-A6-8C", "-1", "", "1", "10" })); // false
		System.out.println(isFilterByInitMap(new String[] { "34-A6-8C", "-1", "", "-1", "10" })); // false
	}

	private boolean filterByMysql(String[] arrays) {
		ServiceInfoHelper instance = ServiceInfoHelper.getInstance(new MassConfiguration());
		instance.getServiceInfo();
		System.out.println(instance.getMacFilterMap());

		return ImcaptureUtil.filter(
				JedisHelper.getInstance().getTemplate().hgetAll(ImcaptureConsts.MAC_COMPANY_FILTER_REDIS_KEY).keySet(),
				instance.getMacFilterMap(), arrays);
	}

	private boolean isFilterByInitMap(String[] arrays) {
		Map<String, String> map = new HashMap<>();

		map.put("mass_filter_mac_no_company_id", "true");
		map.put("mass_filter_power_negative_one", "true");
		map.put("mass_filter_mac_ap", "true");
		map.put("mass_filter_power_zero", "true");

		return ImcaptureUtil.filter(
				JedisHelper.getInstance().getTemplate().hgetAll(ImcaptureConsts.MAC_COMPANY_FILTER_REDIS_KEY).keySet(),
				map, arrays);
	}

	public void testRedisMacCompanyPrefix() {
		Set<String> set = JedisHelper.getInstance().getTemplate().hgetAll(ImcaptureConsts.MAC_COMPANY_FILTER_REDIS_KEY)
				.keySet();
		System.out.println(set);
	}

	public AhoCorasick<MatchInfo> getBy(String match) {
		AhoCorasick<MatchInfo> acinfo = new AhoCorasick<MatchInfo>();

		Long storeId = 1L;
		Long macId = 1L;
		String matchType = "mac";
		String matchValue = match;
		String matchChildValue = "matchChildValue";
		String alarmPhones = "alarmPhones";
		String alarmEmails = "alarmEmails";

		MatchInfo info = new MatchInfo(storeId, macId, matchType, matchValue, matchChildValue, alarmPhones, alarmEmails,
				"", "", 5, "2", "30", "", "", "", "", "", 1 + "", false, false,null);

		// add searchKey bytes and results
		acinfo.add(match.getBytes(), info);

		acinfo.prepare();

		return acinfo;
	}

	public boolean searchBy(String mac, String matchRow) {
		Iterator<SearchResult<MatchInfo>> scResults = getBy(mac).search(matchRow.getBytes());

		System.out.println(scResults.hasNext());

		return scResults.hasNext();
	}

	/**
	 * 查找即包含关系
	 */
	public void testSearchBy() {
		// true
		searchBy("22-22-22-22-22-22M", "22-22-22-22-22-22M|MULLI|MULLS|13888888987P|182293811721009999A|");

		// true
		searchBy("99-99-99-99-99-88M", "99-99-99-99-99-88M|MULLI|MULLS|13888888888P|182293811721009999A|");

		// false
		searchBy("99-99-99-99-99-87M", "99-99-99-99-99-88M|MULLI|MULLS|13888888888P|182293811721009999A|");

		// false
		searchBy("22-22-22-22-22-24M", "22-22-22-22-22-22M|MULLI|MULLS|13888888987P|182293811721009999A|");

		// Phone: 13888888987P : true
		searchBy("13888888987P", "22-22-22-22-22-22M|MULLI|MULLS|13888888987P|182293811721009999A|");

		// Account: 18229381172 : true
		searchBy("13888888987", "22-22-22-22-22-22M|MULLI|MULLS|13888888987P|182293811721009999A|");

		// ProtocolType: 1009999A : true
		searchBy("1009999A", "22-22-22-22-22-22M|MULLI|MULLS|13888888987P|182293811721009999A|");
	}
}
