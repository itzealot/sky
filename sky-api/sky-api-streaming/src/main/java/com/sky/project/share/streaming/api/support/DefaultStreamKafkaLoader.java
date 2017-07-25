package com.sky.project.share.streaming.api.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.project.share.streaming.api.MessageConverter;
import com.sky.project.share.streaming.api.StreamKafkaContext;
import com.sky.project.share.streaming.api.StreamKakfaLoader;
import com.sky.project.share.streaming.conf.SysContans;
import com.sky.project.share.streaming.support.StreamHelper;

import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;

/**
 * DefaultStreamKafkaLoader
 * 
 * @author zealot
 * @param <T>
 */
@SuppressWarnings("serial")
public class DefaultStreamKafkaLoader<T> extends KryoSerializer implements StreamKakfaLoader<T> {
	protected final Logger logger = LoggerFactory.getLogger(DefaultStreamKafkaLoader.class);

	private Map<String, String> certProtocolMap;
	private Map<String, String> macHasCompanyMap;
	private int reduceByKeyNum;
	private String mysqlConn;
	private String brokers;
	private String groupId;
	private String zkConn;
	private Map<String, Object> params;

	public DefaultStreamKafkaLoader(SparkConf conf, Map<String, Broadcast<Map<String, String>>> broadcastRedisMap,
			Map<String, Broadcast<String>> broadcastMap) {
		super(conf);
		initFromBroadcast(broadcastRedisMap, broadcastMap);
	}

	/**
	 * 广播信息初始化
	 * 
	 * @param broadcastRedisMap
	 * @param broadcastMap
	 */
	private void initFromBroadcast(Map<String, Broadcast<Map<String, String>>> broadcastRedisMap,
			Map<String, Broadcast<String>> broadcastMap) {
		/** parameter init to map */
		params.put("reduceByKeyNum", reduceByKeyNum);
		params.put("mysqlConn", mysqlConn);
		params.put("brokers", brokers);
		params.put("groupId", groupId);
		params.put("zkConn", zkConn);
	}

	@Override
	public StreamKafkaContext<T> kafkaLoad(JavaStreamingContext jssc, Map<String, String> kafkaParams, String topic,
			Map<Integer, Long> partionAndOffset, MessageConverter<T> converter) {
		JavaDStream<String> kafkaStream = KafkaUtils.createDirectStream(jssc, String.class, String.class,
				StringDecoder.class, StringDecoder.class, String.class, kafkaParams,
				StreamHelper.topicOffsetToMap(zkConn, groupId, topic, partionAndOffset),
				new Function<MessageAndMetadata<String, String>, String>() {
					@Override
					public String call(MessageAndMetadata<String, String> msgAndMd) throws Exception {
						return msgAndMd.message();
					}
				});

		StreamHelper.updateOffsetToZk(kafkaStream, converter.getStreamingType(), zkConn, groupId);

		// flatMap
		final JavaDStream<T> stream = kafkaStream.flatMap(new FlatMapFunction<String, T>() {
			@Override
			public Iterable<T> call(String line) {
				String[] messages = line.split(SysContans.KAFKA_MESSAGES_SPLITER);
				int len = messages.length;
				List<T> lines = new ArrayList<T>(len);

				for (int i = 0; i < len; i++) {// 遍历所有的记录
					T message = converter.convert(messages[i]);

					// validate message
					if (converter.validate(message)) {
						lines.add(converter.update(message));
					}
				}

				return lines;
			}
		});

		// 每执行一次则返回新的实例
		return new StreamKafkaContext<T>() {
			@Override
			public JavaDStream<T> getStream() {
				return stream;
			}

			@Override
			public Map<String, Object> getParams() {
				return params;
			}

			@Override
			public Map<String, String> getCertProtocolMap() {
				return certProtocolMap;
			}

			@Override
			public Map<String, String> getMacHasCompanyMap() {
				return macHasCompanyMap;
			}
		};
	}

}
