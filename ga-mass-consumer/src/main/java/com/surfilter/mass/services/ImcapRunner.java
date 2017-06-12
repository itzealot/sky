package com.surfilter.mass.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.surfilter.mass.ImcaptureContext;
import com.surfilter.mass.dao.KeyPerDao;
import com.surfilter.mass.dao.db.KeyPerDaoImpl;
import com.surfilter.mass.entity.AlarmInfo;
import com.surfilter.mass.services.match.AcDataMatcher;
import com.surfilter.mass.services.support.RedisDupRemover;
import com.surfilter.mass.utils.ImcaptureUtil;
import com.surfilter.mass.utils.Threads;

/**
 * 消息消费者
 * 
 * @author zealot
 *
 */
public class ImcapRunner implements Runnable {

	private static Logger LOG = LoggerFactory.getLogger(ImcapRunner.class);
	private BlockingQueue<String> queue;
	private DataMatcher dataMatcher;
	private KeyPerDao keyPerDao;
	private DuplicateRemover dpRemover;
	static final int CACHE_SIZE = 1000;

	public ImcapRunner(ImcaptureContext ctx) {
		this.queue = ctx.getBlockQueue();
		this.dpRemover = new RedisDupRemover(ctx);
		this.keyPerDao = new KeyPerDaoImpl(ctx.getJdbcConfig());
		this.dataMatcher = new AcDataMatcher(ctx, keyPerDao);
	}

	@Override
	public void run() {
		while (true) {
			List<String> cache = Lists.newArrayListWithCapacity(CACHE_SIZE);
			int size = queue.drainTo(cache, CACHE_SIZE);

			if (size > 0) {
				LOG.debug("Received msgs:{}", size);
				List<AlarmInfo> matchResults = dataMatcher.match(cache);

				if (matchResults.size() > 0 && keyPerDao != null) {
					Collection<AlarmInfo> alarmInfs = dpRemover.removeDup(matchResults);

					if (alarmInfs != null && !alarmInfs.isEmpty()) {
						Map<String, List<AlarmInfo>> alarmInfoMap = classifyAlarmInfo(alarmInfs);

						List<AlarmInfo> zdPersonAlarmInfos = alarmInfoMap.get("zdPersonAlarmInfo");
						if (CollectionUtils.isNotEmpty(zdPersonAlarmInfos)) {
							this.keyPerDao.saveZdPersonAlarmInfos(zdPersonAlarmInfos);
							zdPersonAlarmInfos.clear();
						}
						zdPersonAlarmInfos = null;

						List<AlarmInfo> focusAlarmInfos = alarmInfoMap.get("focusAlarmInfo");
						if (CollectionUtils.isNotEmpty(focusAlarmInfos)) {
							this.keyPerDao.saveFocusAlarmInfos(focusAlarmInfos);
							focusAlarmInfos.clear();
						}
						focusAlarmInfos = null;

						alarmInfoMap.clear();
						alarmInfoMap = null;

						LOG.info("Total match size:{}", alarmInfs.size());
					}

					alarmInfs.clear();
					alarmInfs = null;
				}

				matchResults.clear();
				matchResults = null;

				cache.clear();
			} else {
				Threads.sleep(100L);
			}

			cache = null;
		}
	}

	/**
	 * @param alarmInfs
	 * @return 将ZDR和布控人员分开，入到不同的数据库表
	 */
	public Map<String, List<AlarmInfo>> classifyAlarmInfo(Collection<AlarmInfo> alarmInfs) {
		HashMap<String, List<AlarmInfo>> alarmInfoMap = new HashMap<>(2);
		alarmInfoMap.put("zdPersonAlarmInfo", new ArrayList<AlarmInfo>());
		alarmInfoMap.put("focusAlarmInfo", new ArrayList<AlarmInfo>());

		for (AlarmInfo alarmInfo : alarmInfs) {
			if (!ImcaptureUtil.isEmpty(alarmInfo.getZdPersonId())) {
				alarmInfoMap.get("zdPersonAlarmInfo").add(alarmInfo);
			} else {
				alarmInfoMap.get("focusAlarmInfo").add(alarmInfo);
			}
		}
		return alarmInfoMap;
	}

}
