package com.surfilter.mass.services.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.surfilter.mass.ImcaptureContext;
import com.surfilter.mass.services.AbstractDataProvider;
import com.surfilter.mass.services.DataProvider;

import kafka.consumer.Consumer;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * Kafka 数据生产者，针对一类数据(指定topicName与partitions)从Kafka拉取数据
 * 
 * @author zealot
 *
 */
public class KafkaProvider extends AbstractDataProvider {

	private ConsumerConnector connector;
	private ExecutorService threadPool;
	private String topicName;
	private int partitions;

	public KafkaProvider(ImcaptureContext ctx, String topicName, int partitions) {
		super(ctx);
		this.topicName = topicName;
		this.partitions = partitions;
		this.connector = Consumer.createJavaConsumerConnector(config);
	}

	@Override
	public void provideData() throws Exception {
		Map<String, Integer> topicsMap = new HashMap<String, Integer>();
		topicsMap.put(topicName, partitions);

		List<KafkaStream<byte[], byte[]>> partitionLists = connector.createMessageStreams(topicsMap).get(topicName);
		threadPool = Executors.newFixedThreadPool(partitions);

		for (KafkaStream<byte[], byte[]> partition : partitionLists) {
			threadPool.execute(new MessageRunner(partition, new SimpleMsgExecutor(context.getBlockQueue())));
		}
	}

	@Override
	public void close() {
		try {
			threadPool.shutdownNow();
		} catch (Exception e) {
			LOG.error("close thread pool error.", e);
		} finally {
			connector.shutdown();
		}
	}

	@Override
	public void register(DataProvider dataProvider) throws Exception {
	}

}
