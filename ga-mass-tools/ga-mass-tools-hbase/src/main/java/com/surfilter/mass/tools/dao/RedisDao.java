package com.surfilter.mass.tools.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.surfilter.mass.tools.conf.JRedisPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * redis访问类
 * 
 * @author liuchen
 */
public class RedisDao {
	private static Logger LOG = Logger.getLogger(RedisDao.class);
	private static final int REDIS_CONNECT_LATENCY = 10000;

	private static RedisDao instance = null;

	private JedisPool jedisPool;
	private String redisIp;
	private String redisPassw;
	private int redisPort;
	private int redisKeyKeepDay = 1;

	private RedisDao(String serversInfo, int database) {
		System.out.println("serversInfo:" + serversInfo + "; database:" + database);
		String[] splits = serversInfo.split(JRedisPoolConfig.STR_DELIMIT);
		if (splits.length < 2) {
			LOG.error("serversInfo error: " + serversInfo);
		}
		redisIp = splits[0];
		try {
			redisPort = Integer.parseInt(splits[1]);
		} catch (NumberFormatException e) {
		}

		if (StringUtils.isBlank(redisIp) || redisPort == 0) {
			LOG.error("error parsing redis, serversInfo is " + serversInfo);
			return;
		}

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(JRedisPoolConfig.MAX_ACTIVE);
		config.setMaxIdle(JRedisPoolConfig.MAX_IDLE);
		// config.setMaxWait(JRedisPoolConfig.MAX_WAIT);
		config.setTestOnBorrow(JRedisPoolConfig.TEST_ON_BORROW);
		config.setTestOnReturn(JRedisPoolConfig.TEST_ON_RETURN);
		if (splits.length == 3) {
			redisPassw = splits[2];
			LOG.info("password is " + redisPassw);
			jedisPool = new JedisPool(config, redisIp, redisPort, REDIS_CONNECT_LATENCY, redisPassw, database);
		} else {
			LOG.info("no password");
			jedisPool = new JedisPool(config, redisIp, redisPort, REDIS_CONNECT_LATENCY, null, database);
		}
		LOG.info("redisIp:" + redisIp);
		LOG.info("redisPort:" + redisPort);
		LOG.info("redisPassw:" + redisPassw);
	}

	/**
	 * 获取JedisUtil实例
	 * 
	 * @return
	 */
	public static RedisDao getInstance(String serversInfo) {
		if (instance == null) {
			synchronized (RedisDao.class) {
				if (instance == null)
					instance = new RedisDao(serversInfo, 0);
			}
		}

		return instance;
	}

	public JedisPool getPool() {
		return jedisPool;
	}

	public int getRedisKeyKeepDay() {
		return redisKeyKeepDay;
	}

	public void setRedisKeyKeepDay(int redisKeyKeepDay) {
		this.redisKeyKeepDay = redisKeyKeepDay;
	}

	/**
	 * 批量获取hash value
	 * 
	 * @param redisKey
	 * @param hashKeyList
	 * @return
	 */
	public void hget(final String redisKey, final List<String> hashKeyList, List<String> results) {
		Jedis jedis = jedisPool.getResource();
		List<Response<String>> resList = new ArrayList<Response<String>>();

		try {
			Pipeline pipeline = jedis.pipelined();
			for (String hashKey : hashKeyList) {
				resList.add(pipeline.hget(redisKey, hashKey));
			}
			pipeline.sync();

			for (int i = 0, size = hashKeyList.size(); i < size; i++) {
				results.add(resList.get(i).get());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
	}
}