package com.surfilter.mass.tools.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.pool.ConnectionException;
import com.surfilter.commons.pool.kafka.KafkaConnectionPool;
import com.surfilter.mass.tools.util.PoolConfigUtil;

import kafka.javaapi.producer.Producer;

/**
 * Kafka producer pool
 * 
 * @author zealot
 *
 */
public final class KafkaProducerPool {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerPool.class);

	private static volatile KafkaProducerPool instance = null;

	private KafkaConnectionPool pool;

	private KafkaProducerPool(String brokers) {
		initPool(brokers);
	}

	public static KafkaProducerPool getInstance(String brokers) {
		KafkaProducerPool inst = instance;
		if (inst == null) {
			synchronized (KafkaProducerPool.class) {
				inst = instance;
				if (inst == null) {
					inst = new KafkaProducerPool(brokers);
					instance = inst;
				}
			}
		}
		return inst;
	}

	private void initPool(String brokers) {
		this.pool = new KafkaConnectionPool(PoolConfigUtil.initPoolConfig(),
				PoolConfigUtil.initKafkaProducerProperties(brokers));
	}

	public Producer<byte[], byte[]> getConnection() {
		try {
			return pool.getConnection();
		} catch (ConnectionException e) {
			LOG.error("get kafka connection fail.", e);
			try {
				Thread.sleep(1000);
				return pool.getConnection();
			} catch (Exception e2) {
				LOG.error("get kafka connection fail again.", e);
				return null;
			}
		}
	}

	public void returnConnection(Producer<byte[], byte[]> producer) {
		pool.returnConnection(producer);
	}

}
