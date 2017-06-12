package com.surfilter.gamass.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.surfilter.gamass.service.ConsumerMsgTask;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * {@linkplain https://cwiki.apache.org/confluence/display/KAFKA/Consumer
 * +Group+Example}
 *
 * @author zealot
 */
public class KafkaConsumer {

	private final ConsumerConnector consumer;
	private final String topic;
	private ExecutorService executor;

	public KafkaConsumer(String zookeeper, String groupId, String topic) {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
		this.topic = topic;
	}

	public void consumer(int partitions) {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();

		// 设计topic和stream的关系，即K为topic，V为stream的个数N
		topicCountMap.put(topic, new Integer(partitions));

		// 获取numThreads个stream
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

		// 对每个 partition 开启一个线程消费
		executor = Executors.newFixedThreadPool(partitions);

		int threadNumber = 0;

		for (final KafkaStream<byte[], byte[]> stream : streams) {// 开启N个消费组线程消费这N个stream
			executor.submit(new ConsumerMsgTask(stream, threadNumber));
			threadNumber++;
		}
	}

	private static ConsumerConfig createConsumerConfig(String zookeeper, String groupId) {
		Properties props = new Properties();

		props.put("zookeeper.connect", zookeeper);
		props.put("group.id", groupId);
		props.put("auto.offset.reset", "smallest");
		props.put("zookeeper.connection.timeout.ms", "3000");

//		props.setProperty("auto.commit.interval.ms", "1000");// 提交消费偏移的间隔
//		props.setProperty("fetch.message.max.bytes", "41943040");// 每次从TOPIC获取数据最大字节
//		props.setProperty("replica.fetch.max.bytes", "41943040");// 主从数据复制最大字节
//		props.setProperty("replica.fetch.min.bytes", "1");// fetch的最小数据尺寸,如果leader中尚未同步的数据不足此值,将会阻塞,直到满足条件
//		props.setProperty("zookeeper.session.timeout.ms", "30000");// 会话超时时间
//		props.setProperty("zookeeper.sync.time.ms", "2000");// 主从同步时间间隔
//		props.setProperty("controlled.shutdown.max.retries", "10");// 控制器关闭的尝试次数
//		props.setProperty("controlled.shutdown.retry.backoff.ms", "2000");// 每次关闭尝试的时间间隔
//		props.setProperty("fetch.message.min.bytes", "1");// server发送到消费端的最小数据，若是不满足这个数值则会等待直到满足指定大小。默认为1表示立即接收
//		props.setProperty("fetch.min.bytes", "1");// server发送到消费端的最小数据，若是不满足这个数值则会等待直到满足指定大小。默认为1表示立即接收
//		props.setProperty("fetch.wait.max.ms", "100");// 若是不满足fetch.min.bytes时，等待消费端请求的最长等待时间
//		// consumer.timeout.ms这个参数非常重要，如果没有设置且没有最新数据会导致程序一直阻塞（其实就是卡死，再也出不来），即使有新数据也无法继续
//		props.setProperty("consumer.timeout.ms", "10000");// 如果指定时间内没有新消息可用于消费，就抛出异常，默认-1表示不受限
//		// socket请求的超时时间。实际的超时时间为max.fetch.wait + socket.timeout.ms
//		props.setProperty("socket.timeout.ms", "30000");
//		props.setProperty("socket.receive.buffer.bytes", "41943040");// socket的接受缓冲区

		return new ConsumerConfig(props);
	}

	public void shutdown() {
		if (consumer != null)
			consumer.shutdown();

		if (executor != null)
			executor.shutdown();
	}

}