package com.sky.project.share.streaming.util;

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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public final class ZkUtils {

	private static Logger LOG = LoggerFactory.getLogger(ZkUtils.class);

	private static final String CONSUMERS_PATH = "/consumers";
	private static final String BROKER_IDS_PATH = "/brokers/ids";
	private static final String BROKER_TOPICS_PATH = "/brokers/topics";

	public static String getBrokerName(ZkClient client, String id) {
		Map<String, String> brokers = new HashMap<String, String>();
		brokers = new HashMap<String, String>();
		List<String> brokerIds = getChildrenParentMayNotExist(client, BROKER_IDS_PATH);

		for (String bid : brokerIds) {
			String data = client.readData(BROKER_IDS_PATH + "/" + bid);
			LOG.debug("Broker " + bid + " " + data);
			brokers.put(bid, data.split(":", 2)[1]);
		}
		return brokers.get(id);
	}

	public static List<String> getBrokerPartitions(ZkClient client, String topic) {
		List<String> partitions = new ArrayList<String>();
		List<String> partitionsTopics = getChildrenParentMayNotExist(client,
				BROKER_TOPICS_PATH + "/" + topic + "/" + "partitions");
		for (String partition : partitionsTopics) {
			String parts = client
					.readData(BROKER_TOPICS_PATH + "/" + topic + "/" + "partitions" + "/" + partition + "/" + "state");
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
		return CONSUMERS_PATH + "/" + group + "/offsets/" + topic + "/" + partition;
	}

	public static long getLastConsumedOffset(ZkClient client, String group, String topic, String partition) {
		String znode = getOffsetsPath(group, topic, partition);

		LOG.debug("getLastConsumedOffset getOffsetsPath: " + znode);

		String offset = client.readData(znode, true);
		if (offset == null) {
			return 0L;
		}
		return Long.valueOf(offset);
	}

	public static void commitLastConsumedOffset(ZkClient client, String group, String topic, String partition,
			long offset) {
		String path = getOffsetsPath(group, topic, partition);

		LOG.debug("commitLastConsumedOffset OFFSET COMMIT " + path + " = " + offset);

		if (!client.exists(path)) {
			client.createPersistent(path, true);
		}
		client.writeData(path, offset);
	}

	private static List<String> getChildrenParentMayNotExist(ZkClient client, String path) {
		try {
			List<String> children = client.getChildren(path);
			return children;
		} catch (ZkNoNodeException e) {
			return new ArrayList<String>();
		}
	}

	public static void close(ZkClient client) throws java.io.IOException {
		if (client != null) {
			client.close();
		}
	}

	public static class StringSerializer implements ZkSerializer {
		public StringSerializer() {
		}

		public Object deserialize(byte[] data) throws ZkMarshallingError {
			if (data == null)
				return null;
			return new String(data);
		}

		public byte[] serialize(Object data) throws ZkMarshallingError {
			return data.toString().getBytes();
		}
	}

	private ZkUtils() {
	}
}
