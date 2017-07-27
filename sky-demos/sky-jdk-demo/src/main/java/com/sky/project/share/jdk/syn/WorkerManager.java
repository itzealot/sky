package com.sky.project.share.jdk.syn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sky.project.share.common.thread.Threads;

/**
 * 使用 synchronized 实现线程间交替运行
 * 
 * @author zealot
 */
public class WorkerManager {
	private static List<Worker> workers = new ArrayList<>();

	/**
	 * Worker communication by synchronized
	 */
	static class Worker implements Runnable {
		// 运行状态切换，用于指定运行的线程
		private static int state = 0;
		private String msg;

		public Worker(String msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			Random random = new Random();
			int counts = random.nextInt(10) + 10;

			while (true) {
				synchronized (workers) { // 上锁
					if (workers.indexOf(this) == state) { // 运行自己时则执行任务
						Task.print(msg, counts); // 执行任务
						Threads.sleep(counts * 100);

						state = (state + 1) % workers.size(); // 运行状态循环切换

						// 唤醒等待的所有线程，必须是 notifyAll，而不是 notify
						workers.notifyAll();
					} else {
						try {
							workers.wait(); // 自己进入等待
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public static void add(Worker worker) {
		synchronized (workers) {
			workers.add(worker);
		}
	}
}