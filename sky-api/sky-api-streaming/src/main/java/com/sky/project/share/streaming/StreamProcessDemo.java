package com.sky.project.share.streaming;

import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sky.project.share.streaming.support.StreamHelper;

import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;

/**
 * 数据流式处理类
 * 
 * @author zealot
 */
@SuppressWarnings("serial")
public class StreamProcessDemo extends KryoSerializer {

	static Logger LOG = LoggerFactory.getLogger(StreamProcessDemo.class);

	static final String STREAM_TYPE = "test_zealot";

	private String zkConn;
	private String groupId;

	public StreamProcessDemo(SparkConf conf, Map<String, Broadcast<String>> broadcastMap) {
		super(conf);
		this.zkConn = broadcastMap.get("ga.zkUrl").getValue();
		this.groupId = broadcastMap.get("ga.topic.group").getValue();
	}

	public void deal(JavaStreamingContext jssc, Map<String, String> kafkaParams, String topic,
			Map<Integer, Long> partionAndOffset) {
		JavaDStream<String> sourceStream = KafkaUtils.createDirectStream(jssc, String.class, String.class,
				StringDecoder.class, StringDecoder.class, String.class, kafkaParams,
				StreamHelper.topicOffsetToMap(zkConn, groupId, topic, partionAndOffset),
				new Function<MessageAndMetadata<String, String>, String>() {
					@Override
					public String call(MessageAndMetadata<String, String> msgAndMd) throws Exception {
						return msgAndMd.message();
					}
				});

		StreamHelper.updateOffsetToZk(sourceStream, STREAM_TYPE, zkConn, groupId);

		sourceStream.count();
	}

}
