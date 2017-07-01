package com.sky.project.share.common.pool.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis Pool {@link redis.clients.jedis.JedisPool}
 * 
 * @author zealot
 *
 */
public class RedisPool {
	/**
	 * 可用连接实例的最大数目，默认值为8；
	 * 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
	 */
	private static final int MAX_ACTIVE = 200;

	/**
	 * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	 */
	private static final int MAX_IDLE = 10;

	/**
	 * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。 如果超过等待时间，则直接抛出JedisConnectionException；
	 */
	private static final int MAX_WAIT = 600 * 1000;

	/**
	 * 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的 jedis 实例均是可用的；
	 */
	private static final boolean TEST_ON_BORROW = true;

	public static RedisPool instance = null;
	private final JedisPool pool;

	public static RedisPool getInstance(String host, int port) {
		if (instance == null) {
			synchronized (RedisPool.class) {
				if (instance == null) {
					instance = new RedisPool(host, port);
				}
			}
		}

		return instance;
	}

	private RedisPool(String host, int port) {
		JedisPoolConfig config = new JedisPoolConfig();

		config.setMaxTotal(MAX_ACTIVE);
		config.setMaxIdle(MAX_IDLE);
		config.setMaxWaitMillis(MAX_WAIT);
		config.setTestOnBorrow(TEST_ON_BORROW);

		pool = new JedisPool(config, host, port);
	}

	/**
	 * 获取 redis 连接
	 * 
	 * @return
	 */
	public Jedis getResource() {
		return pool == null ? null : pool.getResource();
	}

	/**
	 * 归还 redis 连接
	 * 
	 * @param resource
	 */
	public void returnResource(Jedis resource) {
		if (resource != null) {
			resource.close();
		}
	}
}
