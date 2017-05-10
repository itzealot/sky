package com.sky.projects.analysis.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

public class FileToKafka {
	public static long x = 0L;

	public static void main(String[] args) throws IOException {
		Properties prop = new Properties();
		prop.put("zookeeper.connect", "172.16.18.16:2181");
		prop.put("serializer.class", StringEncoder.class.getName());
		prop.put("metadata.broker.list", "172.16.18.16:9092,172.16.18.17:9092");

		Producer<String, String> producer = new Producer<>(new ProducerConfig(prop));

		sendFile(producer, "/root/WiFiDataSet");

		System.out.println("count==" + x);
	}

	public static void sendFile(Producer<String, String> producer, String dir) throws IOException {
		File f = new File(dir);
		for (File child : f.listFiles()) {
			System.out.println("new child:" + child.getName());
			if (!child.getName().endsWith("gz")) {
				if (child.isDirectory()) {
					sendFile(producer, dir + "/" + child.getName());
				} else {
					System.out.println("file==" + child.getName());
					FileInputStream fis = new FileInputStream(child);
					byte[] b = new byte[fis.available()];
					fis.read(b);
					fis.close();
					JSONArray arr = JSONArray.parseArray(new String(b, "utf-8"));
					for (int i = 0; i < arr.size(); i++) {
						JSONObject obj = arr.getJSONObject(i);

						KeyedMessage<String, String> message = new KeyedMessage<>("dgdata1026", JSON.toJSONString(obj));
						producer.send(message);
						x += 1L;
					}
				}
			}
		}
	}
}
