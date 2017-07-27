package com.sky.project.share.jdk.threadlocal;

import java.util.Random;

import com.sky.project.share.common.thread.Threads;

public class ThreadLocalHolderTest {

	public static void main(String[] args) {
		Thread[] threads = new Thread[6];

		for (int i = 0; i < 6; i++) {
			threads[i] = new Thread(() -> {
				Random random = new Random();
				while (true) {
					if (random.nextBoolean()) { // is true, then read
						System.out.println("ThreadName:" + Thread.currentThread().getName() + ",read value:"
								+ ThreadLocalHolder.get().getValue());
						Threads.sleep(1000);
					} else { // update
						int value = new Random().nextInt();
						ThreadLocalHolder.get().setValue(value);
						System.out.println("ThreadName:" + Thread.currentThread().getName() + ",write value:" + value);
						Threads.sleep(1000);
					}
				}
			});

			threads[i].start();
		}
	}
}
