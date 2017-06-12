package com.surfilter.mass.tools.task.impl;

import java.util.Timer;
import java.util.TimerTask;

import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SysConstant;

/**
 * Timer Holder
 * 
 * @author zealot
 *
 */
public class TimerHolder {

	private static volatile TimerHolder instance = null;
	private static Object LOCK = new Object();
	private Timer timer;

	public static TimerHolder getInstance(MassConfiguration conf, Runnable r) {
		if (instance == null) {
			synchronized (LOCK) {
				if (instance == null) {
					instance = new TimerHolder(conf, r);
				}
			}
		}

		return instance;
	}

	/**
	 * 每隔多长执行一次任务
	 * 
	 * @param conf
	 * @param r
	 */
	private TimerHolder(MassConfiguration conf, Runnable r) {
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					r.run();
				}
			}, 100, conf.getInt(SysConstant.TIMER_INTERVAL, 30) * 1000);
		}
	}
}
