package com.surfilter.mass.services.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.ImcaptureContext;
import com.surfilter.mass.dao.redis.JedisHelper;
import com.surfilter.mass.dao.redis.JedisTemplate;
import com.surfilter.mass.dao.redis.JedisTemplate.PipelineActionNoResult;
import com.surfilter.mass.dao.redis.JedisTemplate.PipelineAction;
import com.surfilter.mass.entity.AlarmInfo;
import com.surfilter.mass.services.DuplicateRemover;
import com.surfilter.mass.utils.ImcaptureUtil;

import redis.clients.jedis.Pipeline;

/**
 * redis 排重报警信息实现类
 * 
 * @author hapuer
 *
 */
public class RedisDupRemover implements DuplicateRemover {

	private static Logger LOG = LoggerFactory.getLogger(RedisDupRemover.class);

	private JedisTemplate jedisTemplate;
	private Integer isDupRemover = 1;
	private static final String SPLITER = "|";

	public RedisDupRemover(ImcaptureContext ctx) {
		this.jedisTemplate = JedisHelper.getInstance().getTemplate();
		isDupRemover = ctx.getInt(ImcaptureConsts.MATCH_DUPLICATE_REMOVE, 1);
	}

	@Override
	public List<AlarmInfo> removeDup(List<AlarmInfo> datas) {
		String currentDay = DateTime.now().toString("yyyyMMdd");

		String dumpKey = ImcaptureConsts.REDIS_DUMP_KEY + currentDay;
		String countKey = ImcaptureConsts.REDIS_COUNT_KEY + currentDay;
		List<AlarmInfo> realAlarms = Lists.newArrayList();
		Set<String> alarmMaps = Sets.newHashSet();

		try {
			int size = datas.size();

			List<String> combineKeys = new ArrayList<>(size);
			StringBuffer buffer = new StringBuffer(128);

			for (int i = 0; i < size; i++) { // 根据行健初始化
				AlarmInfo alarmInf = datas.get(i);
				String combineKey = null;

				// 如果是ZDR，则按人(zdPersonId + serviceCode)排重，而不是身份排重
				if (!ImcaptureUtil.isEmpty(alarmInf.getZdPersonId())) {
					combineKey = buffer.append(alarmInf.getZdPersonId()).append(SPLITER)
							.append(alarmInf.getServiceCode()).toString();
				} else {
					combineKey = buffer.append(alarmInf.getMatchType()).append(SPLITER).append(alarmInf.getMatchValue())
							.append(SPLITER).append(alarmInf.getServiceCode()).append(SPLITER)
							.append(alarmInf.getStoreId()).toString();
				}

				buffer.setLength(0);

				combineKeys.add(combineKey);
			}

			List<Object> dumpOldValues = hgets(dumpKey, combineKeys);
			List<Object> countOldValues = hgets(countKey, combineKeys);

			List<String> dumpValues = new ArrayList<>(size);
			List<String> countValues = new ArrayList<>(size);

			for (int i = 0; i < size; i++) {
				AlarmInfo alarmInf = datas.get(i);
				String combineKey = combineKeys.get(i);
				String value = (String) dumpOldValues.get(i);
				String countAndStartTime = (String) countOldValues.get(i);

				if (this.isDupRemover == 1) {// redis排重
					// 相同类型的匹配信息只报警一次
					if (!alarmMaps.contains(combineKey)) {
						alarmMaps.add(combineKey);
						// 判断是否需要按照天进行数据排重,天排重在redis里面进行, 指定是否按照天进行排重
						int dayAlarmCount = alarmInf.getDayAlarmCount();

						// 当天第一次进入的时候，进行报警，因为 redis 中没有相应当天的值，即第一次进入
						if (StringUtils.isBlank(value)) {
							dumpValues.add(String.valueOf(DateTime.now().getMillis()));

							countValues.add("1|" + alarmInf.getStartTime());

							// zd_person 报警次数不限制
							if (dayAlarmCount >= 1 || !ImcaptureUtil.isEmpty(alarmInf.getZdPersonId())) {
								LOG.debug("alarm info first alram, uniqKey={}, limit counts={}", combineKey,
										dayAlarmCount);
								realAlarms.add(alarmInf);
							}
						} else {
							long lastAlarmTimeMillis = Long.valueOf(value);
							long alarmCount = 0;
							long lastStartTime = 0;

							if (!ImcaptureUtil.isEmpty(countAndStartTime)) {
								String[] splits = countAndStartTime.split("\\|");

								if (splits.length == 2) {
									alarmCount = Long.parseLong(splits[0]);
									lastStartTime = Long.parseLong(splits[1]);
								}
							}

							// 时间范围内
							boolean flag = (lastAlarmTimeMillis != 0 && DateTime.now()
									.minusSeconds(alarmInf.getAlarmInterval()).getMillis() > lastAlarmTimeMillis);
							if (((alarmInf.getEndTime() != null && alarmInf.getEndTime() != 0) || flag)
									&& alarmInf.getStartTime() > lastStartTime) {
								alarmCount++;

								dumpValues.add(String.valueOf(DateTime.now().getMillis()));
								countValues.add(buffer.append(alarmCount).append(SPLITER)
										.append(alarmInf.getStartTime()).toString());
								buffer.setLength(0);

								// 次数范围内或zd_person报警次数不限制
								if (dayAlarmCount >= alarmCount || !ImcaptureUtil.isEmpty(alarmInf.getZdPersonId())) {
									LOG.debug("alarm info, uniqKey={}, times={}, limit counts={}, alarmInterval:{}",
											combineKey, alarmCount, dayAlarmCount, alarmInf.getAlarmInterval());
									realAlarms.add(alarmInf);
								}
							}
						}
					}
				} else {
					realAlarms.add(alarmInf); // 不按照天进行排重，则出现一次即报警
				}
			}

			dumpOldValues.clear();
			dumpOldValues = null;
			countOldValues.clear();
			countOldValues = null;

			if (this.isDupRemover == 1) {
				updateKeys(dumpKey, combineKeys, dumpValues);
				dumpValues.clear();

				updateKeys(countKey, combineKeys, countValues);
				countValues.clear();
			}

			combineKeys.clear();
			combineKeys = null;

			countValues = null;
			dumpValues = null;
		} catch (Exception e) {
			LOG.error("redis connect error.", e);
		}

		return realAlarms;
	}

	/**
	 * 根据redisKey与HashKey获取value值
	 * 
	 * @param redisKey
	 * @param hashKeys
	 */
	private List<Object> hgets(String redisKey, List<String> hashKeys) {
		return jedisTemplate.execute(new PipelineAction() {
			@Override
			public List<Object> action(Pipeline pipeline) {
				int size = hashKeys.size();
				for (int i = 0; i < size; i++) {
					pipeline.hget(redisKey, hashKeys.get(i));
				}
				return null;
			}
		});
	}

	/**
	 * 批量更新redis相关的排重key-value
	 * 
	 * @param redisKeys
	 * @param hashKeys
	 * @param values
	 */
	private void updateKeys(String redisKey, List<String> hashKeys, List<String> values) {
		try {
			jedisTemplate.execute(new PipelineActionNoResult() {
				@Override
				public void action(Pipeline pl) {
					for (int i = 0, len = hashKeys.size(); i < len; i++) {
						pl.hset(redisKey, hashKeys.get(i), values.get(i));
					}
				}
			});
		} catch (Exception e) {
		}
	}

}