package com.surfilter.mass.tools.cli;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.PeopleHotelInfoConfig;
import com.surfilter.mass.tools.util.NumberUtil;
import com.surfilter.mass.tools.util.PeopleHotelInfoCatchUtil;

/**
 * 旅馆信息抓取
 * 
 * @author zealot
 *
 */
public class PeopleHotelInfoCatchApp {

	static final Logger LOG = LoggerFactory.getLogger(PeopleHotelInfoCatchApp.class);

	public static void main(String[] args) {
		PeopleHotelInfoConfig config = new PeopleHotelInfoConfig(new MassConfiguration());

		Date start = config.getStartDate();

		Calendar instance = Calendar.getInstance();
		instance.setTime(start);
		int startYear = instance.get(Calendar.YEAR);
		int startMonth = instance.get(Calendar.MONTH) + 1;
		int startDay = instance.get(Calendar.DAY_OF_MONTH);

		Date end = config.getEndDate();
		instance.setTime(end);
		int endYear = instance.get(Calendar.YEAR);
		int endMonth = instance.get(Calendar.MONTH) + 1;
		int endDay = instance.get(Calendar.DAY_OF_MONTH);

		for (int year = startYear; year <= endYear; year++) {
			if (year > startYear && year < endYear) { // (startYear, endYear)
				for (int month = 1; month <= 12; month++) {
					int lastDay = NumberUtil.fetchMonthLastDay(year, month);
					for (int day = 1; day <= lastDay; day++) {
						fetchPeopleHotelInfo(year, month, day);
					}
				}
			} else if (year == startYear) { // year=startYear
				int lastDay = NumberUtil.fetchMonthLastDay(year, startMonth);
				for (int day = startDay; day <= lastDay; day++) {
					fetchPeopleHotelInfo(year, startMonth, day);
				}

				for (int month = startMonth + 1; month <= 12; month++) {
					lastDay = NumberUtil.fetchMonthLastDay(year, month);
					for (int day = 1; day <= lastDay; day++) {
						fetchPeopleHotelInfo(year, month, day);
					}
				}
			} else { // year=endYear
				int lastDay = NumberUtil.fetchMonthLastDay(year, endMonth);

				for (int day = 1; day <= endDay; day++) {
					fetchPeopleHotelInfo(year, endMonth, day);
				}

				for (int month = 1, len = endMonth - 1; month <= len; month++) {
					lastDay = NumberUtil.fetchMonthLastDay(year, month);
					for (int day = 1; day <= lastDay; day++) {
						fetchPeopleHotelInfo(year, month, day);
					}
				}
			}
		}
	}

	private static void fetchPeopleHotelInfo(int year, int month, int day) {
		String dateStr = year + NumberUtil.fillWith0(month) + NumberUtil.fillWith0(day);
		String startTime = dateStr + "0000";
		String endTime = dateStr + "2359";
		String fileName = "D:/results/hotel/" + dateStr + ".txt";
		File file = new File(fileName);
		LOG.debug("startTime:{}, endTime:{}, fileName:{}", startTime, endTime, fileName);

		PeopleHotelInfoCatchUtil.fetchPeopleHotelInfos(1, 10, 25500, startTime, endTime, file);
	}

}
