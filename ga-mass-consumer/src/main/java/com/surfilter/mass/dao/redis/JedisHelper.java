/**
 * 
 */
package com.surfilter.mass.dao.redis;

import org.apache.commons.lang.StringUtils;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.dao.redis.pool.JedisPool;
import com.surfilter.mass.dao.redis.pool.JedisPoolBuilder;

/**
 * @author hapuer
 *
 */
public class JedisHelper {

	private static JedisPool pool;

	private JedisHelper() {
		MassConfiguration conf = new MassConfiguration();
		String redisUrl = conf.get(ImcaptureConsts.REDIS_URL);
		if (StringUtils.isBlank(redisUrl))
			throw new RuntimeException("Redis Url can't be null,pls check.");
		String[] redisStrs = redisUrl.split("\\|");
		pool = new JedisPoolBuilder()
				.setMasterName(JedisPoolBuilder.DIRECT_POOL_PREFIX + redisStrs[0] + ":" + redisStrs[1]).setPoolSize(50)
				.setTimeout(30000).buildPool();
	}

	public static JedisHelper getInstance() {
		return JedisHelperNest.jedisHelper;
	}

	public synchronized JedisTemplate getTemplate() {
		return new JedisTemplate(pool);
	}

	static class JedisHelperNest {
		private static JedisHelper jedisHelper = new JedisHelper();
	}
}
