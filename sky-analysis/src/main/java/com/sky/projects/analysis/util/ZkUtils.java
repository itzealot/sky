package com.sky.projects.analysis.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZkUtils {
	private static Logger LOG = LoggerFactory.getLogger(ZkUtils.class);
	public static final String CONSUMERS_PATH = "/consumers";
	public static final String BROKER_IDS_PATH = "/brokers/ids";
	public static final String BROKER_TOPICS_PATH = "/brokers/topics";

	public static String getBrokerName(ZkClient client, String id) {
		Map<String, String> brokers = new HashMap<>();
		brokers = new HashMap<>();
		List<String> brokerIds = getChildrenParentMayNotExist(client, "/brokers/ids");
		for (String bid : brokerIds) {
			String data = (String) client.readData("/brokers/ids/" + bid);
			LOG.info("Broker " + bid + " " + data);
			brokers.put(bid, data.split(":", 2)[1]);
		}
		return (String) brokers.get(id);
	}

	public static List<String> getBrokerPartitions(ZkClient client, String topic) {
		List<String> partitions = new ArrayList<>();
		List<String> partitionsTopics = getChildrenParentMayNotExist(client,
				"/brokers/topics/" + topic + "/" + "partitions");
		for (String partition : partitionsTopics) {
			String parts = (String) client
					.readData("/brokers/topics/" + topic + "/" + "partitions" + "/" + partition + "/" + "state");
			HashMap<Object, Object> obj = null;
			ObjectMapper om = new ObjectMapper();
			ObjectReader reader = om.reader(HashMap.class);
			reader.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			try {
				obj = reader.readValue(parts);
			} catch (Exception e) {
			}
			partitions.add(obj.get("leader") + "-" + partition);
		}
		return partitions;
	}

	private static String getOffsetsPath(String group, String topic, String partition) {
		return "/consumers/" + group + "/offsets/" + topic + "/" + partition;
	}

	public static long getLastConsumedOffset(ZkClient client, String group, String topic, String partition) {
		String znode = getOffsetsPath(group, topic, partition);
		LOG.info("getOffsetsPath:" + znode);
		String offset = (String) client.readData(znode, true);
		if (offset == null) {
			return 0L;
		}
		return Long.valueOf(offset).longValue();
	}

	public static void commitLastConsumedOffset(ZkClient client, String group, String topic, String partition,
			long offset) {
		String path = getOffsetsPath(group, topic, partition);

		LOG.info("OFFSET COMMIT " + path + " = " + offset);
		if (!client.exists(path)) {
			client.createPersistent(path, true);
		}
		client.writeData(path, Long.valueOf(offset));
	}

	private static List<String> getChildrenParentMayNotExist(ZkClient client, String path) {
		try {
			return client.getChildren(path);
		} catch (ZkNoNodeException e) {
			return new ArrayList<>();
		}
	}

	public static void close(ZkClient client) throws IOException {
		if (client != null) {
			client.close();
		}
	}

	public static class StringSerializer implements ZkSerializer {
		public Object deserialize(byte[] data) throws ZkMarshallingError {
			if (data == null) {
				return null;
			}
			return new String(data);
		}

		public byte[] serialize(Object data) throws ZkMarshallingError {
			return data.toString().getBytes();
		}
	}
}