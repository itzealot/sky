package com.sky.project.share.api.kafka.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public final class ZkUtils {

	static final String CONSUMERS_PATH = "/consumers";
	// path=/consumers/{groupName}/offsets/{topicName}/{partitionId}
	static final String OFFSET_PATH = CONSUMERS_PATH + "/%s/offsets/%s/%s";

	static final String BROKERS_PATH = "/brokers/ids";
	// path=/brokers/ids/{brokerId}
	static final String BROKER_PATH = BROKERS_PATH + "/%s";

	static final String TOPICS_PATH = "/brokers/topics";

	// path=/brokers/topics/{topicName}/partitions
	static final String PARTITIONS_PATH = TOPICS_PATH + "/%s/partitions";

	// path=/brokers/topics/{topicName}/partitions/{partitionId}
	static final String PARTITION_PATH = PARTITIONS_PATH + "/%s";

	// path=/brokers/topics/{topicName}/partitions/{partitionId}/state
	static final String PARTITION_STATE_PATH = PARTITION_PATH + "/state";

	public static String brokerInfo(ZkClient client, String brokerId) {
		return brokers(client).get(brokerId);
	}

	/**
	 * 
	 * @param client
	 * @return key=brokerId,value=broker
	 */
	public static Map<String, String> brokers(ZkClient client) {
		Map<String, String> brokers = new HashMap<String, String>();

		// 获取Zookeeper上的所有brokerId
		List<String> brokerIds = children(client, BROKERS_PATH);

		for (String brokerId : brokerIds) {
			String data = client.readData(String.format(BROKER_PATH, brokerId));

			String[] split = data.split(":", 2);
			// System.out.println("brokerId:" + brokerId);
			// System.out.println("data=>" + data);
			// System.out.println("0=>" + split[0]);
			// System.out.println("1=>" + split[1]);
			brokers.put(brokerId, split[1]);
		}

		return brokers;
	}

	/**
	 * @param client
	 * @param topic
	 * @return [value=brokerId-partition]
	 */
	public static List<String> brokerPartitions(ZkClient client, String topic) {
		List<String> partitions = new ArrayList<String>();

		List<String> partitionNames = children(client, String.format(PARTITIONS_PATH, topic));

		for (String partition : partitionNames) {
			String parts = client.readData(String.format(PARTITION_STATE_PATH, topic, partition));

			HashMap<Object, Object> obj = null;

			ObjectMapper om = new ObjectMapper();
			ObjectReader reader = om.reader(HashMap.class);
			reader.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

			try {
				obj = reader.readValue(parts);
				// value=brokerId-partition
				partitions.add(obj.get("leader") + "-" + partition);
			} catch (Exception e) {
			}
		}

		return partitions;
	}

	public static long getOffset(ZkClient client, String group, String topic, String partition) {
		String offset = client.readData(offsetPath(group, topic, partition), true);
		return offset == null ? -1L : Long.valueOf(offset);
	}

	public static long getOffset(ZkClient client, String group, String topic, int partition) {
		String offset = client.readData(offsetPath(group, topic, String.valueOf(partition)), true);
		return offset == null ? -1L : Long.valueOf(offset);
	}

	private static String offsetPath(String group, String topic, String partition) {
		return String.format(OFFSET_PATH, group, topic, partition);
	}

	public static void updateOffset(ZkClient client, String group, String topic, String partition, long offset) {
		String path = offsetPath(group, topic, partition);

		if (!client.exists(path)) { // not exist the create
			client.createPersistent(path, true);
		}

		// update
		client.writeData(path, offset);
	}

	private static List<String> children(ZkClient client, String path) {
		try {
			return client.getChildren(path);
		} catch (Exception e) {
			return new ArrayList<String>();
		}
	}

	public static void close(ZkClient client) {
		if (client != null) {
			try {
				client.close();
			} catch (Exception e) {
			}
		}
	}

	public static class StringSerializer implements ZkSerializer {
		public StringSerializer() {
			super();
		}

		@Override
		public Object deserialize(byte[] data) throws ZkMarshallingError {
			return data == null ? null : new String(data);
		}

		@Override
		public byte[] serialize(Object data) throws ZkMarshallingError {
			return data == null ? null : data.toString().getBytes();
		}
	}

	private ZkUtils() {
	}
}
