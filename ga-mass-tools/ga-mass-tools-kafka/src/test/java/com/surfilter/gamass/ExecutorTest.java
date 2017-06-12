package com.surfilter.gamass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.surfilter.mass.tools.util.Threads;

public class ExecutorTest {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(10);

		executor.submit(new Runnable() {
			@Override
			public void run() {
				while (true) {
					System.out.println("thread name...");
					Threads.sleep(100);
				}
			}
		});

		Threads.sleep(2000);
		executor.shutdown();
	}

}
