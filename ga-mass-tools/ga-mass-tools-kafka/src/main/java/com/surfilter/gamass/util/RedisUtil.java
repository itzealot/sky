package com.surfilter.gamass.util;

import java.util.ArrayList;
import java.util.List;

import com.surfilter.gamass.conf.JRedisPoolConfig;
import com.surfilter.gamass.dao.RedisDao;
import com.surfilter.gamass.entity.Certification;
import com.surfilter.mass.tools.util.Filter;

import scala.collection.mutable.StringBuilder;

public final class RedisUtil {

	public static final String CP = "|";

	/**
	 * update certification for mac and other certification
	 * 
	 * @param hashKeys
	 *            id|idType
	 * @param values
	 *            firstStartTime|firstTerminalNum|source|createTime|sysSource
	 * @param serversInfo
	 */
	public static void updateCertificationInRedis(List<String> hashKeys, List<String> values, String serversInfo,
			boolean versionIsFxj) {
		int size = hashKeys.size();
		List<String> otherRedisKeys = new ArrayList<>(size / 2);
		List<String> otherVals = new ArrayList<>(size / 2);
		List<String> otherHashKeys = new ArrayList<>(size / 2);

		List<String> macs = new ArrayList<>(size);
		List<String> macVals = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			String hashKey = hashKeys.get(i);
			String[] vals = hashKey.split(JRedisPoolConfig.STR_DELIMIT);
			String redisKey = fetchRedisKey(vals[1]);

			if (redisKey == null) { // is mac
				macs.add(vals[0]);
				macVals.add(values.get(i));
			} else { // is imei,wx,qq,mobile or others
				if (JRedisPoolConfig.CERTIFICATION_OTHER_KEY.equals(redisKey)) {
					otherHashKeys.add(hashKey);
				} else {
					otherHashKeys.add(vals[0]);
				}
				otherRedisKeys.add(redisKey);
				otherVals.add(values.get(i));
			}
		}

		if (macs.size() > 0) { // update mac
			updateCertificationMac(macs, macVals, serversInfo, versionIsFxj);
			macs.clear();
			macVals.clear();
		}

		macs = null;
		macVals = null;

		if (otherRedisKeys.size() > 0) {
			updateCertificationOthers(otherRedisKeys, otherHashKeys, otherVals, serversInfo, versionIsFxj);
			otherRedisKeys.clear();
			otherHashKeys.clear();
			otherVals.clear();
		}

		otherRedisKeys = null;
		otherHashKeys = null;
		otherVals = null;
	}

	/**
	 * 
	 * @param redisKeys
	 * 
	 * @param hashKeys
	 *            id|idType
	 * @param values
	 *            firstStartTime|firstTerminalNum|source|createTime|sysSource
	 * @param serversInfo
	 */
	public static void updateCertificationOthers(List<String> redisKeys, List<String> hashKeys, List<String> values,
			String serversInfo, boolean versionIsFxj) {
		RedisDao dao = RedisDao.getInstance(serversInfo);

		List<String> newHashValues = new ArrayList<>(hashKeys.size());
		List<String> hashValues = dao.hgets(redisKeys, hashKeys);

		for (int i = 0, size = redisKeys.size(); i < size; i++) {
			if (versionIsFxj) {
				newHashValues.add(getCertificationOtherNewHashValueFxj(hashValues.get(i), values.get(i)));
			} else {
				newHashValues.add(getCertificationOtherNewHashValue(hashValues.get(i), values.get(i)));
			}
		}

		hashValues.clear();
		hashValues = null;

		dao.hsets(redisKeys, hashKeys, newHashValues);

		newHashValues.clear();
		newHashValues = null;
	}

	/**
	 * 批量根据mac拆分redisKey与hashKey更新 mac 的 hashvalue
	 * 
	 * @param macs
	 *            mac list
	 * @param values
	 *            firstStartTime|firstTerminalNum|source|createTime|sysSource
	 * @param serversInfo
	 */
	public static void updateCertificationMac(List<String> macs, List<String> values, String serversInfo,
			boolean versionIsFxj) {
		RedisDao dao = RedisDao.getInstance(serversInfo);

		List<String> redisKeys = new ArrayList<>(macs.size());
		List<String> hashKeys = new ArrayList<>(macs.size());
		List<String> newHashValues = new ArrayList<>(macs.size());

		for (String mac : macs) {
			String str = mac.replaceAll("-", "");
			redisKeys.add(ParseRelationUtil.macRedisKey(str));
			hashKeys.add(ParseRelationUtil.macHashKey(str));
		}

		List<String> hashValues = dao.hgets(redisKeys, hashKeys);

		for (int i = 0, size = macs.size(); i < size; i++) {
			if (versionIsFxj) {
				newHashValues.add(getCertificationNewHashValueFxj(hashValues.get(i), values.get(i)));
			} else {
				newHashValues.add(getCertificationNewHashValue(hashValues.get(i), values.get(i)));
			}
		}

		hashValues.clear();
		hashValues = null;

		dao.hsets(redisKeys, hashKeys, newHashValues);

		redisKeys.clear();
		redisKeys = null;
		hashKeys.clear();
		hashKeys = null;
		newHashValues.clear();
		newHashValues = null;
	}

	/**
	 * 根据redis old hash value 及身份的value信息
	 * 
	 * @param oldHashValue
	 *            TIMES|SITENO|UPDATE_TIME|采集系统类型(bit 或运算,sys_source)
	 * @param value
	 *            firstStartTime|firstTerminalNum|source|createTime|sysSource
	 * @return pattern:TIMES|SITENO|UPDATE_TIME|采集系统类型(bit 或运算,sys_source)
	 */
	public static String getCertificationNewHashValue(String oldHashValue, String value) {
		String[] vals = value.split(JRedisPoolConfig.STR_DELIMIT);

		String times = "1"; // 保存次数
		long lastStartTime = Long.parseLong(vals[0]); // 保存最近出现时间
		String lastSiteNo = vals[1]; // 保存最近出现场所
		int bit = 0; // bit 值

		int sysSource = 0; // 处理sysSource
		try {
			sysSource = Integer.parseInt(vals[2]);
			bit = bit | ((int) Math.pow(2, sysSource - 1)); // 系统来源
		} catch (Exception e) {
		}

		if (Filter.isBlank(oldHashValue)) {
			return certificationHashValue(times, lastSiteNo, lastStartTime, bit);
		}

		String[] oldHashValues = oldHashValue.split(JRedisPoolConfig.STR_DELIMIT);
		if (oldHashValues.length >= 1) { // 次数不变
			times = oldHashValues[0];
		}

		if (oldHashValues.length >= 3) { // 最近出现时间
			long oldLastStartTime = 0l;
			try {
				oldLastStartTime = Long.parseLong(oldHashValues[2]);
			} catch (Exception e) {
			}

			// redis 最新出现时间比当前身份出现时间晚(及redis是最新出现时间)，取redis时间和场所
			if (lastStartTime < oldLastStartTime) {
				lastStartTime = oldLastStartTime;
				lastSiteNo = oldHashValues[1]; // 最近出现场所取 redis 中的值
			}
		}

		if (oldHashValues.length >= 4) { // 系统来源与redis中的值进行位运算
			try {
				bit = bit | Integer.parseInt(oldHashValues[3]);
			} catch (Exception e) {
			}
		}

		return certificationHashValue(times, lastSiteNo, lastStartTime, bit);
	}

	public static String getCertificationNewHashValueFxj(String oldHashValue, String value) {
		if (Filter.isBlank(oldHashValue)) {
			return "1";
		}
		return oldHashValue.split(JRedisPoolConfig.STR_DELIMIT)[0];
	}

	/**
	 * 根据redis old hash value 及身份的value信息
	 * 
	 * @param oldHashValue
	 *            TIMES|SITENO|UPDATE_TIME
	 * @param value
	 *            firstStartTime|firstTerminalNum|source|createTime|sysSource
	 * @return pattern:TIMES|SITENO|UPDATE_TIME
	 */
	public static String getCertificationOtherNewHashValue(String oldHashValue, String value) {
		String[] vals = value.split(JRedisPoolConfig.STR_DELIMIT);

		String times = "1"; // 保存次数
		long lastStartTime = Long.parseLong(vals[0]); // 保存最近出现时间
		String lastSiteNo = vals[1]; // 保存最近出现场所

		if (Filter.isBlank(oldHashValue)) {
			return certificationOtherHashValue(times, lastSiteNo, lastStartTime);
		}

		String[] oldHashValues = oldHashValue.split(JRedisPoolConfig.STR_DELIMIT);

		if (oldHashValues.length >= 1) { // 次数不变
			times = oldHashValues[0];
		}

		if (oldHashValues.length >= 3) { // 最近出现时间
			long oldLastStartTime = 0l;
			try {
				oldLastStartTime = Long.parseLong(oldHashValues[2]);
			} catch (Exception e) {
			}

			// redis 最新出现时间比当前身份出现时间晚(及redis是最新出现时间)，取redis时间和场所
			if (lastStartTime < oldLastStartTime) {
				lastStartTime = oldLastStartTime;
				lastSiteNo = oldHashValues[1]; // 最近出现场所取 redis 中的值
			}
		}

		return certificationOtherHashValue(times, lastSiteNo, lastStartTime);
	}

	public static String getCertificationOtherNewHashValueFxj(String oldHashValue, String value) {
		if (Filter.isBlank(oldHashValue)) {
			return "1";
		}
		return oldHashValue.split(JRedisPoolConfig.STR_DELIMIT)[0];
	}

	/**
	 * 格式：TIMES|SITENO|UPDATE_TIME|采集系统类型(bit 或运算,sys_source|)
	 * 
	 * @param times
	 * @param lastSiteNo
	 * @param lastStartTime
	 * @param bit
	 * @return
	 */
	public static String certificationHashValue(String times, String lastSiteNo, long lastStartTime, int bit) {
		return join(CP, times, lastSiteNo, String.valueOf(lastStartTime), String.valueOf(bit));
	}

	public static String certificationHashValue(Certification c) {
		int bit = 0; // 处理sysSource
		try {
			int sysSource = Integer.parseInt(c.getSource());
			bit = bit | ((int) Math.pow(2, sysSource - 1)); // 系统来源
		} catch (Exception e) {
		}
		return join(CP, "1", c.getFirstTerminalNum(), c.getFirstStartTime(), String.valueOf(bit));
	}

	/**
	 * 格式：TIMES|SITENO|UPDATE_TIME
	 * 
	 * @param times
	 * @param lastSiteNo
	 * @param lastStartTime
	 * @param bit
	 * @return
	 */
	public static String certificationOtherHashValue(String times, String lastSiteNo, long lastStartTime) {
		return join(CP, times, lastSiteNo, String.valueOf(lastStartTime));
	}

	/**
	 * join strings with spliter
	 * 
	 * @param sp
	 * @param strings
	 * @return
	 */
	public static String join(String sp, String... strings) {
		if (strings == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder(128);
		for (int i = 0, size = strings.length; i < size; i++) {
			builder.append(strings[i]).append(sp);
		}
		int len = builder.size();
		return builder.delete(len - sp.length(), len).toString();
	}

	public static String fetchRedisKey(String protocolType) {
		switch (protocolType) {
		case "1020002": // mac
			return null;
		case "1020004":
			return JRedisPoolConfig.CERTIFICATION_MOBILE_KEY;
		case "1021901": // imei
			return JRedisPoolConfig.CERTIFICATION_IMEI_KEY;
		case "1030001": // qq
			return JRedisPoolConfig.CERTIFICATION_QQ_KEY;
		case "1030036": // wx
			return JRedisPoolConfig.CERTIFICATION_WX_KEY;
		}
		return JRedisPoolConfig.CERTIFICATION_OTHER_KEY; // other
	}

	private RedisUtil() {
	}
}
