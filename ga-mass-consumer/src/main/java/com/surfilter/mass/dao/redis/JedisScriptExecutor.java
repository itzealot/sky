/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.surfilter.mass.dao.redis;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import com.surfilter.mass.dao.redis.JedisTemplate.JedisAction;
import com.surfilter.mass.dao.redis.pool.JedisPool;

/**
 * 装载并执行Lua Script，
 * 
 * 如果服务器上因为集群多台服务器或重启等原因没有装载script，会自动重新装载后重试。
 * 
 * 因为涉及到的key未知，暂时不支持Sharding。
 */
public class JedisScriptExecutor {
	private static Logger logger = LoggerFactory.getLogger(JedisScriptExecutor.class);

	private JedisTemplate jedisTemplate;

	private String script;
	private String sha1;

	public JedisScriptExecutor(JedisPool jedisPool) {
		this.jedisTemplate = new JedisTemplate(jedisPool);
	}

	public JedisScriptExecutor(JedisTemplate jedisTemplate) {
		this.jedisTemplate = jedisTemplate;
	}

	/**
	 * 装载Lua Script。
	 * 如果Script出错，抛出JedisDataException。
	 */
	public void load(final String scriptContent) throws JedisDataException {
		sha1 = jedisTemplate.execute(new JedisTemplate.JedisAction<String>() {
			@Override
			public String action(Jedis jedis) {
				return jedis.scriptLoad(scriptContent);
			}
		});
		script = scriptContent;

		logger.debug("Script \"{}\" had been loaded as {}", scriptContent, sha1);
	}


	/**
	 * 执行Lua Script, 如果Redis服务器上还没装载Script则自动装载并重试。
	 * keys与args不允许为null.
	 */
	public Object execute(final String[] keys, final String[] args) throws IllegalArgumentException {
		Validate.notNull(keys, "keys can't be null.");
		Validate.notNull(args, "args can't be null.");
		return execute(Arrays.asList(keys), Arrays.asList(args));
	}

	/**
	 * 执行Lua Script, 如果Redis服务器上还没装载Script则自动装载并重试。
	 * keys与args不允许为null.
	 */
	public Object execute(final List<String> keys, final List<String> args) throws IllegalArgumentException {
		Validate.notNull(keys, "keys can't be null.");
		Validate.notNull(args, "args can't be null.");

		return jedisTemplate.execute(new JedisAction<Object>() {
			@Override
			public Object action(Jedis jedis) {
				try {
					return jedis.evalsha(sha1, keys, args);
				} catch (JedisDataException e) {
					logger.warn(
							"Script {} is not loaded in server yet or the script is wrong, try to reload and run it again.",
							script, e);
					return jedis.eval(script, keys, args);
				}
			}
		});
	}
}