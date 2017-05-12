package com.surfilter.mass.services;

import java.util.Properties;

import kafka.consumer.ConsumerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.ImcaptureContext;

/**
 * 抽象的数据生产者
 * 
 * @author hapuer
 *
 */
public abstract class AbstractDataProvider implements DataProvider {

	protected Logger LOG = LoggerFactory.getLogger(this.getClass());

	protected ImcaptureContext context;
	protected ConsumerConfig config;

	public AbstractDataProvider(ImcaptureContext context) {
		this.context = context;
		this.config = new ConsumerConfig(initKafkaProps());
	}

	private Properties initKafkaProps() {
		Properties properties = new Properties();

		properties.setProperty("zookeeper.connect", context.gets(ImcaptureConsts.KAFKA_ZK_URL));
		properties.setProperty("zookeeper.connection.timeout.ms", "100000");
		properties.setProperty("auto.offset.reset", "largest");
		properties.setProperty("group.id", context.getString(ImcaptureConsts.KAFKA_GROUP_ID));

		return properties;
	}

}
