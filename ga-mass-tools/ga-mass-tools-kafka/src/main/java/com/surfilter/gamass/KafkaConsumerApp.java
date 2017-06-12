package com.surfilter.gamass;

import com.surfilter.gamass.conf.Constant;
import com.surfilter.gamass.conf.MassConfiguration;
import com.surfilter.gamass.consumer.KafkaConsumer;

public class KafkaConsumerApp {

	public static void main(String[] arg) {
		MassConfiguration conf = new MassConfiguration();

		String zookeeper = conf.get(Constant.ZOOKEEPER_LIST).replace(";", ",");
		String groupId = conf.get(Constant.KAFKA_CONSUMER_GROUP);
		String topic = conf.get(Constant.KAFKA_TOPIC);
		int partitions = conf.getInt(Constant.KAFKA_CONSUMER_TOPIC_PARTITION_NUM);

		System.out.println("zookeeper:" + zookeeper);
		System.out.println("groupId:" + groupId);
		System.out.println("topic:" + topic);
		System.out.println("partitions:" + partitions);

		KafkaConsumer demo = new KafkaConsumer(zookeeper, groupId, topic);

		try {
			demo.consumer(partitions);
		} catch (Throwable e) {
			demo.shutdown();
		}
	}
}
