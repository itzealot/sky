package com.surfilter.mass.tools;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTest {
	public static void main(String[] args) {
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				System.out.println(Calendar.getInstance().getTime());
			}
		}, 1000L, 1000L);
	}
}
