package com.sky.project.share.common.pool.redis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * Redis
 * 
 * @author zealot
 *
 */
public final class Redis {
	private final Jedis jedis;

	public Redis(Jedis jedis) {
		checkNotNull(jedis, "jedis must not be null");
		this.jedis = jedis;
	}

	public <T> T execute(RedisResultAction<T> action) {
		checkNotNull(action, "action must not be null");
		return action.action(jedis);
	}

	public <T> void execute(RedisNoResultAction action) {
		checkNotNull(action, "action must not be null");
		action.action(jedis);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> execute(PipelineResultAction<T> action) {
		checkNotNull(action, "action must not be null");

		Pipeline pipeline = jedis.pipelined();
		action.action(pipeline);
		return (List<T>) pipeline.syncAndReturnAll();
	}

	public void execute(PipelineNoResultAction action) {
		checkNotNull(action, "action must not be null");

		Pipeline pipeline = jedis.pipelined();
		action.action(pipeline);
		pipeline.sync();
	}

	/**
	 * RedisResultAction
	 * 
	 * @param <T>
	 */
	public static interface RedisResultAction<T> {
		T action(Jedis jedis);
	}

	/**
	 * RedisNoResultAction
	 */
	public static interface RedisNoResultAction {
		void action(Jedis jedis);
	}

	/**
	 * PipelineResultAction
	 * 
	 * @param <T>
	 */
	public static interface PipelineResultAction<T> {
		void action(Pipeline pipeline);
	}

	/**
	 * PipelineNoResultAction
	 */
	public static interface PipelineNoResultAction {
		void action(Pipeline pipeline);
	}

	public Jedis getJedis() {
		return jedis;
	}

}
