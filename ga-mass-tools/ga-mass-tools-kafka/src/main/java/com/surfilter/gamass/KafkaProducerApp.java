package com.surfilter.gamass;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.surfilter.gamass.conf.Constant;
import com.surfilter.gamass.conf.MassConfiguration;
import com.surfilter.gamass.producer.KafkaProducer;
import com.surfilter.gamass.util.ParseRelationUtil;
import com.surfilter.mass.tools.util.Threads;

public class KafkaProducerApp {

	public static void main(String[] args) throws Exception {
		MassConfiguration conf = new MassConfiguration();

		String brokerUrl = conf.get(Constant.KAFKA_BROKER_LIST).replace(";", ",");
		String topic = conf.get(Constant.KAFKA_TOPIC);
		String src = conf.get(Constant.READ_SRC_FILE);
		int poolSize = conf.getInt(Constant.KAFKA_PRODUCE_THREADS, 10);
		int counts = conf.getInt(Constant.READ_SLEEP_COUNTS, 60000);
		int sleep = conf.getInt(Constant.READ_SLEEP_MILLS, 1000);

		System.out.println("brokerUrl:" + brokerUrl);
		System.out.println("topic:" + topic);
		System.out.println("src file:" + src);
		System.out.println("poolSize:" + poolSize);
		System.out.println("counts:" + counts);
		System.out.println("sleep:" + sleep);

		BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		File file = new File(src);

		KafkaProducer producer = null;
		try {
			ParseRelationUtil.read(queue, file, sleep, counts);
			producer = new KafkaProducer(queue, poolSize, brokerUrl, topic);

			producer.produce();
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			if (!queue.isEmpty()) {
				Threads.sleep(1000);
			} else {
				if (producer != null)
					producer.shutdown();
				break;
			}
		}
	}

}
