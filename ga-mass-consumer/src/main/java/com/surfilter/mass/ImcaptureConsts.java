package com.surfilter.mass;

/**
 * 报警程序常量类
 * 
 * @author zealot
 * 
 */
public class ImcaptureConsts {

	public static final String MAC_COMPANY_FILTER_REDIS_KEY = "mac_no_company_id";

	/** kafka 配置信息 */
	public static final String KAFKA_ZK_URL = "kafka.zk.url";
	public static final String KAFKA_GROUP_ID = "kafka.group.id";
	public static final String KAFKA_TOPIC_NAME = "kafka.topic";
	public static final String KAFKA_TOPIC_PARTITION = "kafka.topic.partition";

	/** 数据库配置信息 */
	public static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
	public static final String JDBC_URL = "jdbc.url";
	public static final String JDBC_USER_NAME = "jdbc.username";
	public static final String JDBC_PASS_WORD = "jdbc.password";

	/** 匹配线程数量 */
	public static final String MATCH_THREAD_SIZE = "match.thread.size";

	/** 匹配间隔 **/
	public static final String MATCH_INIT_INTERVAL = "match.init.interval";

	/** redis 排重配置 */
	public static final String MATCH_DUPLICATE_REMOVE = "match.duplicate.remove";

	/** 日志时间过滤配置 */
	public static final String ALARM_TIME_RANGE = "alarm.info.days.between";
	public static final String ALARM_MAX_INTERVAL_SECONDS = "alarm.max.interval.seconds";

	public static final String REDIS_URL = "redis.url";
	public static final String MAIL_TITLE = "布控邮件报警";

	/** 获取解析器字段长度的key */
	public static final String WL_PARSER_KEY = "match.parse.wl.length";
	public static final String FJ_PARSER_KEY = "match.parse.fj.length";
	public static final String XW_PARSER_KEY = "match.parse.xw.length";
	public static final String SJ_PARSER_KEY = "match.parse.sj.length";
	public static final String TZ_PARSER_KEY = "match.parse.tz.length";

	/** 场所信息缓存分钟 */
	public static final String SERVICE_INFO_RELOAD_MINUTES = "service.info.reload.minutes";

	/** zd cluster result settings */
	/** 分析多长时间(分钟)内的告警信息,默认值60 */
	public static final String ALARM_INFO_QUERY_INTERVAL = "alarm_info_query_interval";
	/** 出现时间间隔为多少秒认为是伴随出现,默认出现时间间隔5分钟即300s */
	public static final String CLUSTER_ALARM_RESULT_INTERVAL = "cluster_alarm_result_interval";
	/** 报警团伙人数 */
	public static final String CLUSTER_ALARM_MIN_COUNTS = "cluster_alarm_min_counts";

	/** 聚集初级的逗留时长最小范围区间，单位为秒 */
	public static final String CLUSTER_ALARM_STAY_LIMIT_SECONDS = "cluster.alarm.stay.limit.seconds";

	/** ZD_PERSON_ALARM_INFO 删除超过指定天数的数据 */
	public static final String ZD_PERSON_ALARM_INFO_CLEAN_DAYS = "zd.person.alarm.info.clean.days";

	/** redis key */
	public static final String REDIS_DUMP_KEY = "imcap_dump_";
	public static final String REDIS_COUNT_KEY = "imcap_count_";

	public static final String SPLITER = "\\|";

	/**
	 * 数据接入类型，对应日志的后缀编码
	 */
	public enum DataType {
		WL("001"), FJ("002"), IM("003"), XW("004"), SJ("005"), TZ("014");

		private String typeCode;

		private DataType(String typeCode) {
			this.typeCode = typeCode;
		}

		public String getTypeCode() {
			return typeCode;
		}
	}
}
