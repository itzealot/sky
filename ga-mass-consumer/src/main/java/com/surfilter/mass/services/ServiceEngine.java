package com.surfilter.mass.services;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.ImcaptureContext;
import com.surfilter.mass.dao.KeyPerDao;
import com.surfilter.mass.dao.db.KeyPerDaoImpl;
import com.surfilter.mass.dao.redis.JedisHelper;
import com.surfilter.mass.dao.redis.JedisTemplate;
import com.surfilter.mass.entity.ServiceInfo;
import com.surfilter.mass.services.match.ACHelper;
import com.surfilter.mass.services.match.ServiceInfoHelper;
import com.surfilter.mass.services.support.AlarmInfoAnalysis;
import com.surfilter.mass.services.support.CmDataConsumer;
import com.surfilter.mass.services.support.DataProviderHandler;
import com.surfilter.mass.services.support.KafkaProvider;
import com.surfilter.mass.utils.ImcaptureUtil;

/**
 * 服务注册及启动类
 * 
 * @author hapuer
 *
 */
public class ServiceEngine {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceEngine.class);

	private MassConfiguration conf;
	private ImcaptureContext context;
	private DataProvider dataProvider;
	private DataConsumer dataConsumer;
	private final JedisTemplate jedisTemplate;
	private Timer zdTimer;
	private AlarmInfoAnalysis analysis;
	private ServiceInfoHelper serviceInfoHelper;
	private Timer zdCleanTimer;

	private ServiceEngine() {
		this.conf = new MassConfiguration();
		this.context = new ImcaptureContext(this.conf);

		/** 初始化解析器 */
		MsgParserHolder.getInstance(this.context.getConf()) // 获取单例
				.addWlParser() // 初始化wl数据解析器
				.addFjParser() // 初始化fj数据解析器
				.addSjParser() // 初始化sj数据解析器
				.addXwParer() // 初始化xw数据解析器
				.addTzParser(); // 初始化tz数据解析器

		/** 初始化redis单例 */
		this.jedisTemplate = JedisHelper.getInstance().getTemplate();

		/** 初始化厂商数据 */
		context.setMacCompanyKeys(jedisTemplate.hgetAll(ImcaptureConsts.MAC_COMPANY_FILTER_REDIS_KEY).keySet());

		/** 初始化场所信息 */
		this.serviceInfoHelper = ServiceInfoHelper.getInstance(this.conf);
		this.serviceInfoHelper.getServiceInfo();

		/** 初始化 ACHelper */
		ACHelper.getInstance(context.getInt(ImcaptureConsts.MATCH_INIT_INTERVAL, 5))
				.getAC(new KeyPerDaoImpl(this.context.getJdbcConfig()));

		/** 初始化生产者持有类 */
		this.dataProvider = DataProviderHandler.getInstance(this.context);

		/** 获取过滤信息系统 */
		String queryInterval = this.serviceInfoHelper.getMacFilterMap().get(ImcaptureConsts.ALARM_INFO_QUERY_INTERVAL);
		int interval = ImcaptureUtil.getValue(queryInterval, 20);

		this.analysis = new AlarmInfoAnalysis(new KeyPerDaoImpl(this.context.getJdbcConfig()));
		LOG.info("analysis alarm info logs params, interval:{} min", interval);

		/** 清除前天的redis排重key */
		String dayStr = DateTime.now().minusDays(1).toString("yyyyMMdd");
		this.jedisTemplate.del(ImcaptureConsts.REDIS_DUMP_KEY + dayStr, ImcaptureConsts.REDIS_COUNT_KEY + dayStr);

		/** 初始化 zdTimer */
		this.zdTimer = new Timer();
		this.zdTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				/** 重新加载场所信息及wl数据过滤配置信息 */
				Map<String, ServiceInfo> map = serviceInfoHelper.getServiceInfo();
				Map<String, String> macFilterMap = serviceInfoHelper.getMacFilterMap();

				String clusterInterval = macFilterMap.get(ImcaptureConsts.CLUSTER_ALARM_RESULT_INTERVAL);
				long seconds = ImcaptureUtil.getValue(clusterInterval, 300L);

				String clusterCounts = macFilterMap.get(ImcaptureConsts.CLUSTER_ALARM_MIN_COUNTS);
				int counts = ImcaptureUtil.getValue(clusterCounts, 3);

				String queryInterval = macFilterMap.get(ImcaptureConsts.ALARM_INFO_QUERY_INTERVAL);
				int interval = ImcaptureUtil.getValue(queryInterval, 20);

				LOG.info("start analysis alarm info logs, cluster counts:{}, cluster seconds:{}s, intserval:{} min.",
						counts, seconds, interval);

				long start = System.currentTimeMillis();
				int stayLimitSeconds = conf.getInt(ImcaptureConsts.CLUSTER_ALARM_STAY_LIMIT_SECONDS, 3600);
				analysis.alalysis(map, interval, seconds, counts, stayLimitSeconds);
				long spend = (System.currentTimeMillis() - start) / 1000L;
				LOG.info("finish analysis alarm info logs, spends:{}s", spend);
			}
		}, 3 * 60 * 1000L, interval * 60 * 1000);

		this.zdCleanTimer = new Timer();
		this.zdCleanTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				KeyPerDao dao = new KeyPerDaoImpl(context.getJdbcConfig());
				int cleanDays = conf.getInt(ImcaptureConsts.ZD_PERSON_ALARM_INFO_CLEAN_DAYS, 5);
				if (cleanDays < 1) { // 非法参数，则默认删除5天内的数据
					cleanDays = 5;
				}
				dao.cleanZdPersonAlarmInfo(cleanDays);
			}
		}, 1 * 60 * 1000L, 24 * 60 * 60 * 1000);
	}

	/**
	 * 生产者初始化及注册
	 * 
	 * @return
	 */
	public ServiceEngine provide() {
		String topics = this.conf.get(ImcaptureConsts.KAFKA_TOPIC_NAME);
		String partitions = this.conf.get(ImcaptureConsts.KAFKA_TOPIC_PARTITION);

		if (StringUtils.isNotBlank(topics) && StringUtils.isNotBlank(partitions)) {
			String[] topicArrays = topics.split(ImcaptureConsts.SPLITER);
			String[] partitionArrays = partitions.split(ImcaptureConsts.SPLITER);

			if (topicArrays.length != partitionArrays.length) {
				LOG.error("error settings for {} or {}, topic counts not equal partition counts",
						ImcaptureConsts.KAFKA_TOPIC_NAME, ImcaptureConsts.KAFKA_TOPIC_PARTITION);
				throw new IllegalArgumentException("register service fial.");
			}

			for (int i = 0, len = topicArrays.length; i < len; i++) {
				try {
					String topicName = topicArrays[i];
					String partition = partitionArrays[i];
					// 根据topic与partition数量新建针对一类数据的 KafkaProvider
					dataProvider.register(new KafkaProvider(context, topicName, ImcaptureUtil.getValue(partition, 5)));
				} catch (Exception e) {
					LOG.error("register service fail.", e);
				}
			}
		} else {
			LOG.error("error settings for {} or {}, can't be empty", ImcaptureConsts.KAFKA_TOPIC_NAME,
					ImcaptureConsts.KAFKA_TOPIC_PARTITION);
			throw new IllegalArgumentException("register service fial.");
		}

		return this;
	}

	/**
	 * 初始化消费者
	 * 
	 * @return
	 */
	public ServiceEngine consume() {
		this.dataConsumer = new CmDataConsumer(this.context);
		return this;
	}

	/**
	 * 生产者开始生产，消费者开始消费
	 */
	public void startEngine() {
		if (this.dataProvider != null) {
			try {
				this.dataProvider.provideData();
			} catch (Exception e) {
				LOG.error("Start kafka info error.", e);
			}
		}
		if (this.dataConsumer != null) {
			this.dataConsumer.startConsume();
		}
	}

	public static ServiceEngine getInstance() {
		return ServiceEngineNest.serviceEngine;
	}

	static class ServiceEngineNest {
		private static ServiceEngine serviceEngine = new ServiceEngine();
	}
}
