package com.surfilter.mass.tools.cli;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.VehicleConfig;
import com.surfilter.mass.tools.service.NewVehicleDataService;
import com.surfilter.mass.tools.util.Dates;

/**
 * 根据起止时间配置抓取视频车辆数据 for 新系统
 * 
 * @author zealot
 *
 */
public class VehicleDataCatchApp {

	static final Logger LOG = LoggerFactory.getLogger(VehicleDataCatchApp.class);

	public static void main(String[] args) {
		VehicleConfig config = new VehicleConfig(new MassConfiguration());
		NewVehicleDataService service = new NewVehicleDataService(config);

		Calendar c = Calendar.getInstance();

		Date startDate = config.getStartDate();
		c.setTime(startDate);
		int startHours = c.get(Calendar.HOUR_OF_DAY);

		Date endDate = config.getEndDate();
		c.setTime(endDate);
		int endHours = c.get(Calendar.HOUR_OF_DAY);

		String startStr = Dates.date2Str(startDate, "yyyy-MM-dd");
		String endStr = Dates.date2Str(endDate, "yyyy-MM-dd");

		// 抓取从 startDate-endDate之间的数据
		fetchDataBetweenDate(service, startDate, startHours, endDate, endHours, startStr, endStr);
	}

	public static void fetchDataBetweenDate(NewVehicleDataService service, Date startDate, int startHours, Date endDate,
			int endHours, String startStr, String endStr) {
		if (startStr.equals(endStr)) { // 抓取同一天的数据
			// 抓取开始时间到结束时间
			for (int i = startHours; i < endHours; i++) {
				service.fetchByHour(startStr, i);
			}
		} else {
			// 抓取开始时间到末尾时间
			for (int i = startHours; i < 24; i++) {
				service.fetchByHour(startStr, i);
			}

			// 获取开始时间的后一天
			startDate = Dates.tormorrow(startDate);

			// 获取结束时间的前一天
			Date circle = Dates.yesterday(endDate);

			/** 循环爬取历史数据 */
			while (startDate.before(circle)) {
				// 爬取一天的数据
				for (int i = 0; i < 24; i++) {
					service.fetchByHour(Dates.date2Str(startDate, "yyyy-MM-dd"), i);
				}

				// 改变开始时间
				startDate = Dates.tormorrow(startDate);
			}

			// 抓取结束时间到截止时间
			for (int i = 0; i < endHours; i++) {
				service.fetchByHour(endStr, i);
			}
		}
	}

}
