package com.surfilter.mass.tools.cli;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.ZtryRevokeConfig;
import com.surfilter.mass.tools.entity.ZtryPageInfo;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.NumberUtil;
import com.surfilter.mass.tools.util.Threads;
import com.surfilter.mass.tools.util.ZtryCatchUtil;

/**
 * Ztry url 爬虫程序
 * 
 * @author zealot
 *
 */
public class ZtryRevokeUrlCatchApp {

	static final Logger LOG = LoggerFactory.getLogger(ZtryRevokeUrlCatchApp.class);

	public static final int PAGE_SIZE = 99;

	public static void main(String[] args) {
		ZtryRevokeConfig config = new ZtryRevokeConfig(new MassConfiguration());

		Date start = config.getZtryCatchStartDate();

		Calendar instance = Calendar.getInstance();
		instance.setTime(start);
		int startYear = instance.get(Calendar.YEAR);

		Date end = config.getZtryCatchEndDate();
		instance.setTime(end);
		int endYear = instance.get(Calendar.YEAR);

		String urlsDst = config.getZtryUrlsDst();

		for (int year = startYear; year <= endYear; year++) {
			for (int month = 1; month <= 12; month++) { // month
				fetchUrl(config, urlsDst, year, month);
				Threads.sleep(1000);
			}
		}
	}

	/**
	 * 根据年份及月份抓取数据，每个月分1-15,15-end抓取
	 * 
	 * @param config
	 * @param urlsDst
	 * @param detailDst
	 * @param year
	 * @param month
	 */
	private static void fetchUrl(ZtryRevokeConfig config, String urlsDst, int year, int month) {
		String m = NumberUtil.fillWith0(month);
		String start1 = year + "-" + m + "-01";
		String end1 = year + "-" + m + "-15";
		String url = config.getZtryCatchUrl().replace("startDate", start1).replace("endDate", end1);
		LOG.debug("start fetch startDate:{}, endDate:{}", start1, end1);
		fetchUrl(config.getZtryCookies(), url, FileUtil.date2Path(urlsDst, start1, end1));

		String start2 = year + "-" + m + "-16";
		String end2 = year + "-" + m + "-" + NumberUtil.fetchMonthLastDay(year, month);
		url = config.getZtryCatchUrl().replace("startDate", start2).replace("endDate", end2);
		LOG.debug("start fetch startDate:{}, endDate:{}", start2, end2);
		fetchUrl(config.getZtryCookies(), url, FileUtil.date2Path(urlsDst, start2, end2));
	}

	/**
	 * Ztry url及详细信息爬取
	 * 
	 * @param cookie
	 * @param startDate
	 * @param endDate
	 * @param urlsFile
	 */
	public static void fetchUrl(Map<String, String> cookie, String url, String urlsFile) {
		ZtryPageInfo info = new ZtryPageInfo();
		info.setPageSize(PAGE_SIZE);
		info.setCookie(cookie);
		ZtryCatchUtil.fetchUrl(url, new File(urlsFile), 0, info, true);
	}
}
