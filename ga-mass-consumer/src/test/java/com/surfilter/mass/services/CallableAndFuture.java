package com.surfilter.mass.services;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.surfilter.mass.utils.Threads;

public class CallableAndFuture {

	public static void main(String[] args) {
		Callable<Integer> callable = new Callable<Integer>() {
			public Integer call() throws Exception {
				Threads.sleep(1000);
				return new Random().nextInt(100);
			}
		};

		FutureTask<Integer> future = new FutureTask<Integer>(callable);
		new Thread(future).start();

		try {
			System.out.println(future.isDone());
			if (future.isDone()) {
				System.out.println(future.get());
			}
			System.out.println("sleep");
			Thread.sleep(5000);// 可能做一些事情
			System.out.println(future.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}