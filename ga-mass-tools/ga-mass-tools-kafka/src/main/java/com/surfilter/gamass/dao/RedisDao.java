package com.surfilter.gamass.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.gamass.conf.JRedisPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * redis CRUD 操作.
 * 
 * @author liuchen
 */
public class RedisDao {

	private static Logger LOG = LoggerFactory.getLogger(RedisDao.class);

	// redis 超时时间
	private static final int REDIS_CONNECT_LATENCY = 120000;
	public static final int THREE_DAY_SECONDS = 3 * 24 * 60 * 60;
	private static final Object LOCK = new Object();
	private static volatile RedisDao instance = null;

	private JedisPool jedisPool;
	private String redisIp;
	private String redisPassw;
	private int redisPort;

	public static RedisDao getInstance(String serversInfo) {
		if (instance == null) {
			synchronized (LOCK) {
				if (instance == null) {
					instance = new RedisDao(serversInfo, 0);
				}
			}
		}

		return instance;
	}

	private RedisDao(String serversInfo, int database) {
		LOG.info("serversInfo: {}; database: {}", serversInfo, database);

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

		LOG.debug("redisIp: {}, redisPort: {}", redisIp, redisPort);
	}

	/**
	 * 批量增加 redisKey中hashKey的值，并根据seconds是否为null设置redisKey过期时间
	 * 
	 * @param redisKey
	 * @param hashKeys
	 * @param hashValues
	 * @param seconds
	 *            不为null时设置redisKey过期时间
	 */
	public void hincrBys(final String redisKey, final List<String> hashKeys, final List<Long> hashValues,
			Integer seconds) {
		Jedis jedis = jedisPool.getResource();

		try {
			Pipeline pipeline = jedis.pipelined();
			if (seconds != null)
				pipeline.expire(redisKey, seconds);

			for (int i = 0, size = hashKeys.size(); i < size; i++) {
				pipeline.hincrBy(redisKey, hashKeys.get(i), hashValues.get(i));
			}

			pipeline.sync();
		} catch (Exception e) {
			LOG.error("execute hincrByPipeline method error", e);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 批量增加 redisKey中hashKey的值，并根据seconds是否为null设置redisKey过期时间
	 * 
	 * @param redisKey
	 * @param hashKeys
	 * @param seconds
	 *            不为null时设置redisKey过期时间
	 */
	public void hincrBys(final String redisKey, final List<String> hashKeys, Integer seconds) {
		Jedis jedis = jedisPool.getResource();

		try {
			Pipeline pipeline = jedis.pipelined();
			if (seconds != null)
				pipeline.expire(redisKey, seconds);

			for (int i = 0, size = hashKeys.size(); i < size; i++) {
				pipeline.hincrBy(redisKey, hashKeys.get(i), 1l);
			}

			pipeline.sync();
		} catch (Exception e) {
			LOG.error("execute hincrByPipeline method error", e);
		} finally {
			jedis.close();
		}
	}

	/**
	 * 批量判断某个redisKey下的hashKey是否存在
	 * 
	 * @param redisKey
	 * @param hashKeys
	 * @return
	 */
	public List<Boolean> hexistsGroup(List<List<String>> redisKeyLists, List<String> hashKeys) {
		Jedis jedis = jedisPool.getResource();
		List<Boolean> results = new ArrayList<>();

		try {
			Pipeline pipeline = jedis.pipelined();
			List<List<Response<Boolean>>> responses = new ArrayList<>();
			for (int i = 0, size = hashKeys.size(); i < size; i++) {
				List<String> redisKeys = redisKeyLists.get(i);
				List<Response<Boolean>> res = new ArrayList<>();
				for (int j = 0; j < redisKeys.size(); j++) {
					res.add(pipeline.hexists(redisKeys.get(j), hashKeys.get(i)));
				}
				responses.add(res);
			}

			pipeline.sync();

			for (int i = 0, size = responses.size(); i < size; i++) {
				boolean flag = false;
				List<Response<Boolean>> res = responses.get(i);
				for (int j = 0; j < res.size(); j++) {
					flag = flag | res.get(j).get();
				}
				results.add(flag);
			}
		} catch (Exception e) {
			LOG.error("execute hexistsGroup method error", e);
		} finally {
			jedis.close();
		}
		return results;
	}

	/**
	 * 批量判断某个redisKey下的 hashKey是否存在
	 * 
	 * @param redisKey
	 * @param hashKeys
	 * @return
	 */
	public List<Boolean> hexists(List<String> redisKeys, List<String> hashKeys) {
		Jedis jedis = jedisPool.getResource();
		List<Boolean> results = new ArrayList<>();

		try {
			Pipeline pipeline = jedis.pipelined();

			List<Response<Boolean>> responses = new ArrayList<>();
			for (int i = 0, size = hashKeys.size(); i < size; i++) {
				responses.add(pipeline.hexists(redisKeys.get(i), hashKeys.get(i)));
			}

			pipeline.sync();

			for (int i = 0, size = responses.size(); i < size; i++) {
				results.add(responses.get(i).get());
			}
		} catch (Exception e) {
			LOG.error("execute hexists method error", e);
		} finally {
			jedis.close();
		}
		return results;
	}

	public List<Boolean> hexists(String redisKey, List<String> hashKeys) {
		Jedis jedis = jedisPool.getResource();
		List<Boolean> results = new ArrayList<>();

		try {
			Pipeline pipeline = jedis.pipelined();

			List<Response<Boolean>> responses = new ArrayList<>();
			for (int i = 0, size = hashKeys.size(); i < size; i++) {
				responses.add(pipeline.hexists(redisKey, hashKeys.get(i)));
			}

			pipeline.sync();

			for (int i = 0, size = responses.size(); i < size; i++) {
				results.add(responses.get(i).get());
			}
		} catch (Exception e) {
			LOG.error("execute hexists method error", e);
		} finally {
			jedis.close();
		}
		return results;
	}

	public List<Boolean> exists(List<String> redisKeys) {
		Jedis jedis = jedisPool.getResource();
		List<Boolean> results = new ArrayList<>();

		try {
			Pipeline pipeline = jedis.pipelined();

			List<Response<Boolean>> responses = new ArrayList<>();
			for (int i = 0, size = redisKeys.size(); i < size; i++) {
				responses.add(pipeline.exists(redisKeys.get(i)));
			}

			pipeline.sync();

			for (int i = 0, size = responses.size(); i < size; i++) {
				results.add(responses.get(i).get());
			}
		} catch (Exception e) {
			LOG.error("execute hexists method error", e);
		} finally {
			jedis.close();
		}
		return results;
	}

	public List<String> hgets(String redisKey, List<String> hashKeys) {
		Jedis jedis = jedisPool.getResource();
		List<String> results = new ArrayList<>();

		try {
			Pipeline pipeline = jedis.pipelined();

			List<Response<String>> responses = new ArrayList<>();
			for (int i = 0, size = hashKeys.size(); i < size; i++) {
				responses.add(pipeline.hget(redisKey, hashKeys.get(i)));
			}

			pipeline.sync();

			for (int i = 0, size = responses.size(); i < size; i++) {
				results.add(responses.get(i).get());
			}
		} catch (Exception e) {
			LOG.error("execute hgets method error", e);
		} finally {
			jedis.close();
		}

		return results;
	}

	/**
	 * 批量根据redisKey及对应的hashKey获取 hashValue
	 * 
	 * @param redisKeys
	 * @param hashKeys
	 * @return
	 */
	public List<String> hgets(List<String> redisKeys, List<String> hashKeys) {
		Jedis jedis = jedisPool.getResource();
		List<String> results = new ArrayList<>();

		try {
			Pipeline pipeline = jedis.pipelined();

			List<Response<String>> responses = new ArrayList<>();
			for (int i = 0, size = hashKeys.size(); i < size; i++) {
				responses.add(pipeline.hget(redisKeys.get(i), hashKeys.get(i)));
			}

			pipeline.sync();

			for (int i = 0, size = responses.size(); i < size; i++) {
				results.add(responses.get(i).get());
			}
		} catch (Exception e) {
			LOG.error("execute hgets method error", e);
		} finally {
			jedis.close();
		}

		return results;
	}

	/**
	 * 批量根据redisKey及对应的hashKey更新hashValue
	 * 
	 * @param redisKeys
	 * @param hashKeys
	 * @return
	 */
	public void hsets(List<String> redisKeys, List<String> hashKeys, List<String> hashValues) {
		Jedis jedis = jedisPool.getResource();

		try {
			Pipeline pipeline = jedis.pipelined();

			for (int i = 0, size = hashKeys.size(); i < size; i++) {
				pipeline.hset(redisKeys.get(i), hashKeys.get(i), hashValues.get(i));
			}

			pipeline.sync();
		} catch (Exception e) {
			LOG.error("execute hsets method error", e);
		} finally {
			jedis.close();
		}
	}
}