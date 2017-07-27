package com.sky.project.share.jdk.syn;

import com.sky.project.share.jdk.syn.WorkerManager.Worker;

public class ThreadManagerTest {

	public static void main(String[] args) {
		Worker worker1 = new Worker("Worker1");
		Worker worker2 = new Worker("Worker2");
		Worker worker3 = new Worker("Worker3");

		WorkerManager.add(worker1);
		WorkerManager.add(worker2);
		WorkerManager.add(worker3);

		new Thread(worker1).start();
		new Thread(worker2).start();
		new Thread(worker3).start();
	}
}
