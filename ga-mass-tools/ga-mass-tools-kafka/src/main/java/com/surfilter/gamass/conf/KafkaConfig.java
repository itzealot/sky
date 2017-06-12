package com.surfilter.gamass.conf;

import java.util.Properties;

public final class KafkaConfig {

	public static Properties initProducer(String brokerUrl) {
		Properties props = new Properties();

		props.setProperty("metadata.broker.list", brokerUrl);
		// props.setProperty("producer.type", "async");
		props.setProperty("request.required.acks", "-1");
		// .setProperty("compression.codec", "snappy");
		props.setProperty("batch.num.messages", "200");

		return props;
	}

	private KafkaConfig() {
	}
}
