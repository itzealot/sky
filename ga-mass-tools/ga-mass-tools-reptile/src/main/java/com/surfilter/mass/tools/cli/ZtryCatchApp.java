package com.surfilter.mass.tools.cli;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.ZtryConfig;
import com.surfilter.mass.tools.entity.ZtryPageInfo;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.NumberUtil;
import com.surfilter.mass.tools.util.Threads;
import com.surfilter.mass.tools.util.ZtryCatchUtil;

/**
 * Ztry Url及详细信息爬虫程序
 * 
 * @author zealot
 *
 */
public class ZtryCatchApp {

	static final Logger LOG = LoggerFactory.getLogger(ZtryCatchApp.class);

	public static final int PAGE_SIZE = 99;

	public static void main(String[] args) {
		ZtryConfig config = new ZtryConfig(new MassConfiguration());

		Date start = config.getZtryCatchStartDate();

		Calendar instance = Calendar.getInstance();
		instance.setTime(start);
		int startYear = instance.get(Calendar.YEAR);

		Date end = config.getZtryCatchEndDate();
		instance.setTime(end);
		int endYear = instance.get(Calendar.YEAR);

		String urlsDst = config.getZtryUrlsDst();
		String detailDst = config.getZtryDetailDst();

		for (int year = startYear; year <= endYear; year++) {
			for (int month = 1; month <= 12; month++) { // month
				fetchUrlAndDetail(config, urlsDst, detailDst, year, month);
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
	private static void fetchUrlAndDetail(ZtryConfig config, String urlsDst, String detailDst, int year, int month) {
		String m = NumberUtil.fillWith0(month);
		String start1 = year + "-" + m + "-01";
		String end1 = year + "-" + m + "-15";
		String url = config.getZtryCatchUrl().replace("startDate", start1).replace("endDate", end1);
		LOG.debug("start fetch startDate:{}, endDate:{}", start1, end1);
		fetchUrlAndDetail(config.getZtryCookies(), url, FileUtil.date2Path(urlsDst, start1, end1),
				FileUtil.date2Path(detailDst, start1, end1));

		String start2 = year + "-" + m + "-16";
		String end2 = year + "-" + m + "-" + NumberUtil.fetchMonthLastDay(year, month);
		url = config.getZtryCatchUrl().replace("startDate", start2).replace("endDate", end2);
		LOG.debug("start fetch startDate:{}, endDate:{}", start2, end2);
		fetchUrlAndDetail(config.getZtryCookies(), url, FileUtil.date2Path(urlsDst, start2, end2),
				FileUtil.date2Path(detailDst, start1, end1));
	}

	/**
	 * Ztry url及详细信息爬取
	 * 
	 * @param cookie
	 * @param startDate
	 * @param endDate
	 * @param urlsFile
	 */
	public static void fetchUrlAndDetail(Map<String, String> cookie, String url, String urlsFile, String detailsFile) {
		ZtryPageInfo info = new ZtryPageInfo();
		info.setPageSize(PAGE_SIZE);
		info.setCookie(cookie);
		ZtryCatchUtil.fetchUrlAndDetail(url, new File(urlsFile), new File(detailsFile), 0, info, false);
	}
}
