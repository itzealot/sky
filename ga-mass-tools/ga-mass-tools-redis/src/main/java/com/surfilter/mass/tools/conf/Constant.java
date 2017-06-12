package com.surfilter.mass.tools.conf;

public final class Constant {

	public static final int TIMEOUT = 2000;
	public static final int BUFFERSIZE = 10 * 1024 * 1024;

	public static final String KAFKA_BROKER_LIST = "kafka.broker.list";
	public static final String KAFKA_TOPIC = "kafka.topic";

	public static final String ZOOKEEPER_LIST = "zookeeper.list";
	public static final String KAFKA_CONSUMER_GROUP = "kafka.consumer.group";
	public static final String KAFKA_CONSUMER_TOPIC_PARTITION_NUM = "kafka.consumer.topic.partition.num";
	public static final String KAFKA_PRODUCE_THREADS = "kafka.producer.threads";

	public static final int PRODUCER_BATCH_SIZE = 5000;
	public static final int KAFKA_EVENTS_SIZE = 100;

	public static final String KAFKA_MSG_SPLITER = "\002";
	public static final String KAFKA_FILED_SPLITER = "|";

	public static final String DEFAULT_FILE_WRITE_SPLITER = "\t";

	public static final String READ_SRC_FILE = "read.src.file";
	public static final String WRITE_BATCH_SIZE = "write.batch.size";
	public static final String READ_SLEEP_COUNTS = "read.sleep.counts";
	public static final String READ_SLEEP_MILLS = "read.sleep.millis";
	public static final String PARSE_THREADS = "parse.threads";
	public static final String WRITE_DST_DIR = "write.dst.dir";

	/** mac 身份存储变更 */
	public static final String MAC_MODIFY_PREFIX_LENGTH = "mac.modify.prefix.length";

	public static final String WRITE_HBASE_CERTIFICATION_TABLE = "write.hbase.certification.table";
	public static final String WRITE_HBASE_RELATION_TABLE = "write.hbase.relation.table";
	public static final String WRITE_HBASE_PARAMS = "write.hbase.params";
	public static final String WRITE_REDIS = "write.redis";
	public static final String WRITE_REDIS_IP = "redis.ip";
	public static final String WRITE_REDIS_PORT = "redis.port";
	public static final int REDIS_BATCH_SIZE = 5000;
	public static final int RELATION_BATCH_SIZE = 2000;

	/** conf for relation */

	public static final String MULL = "MULL";

	private Constant() {
	}
}
