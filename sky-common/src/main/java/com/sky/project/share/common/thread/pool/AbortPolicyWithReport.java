package com.sky.project.share.common.thread.pool;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abort Policy. Log warn info when abort.
 * 
 * 线程池拒绝策略，拒绝时打印警告信息
 * 
 * 其他策略实现参考:<br>
 * 线程池未关闭，执行执行{@link java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy}<br>
 * 丢弃策略，什么都不处理{@link java.util.concurrent.ThreadPoolExecutor.DiscardPolicy}<br>
 * 丢弃最老的任务，处理最新任务{@link java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy}<br>
 * 
 * @author ding.lid
 */
public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// 线程名称
	private final String threadName;

	public AbortPolicyWithReport(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
		String msg = String.format(
				"Thread pool is EXHAUSTED!"
						+ " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
						+ " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)!",
				threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(),
				e.getLargestPoolSize(), e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(),
				e.isTerminating());
		logger.warn(msg);
		throw new RejectedExecutionException(msg);
	}

}