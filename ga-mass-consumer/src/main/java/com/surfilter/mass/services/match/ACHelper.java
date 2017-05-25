package com.surfilter.mass.services.match;

import com.surfilter.mass.MatchType;
import com.surfilter.mass.dao.KeyPerDao;
import com.surfilter.mass.entity.MatchInfo;
import com.surfilter.mass.services.match.algthm.AhoCorasick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

/**
 * @author hapuer
 *
 */
public class ACHelper {

	private static Logger LOG = LoggerFactory.getLogger(ACHelper.class);
	private static AhoCorasick<MatchInfo> acinfo = null;
	private static final Object LOCK = new Object();
	private static volatile boolean isInited = false; // 是否已初始化好matchInfo标记
	private static Timer timer = null;

	private ACHelper() {
	}

	public static ACHelper getInstance(Integer interval) {
		if (timer == null) {
			synchronized (LOCK) {
				if (timer == null) {
					initTimer(interval);
				}
			}
		}

		return AcHelperNest.acHelper;
	}

	private static void initTimer(Integer interval) {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				isInited = false; // 标记重新重新初始化
				LOG.debug("imcapture reload focus_mac info.");
			}
		}, interval * 60 * 1000, interval * 60 * 1000);// 设定指定的时间time,此处为2000毫秒
	}

	/**
	 * 根据布控策略时间范围内构建查询对象，将关键字 keyword 保存到查询器 AhoCorasick 中，并返回相应的匹配对象 MatchInfo
	 * 
	 * @param keyPerDao
	 * @return
	 */
	public AhoCorasick<MatchInfo> getAC(KeyPerDao keyPerDao) {
		if (!isInited) {
			synchronized (LOCK) {
				if (!isInited) {
					acinfo = new AhoCorasick<MatchInfo>();
					keyPerDao.addAllKeyPer(acinfo);
					acinfo.prepare();
					isInited = true;
				}
			}
		}

		return acinfo;
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void initAcinfo(List<String> pers) {
		acinfo = new AhoCorasick<MatchInfo>();
		long start = System.currentTimeMillis();
		int counts = 0;

		for (String per : pers) {
			String[] splits = per.split("\002");
			if (splits != null && splits.length == 19) {
				counts++;
				String[] strs = splits[2].split(":");
				Integer matchType = Integer.valueOf(strs[0]);

				MatchInfo matchInfo = new MatchInfo(Long.valueOf(splits[1]), Long.valueOf(splits[0]), strs[0], strs[1],
						strs[2], splits[3], splits[4], splits[5], splits[6], Integer.valueOf(splits[7]), splits[8],
						splits[9], splits[10], splits[11], splits[12], splits[13], splits[14], splits[15],
						"1".equals(splits[16]), "1".equals(splits[17]),splits[18]);

				if (MatchType.MAC.getCode() == matchType) {
					acinfo.add((strs[1] + MatchType.MAC.getSimCode()).getBytes(), matchInfo);
				} else if (MatchType.PROTOCOL.getCode() == matchType) {
					acinfo.add((strs[1] + MatchType.PROTOCOL.getSimCode()).getBytes(), matchInfo);
				} else if (MatchType.PHONE.getCode() == matchType) {
					acinfo.add((strs[1] + MatchType.PHONE.getSimCode()).getBytes(), matchInfo);
				} else if (MatchType.IMEI.getCode() == matchType) {
					acinfo.add((strs[1] + MatchType.IMEI.getSimCode()).getBytes(), matchInfo);
				} else if (MatchType.IMSI.getCode() == matchType) {
					acinfo.add((strs[1] + MatchType.IMSI.getSimCode()).getBytes(), matchInfo);
				} else if (MatchType.CERT.getCode() == matchType) {
					acinfo.add((strs[1] + strs[2] + MatchType.CERT.getSimCode()).getBytes(), matchInfo);
				} else if (MatchType.ACCOUNT.getCode() == matchType) {
					// match_type:match_value:match_child_type=>账号类数据解决后缀匹配的 BUG
					acinfo.add((MatchType.ACCOUNT.getSimCode() + strs[1] + strs[2]).getBytes(), matchInfo);
				}
			} else {
				LOG.error("ac init fialed, filed lenth is error, len:{}, per:{}", splits.length, per);
			}
		}

		acinfo.prepare();
		long spends = System.currentTimeMillis() - start;
		LOG.info("Ac inited successfully! total size: {}, spends: {}ms", counts, spends);
	}

	static class AcHelperNest {
		private static ACHelper acHelper = new ACHelper();
	}

	/**
	 * 获取包含ZDR的所有布控信息，异步加载
	 */
	static class ZdInitTask implements Callable<List<String>> {
		private KeyPerDao keyPerDao;

		public ZdInitTask(KeyPerDao keyPerDao) {
			this.keyPerDao = keyPerDao;
		}

		@Override
		public List<String> call() throws Exception {
			return this.keyPerDao.getAllKeyPer();
		}
	}

}
