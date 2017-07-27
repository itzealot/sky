package com.sky.project.share.jdk.syn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.sky.project.share.common.thread.Threads;

/**
 * 使用 Lock 实现线程间交替运行
 * 
 * @author zealot
 */
public class WorkerManagerByLock {

	private static final Lock lock = new ReentrantLock();
	private static final List<Worker> workers = new ArrayList<>();
	private static final List<Condition> conditions = new ArrayList<>();

	/**
	 * Worker communication by lock
	 */
	static class Worker implements Runnable {
		// 运行状态切换，用于指定运行的线程编号
		private static int state = 0;
		private String msg;

		public Worker(String msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			Random random = new Random();
			int counts = random.nextInt(5) + 5;

			while (true) {
				try {
					lock.lock();

					if (workers.indexOf(this) == state) { // 运行自己时则执行任务
						Task.print(msg, counts); // 执行任务
						Threads.sleep(counts * 100);

						conditions.get(state).signal(); // 唤醒下一个等待的线程
						state = (state + 1) % workers.size(); // 运行状态循环切换
					} else {
						try {
							conditions.get(state).await(); // 自己进入等待
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} finally {
					lock.unlock();
				}
			}
		}
	}

	public static void add(Worker worker) {
		try {
			lock.lock();
			workers.add(worker);
			conditions.add(lock.newCondition());
		} finally {
			lock.unlock();
		}
	}
}