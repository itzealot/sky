package com.surfilter.mass.tools.cli;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.VehicleConfig;
import com.surfilter.mass.tools.service.NewVehicleDataService;
import com.surfilter.mass.tools.util.Dates;

/**
 * 每天抓取昨天的视频车辆数据 for 新系统
 * 
 * @author zealot
 *
 */
public class VehicleDataCatchTimerApp {

	static final Logger LOG = LoggerFactory.getLogger(VehicleDataCatchTimerApp.class);
	static final int ONE_DAY_MILLS = 24 * 60 * 60 * 1000;
	static final int ONE_HOUR_MILLS = 1 * 60 * 60 * 1000;

	public static void main(String[] args) {
		VehicleConfig config = new VehicleConfig(new MassConfiguration());
		NewVehicleDataService service = new NewVehicleDataService(config);

		Timer timer = new Timer();

		/**
		 * 定时抓取一小时前的数据
		 */
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				long start = System.currentTimeMillis() / 1000;

				Calendar c = Calendar.getInstance();
				// 抓取一小时前的数据
				int hours = c.get(Calendar.HOUR_OF_DAY) - 1;
				if (hours < 0) {
					return;
				}

				service.fetchByHour(Dates.date2Str(c.getTime(), "yyyy-MM-dd"), hours);

				long spend = System.currentTimeMillis() / 1000 - start;

				LOG.info("finish run timer for hour:{}, spends:{} s", Dates.date2Str(c.getTime(), "yyyy-MM-dd+HH"),
						spend);
			}
		}, ONE_HOUR_MILLS, ONE_HOUR_MILLS);
	}

}
