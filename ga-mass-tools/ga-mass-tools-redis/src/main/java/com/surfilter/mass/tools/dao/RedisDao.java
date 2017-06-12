package com.surfilter.mass.tools.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.io.FileInputStream;
import java.util.*;

/**
 * redis CRUD 操作.
 *
 * @author liujikuan
 */
public class RedisDao {

    private static Logger LOG = LoggerFactory.getLogger(RedisDao.class);

    private static final Object LOCK = new Object();


    private static RedisDao instance = null;
    private static Jedis jedis;

    static {
        Properties prop = new Properties();
        String propertyPath = System.getProperty("user.dir") + "/conf/config.properties";
        try {
            prop.load(new FileInputStream(propertyPath));
            String ip = prop.getProperty("redis.ip", "localhost");
            String port = prop.getProperty("redis.port", "6379");
            jedis = new Jedis(ip, Integer.parseInt(port));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * @param keys hashKey的集合
     * @return
     */
    public static void delKeys(String key, String[] keys) {
        Pipeline pipeline = jedis.pipelined();
        Response<Long> resp = pipeline.hdel(key, keys);
        pipeline.sync();
    }


    /**
     * 获取JedisUtil实例
     *
     * @return
     */
    public static RedisDao getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new RedisDao();
                }
            }
        }

        return instance;
    }




}
