package com.sky.project.share.api.kafka;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sky.project.share.common.thread.Threads;
import com.sky.project.share.common.thread.pool.NamedThreadFactory;

public class ExecutorsTest {

	public static void main(String[] args) {
		ExecutorService deamons = Executors.newFixedThreadPool(4, new NamedThreadFactory("Deamon", true));

		for (int i = 0; i < 4; i++) {
			deamons.execute(() -> {
				while (true) {
					System.out.println(Thread.currentThread().getName() + " sleep 1s");
					Threads.sleep(1000);
				}
			});
		}

		Threads.sleep(10000);
		System.out.println("finish.......");
	}
}
