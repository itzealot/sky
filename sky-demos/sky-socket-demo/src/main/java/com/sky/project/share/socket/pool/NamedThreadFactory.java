package com.sky.project.share.socket.pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * NamedThreadFactory
 * 
 * 参考实现{@link java.util.concurrent.Executors.DefaultThreadFactory}
 * 
 * @author qian.lei
 */
public class NamedThreadFactory implements ThreadFactory {
	/** 线程池池编号 */
	private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

	/** 线程池线程编码 */
	private final AtomicInteger mThreadNum = new AtomicInteger(1);

	/** 线程名称前缀 */
	private final String prefix;

	/** 是否为守护线程 */
	private final boolean deamon;

	/** 线程所属组 */
	private final ThreadGroup group;

	/**
	 * 默认构造，等效于Jdk的DefaultThreadFactory实现
	 */
	public NamedThreadFactory() {
		this("pool-" + POOL_SEQ.getAndIncrement(), false);
	}

	/**
	 * 根据线程名称前缀创建非守护线程
	 * 
	 * @param prefix
	 */
	public NamedThreadFactory(String prefix) {
		this(prefix, false);
	}

	/**
	 * 根据线程名称前缀及是否为守护线程创建线程
	 * 
	 * @param prefix
	 *            线程名称前缀
	 * @param deamon
	 *            是否为守护线程
	 */
	public NamedThreadFactory(String prefix, boolean deamon) {
		this.prefix = prefix + "-thread-";
		this.deamon = deamon;
		SecurityManager s = System.getSecurityManager();
		group = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
	}

	@Override
	public Thread newThread(Runnable runnable) {
		String name = prefix + mThreadNum.getAndIncrement();
		Thread ret = new Thread(group, runnable, name, 0);
		ret.setDaemon(deamon);
		return ret;
	}

	public ThreadGroup getThreadGroup() {
		return group;
	}
}