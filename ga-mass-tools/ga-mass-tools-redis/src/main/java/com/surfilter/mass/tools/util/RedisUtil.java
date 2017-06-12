package com.surfilter.mass.tools.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.JRedisPoolConfig;
import com.surfilter.mass.tools.dao.RedisDaoAlias;
import com.surfilter.mass.tools.entity.Certification;

public final class RedisUtil {

	private static Logger LOG = LoggerFactory.getLogger(RedisUtil.class);

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
	public static void updateCertificationInRedis(List<String> hashKeys, List<String> values, String serversInfo) {
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
			updateCertificationMac(macs, macVals, serversInfo);
			macs.clear();
			macVals.clear();
		}

		macs = null;
		macVals = null;

		if (otherRedisKeys.size() > 0) {
			updateCertificationOthers(otherRedisKeys, otherHashKeys, otherVals, serversInfo);
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
			String serversInfo) {
		RedisDaoAlias dao = RedisDaoAlias.getInstance(serversInfo);

		List<String> newHashValues = new ArrayList<>(hashKeys.size());
		List<String> hashValues = dao.hgets(redisKeys, hashKeys);

		for (int i = 0, size = redisKeys.size(); i < size; i++) {
			newHashValues.add(getCertificationOtherNewHashValue(hashValues.get(i), values.get(i)));
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
	public static void updateCertificationMac(List<String> macs, List<String> values, String serversInfo) {
		RedisDaoAlias dao = RedisDaoAlias.getInstance(serversInfo);

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
			newHashValues.add(getCertificationNewHashValue(hashValues.get(i), values.get(i)));
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
		int len = builder.length();
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

	/**
	 * updateCertificationOnlyTimesByRowKey
	 * 
	 * @param values
	 *            idNo|idType
	 * @param serversInfo
	 */
	public static void changeCertificationOnlyTimesByRowKey(List<String> values, String serversInfo) {
		RedisDaoAlias dao = RedisDaoAlias.getInstance(serversInfo);

		int size = values.size();
		List<String> redisKeys = new ArrayList<>(size);
		List<String> hashKeys = new ArrayList<>(size);

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < size; i++) {
			String hashKey = values.get(i);
			String[] vals = hashKey.split(JRedisPoolConfig.STR_DELIMIT);
			String redisKey = fetchRedisKey(vals[1]);

			if (redisKey == null) { // is mac
				String mac = vals[0].replaceAll("-", "");
				redisKeys.add(join(buffer, JRedisPoolConfig.CERTIFICATION_MAC_PREFIX_KEY, mac.substring(0, 5)));
				hashKeys.add(mac.substring(5));
			} else { // is imei,wx,qq,mobile or others
				if (JRedisPoolConfig.CERTIFICATION_OTHER_KEY.equals(redisKey)) {
					hashKeys.add(hashKey);
				} else {
					hashKeys.add(vals[0]);
				}

				redisKeys.add(redisKey);
			}
		}

		List<String> vals = dao.hgets(redisKeys, hashKeys);
		List<String> times = new ArrayList<>(size);

		List<String> newRedisKeys = new ArrayList<>(size);
		List<String> newHashKeys = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			String val = vals.get(i);
			String redisKey = redisKeys.get(i);
			String hashKey = hashKeys.get(i);

			if (!isBlank(val)) { // 截取次数
				String time = val.split(JRedisPoolConfig.STR_DELIMIT)[0];
				times.add(time);
				newRedisKeys.add(redisKey);
				newHashKeys.add(hashKey);

				try {
					Long.parseLong(time);
				} catch (Exception e) {
					LOG.error("error certification redisKey for redis times value:{}",
							join(buffer, redisKey, JRedisPoolConfig.VALUE_SPLITER, hashKey));
				}
			}
		}

		redisKeys.clear();
		redisKeys = null;
		hashKeys.clear();
		hashKeys = null;
		vals.clear();
		vals = null;

		if (times.size() > 0) {
			dao.hsets(newRedisKeys, newHashKeys, times);
			LOG.debug("finish update redis certification value size:{}", times.size());
			newRedisKeys.clear();
			newHashKeys.clear();
			times.clear();
		}

		newHashKeys = null;
		newRedisKeys = null;
		times = null;
	}

	public static void modifyCertificationSaveModel(List<String> values, String serversInfo, int prefixLen) {
		RedisDaoAlias dao = RedisDaoAlias.getInstance(serversInfo);

		int size = values.size();
		List<String> redisKeys = new ArrayList<>(size);
		List<String> hashKeys = new ArrayList<>(size);
		List<String> macs = new ArrayList<>(size);

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < size; i++) {
			String hashKey = values.get(i);
			String[] vals = hashKey.split(JRedisPoolConfig.STR_DELIMIT);
			String redisKey = fetchRedisKey(vals[1]);

			if (redisKey == null) { // is mac
				String mac = vals[0].replaceAll("-", "");
				redisKeys.add(join(buffer, JRedisPoolConfig.CERTIFICATION_MAC_PREFIX_KEY, mac.substring(0, 5)));
				hashKeys.add(mac.substring(5));
				macs.add(mac);
			}
		}

		List<String> vals = dao.hgets(redisKeys, hashKeys);
		dao.hdels(redisKeys, hashKeys); // 删除

		redisKeys.clear();
		redisKeys = null;

		hashKeys.clear();
		hashKeys = null;

		int macLen = macs.size();
		if (macLen <= 0) {
			return;
		}

		List<byte[]> newRedisKeys = new ArrayList<>(macLen);
		List<byte[]> newHashKeys = new ArrayList<>(macLen);
		List<String> newVals = new ArrayList<>(macLen);

		for (int i = 0, len = macs.size(); i < len; i++) {
			String val = vals.get(i);
			String mac = macs.get(i);

			if (!isBlank(val)) { // 初始化次数
				byte[][] keys = MacTransferUtil.macKeyBytes(mac, prefixLen);
				newRedisKeys.add(keys[0]);
				newHashKeys.add(keys[1]);
				newVals.add(val.split("\\|")[0]);
			}
		}

		macs.clear();
		macs = null;

		vals.clear();
		vals = null;

		dao.hsetByBytes(newRedisKeys, newHashKeys, newVals);
		LOG.debug("finish modify redis mac value size:{}", newRedisKeys.size());

		newRedisKeys.clear();
		newRedisKeys = null;

		newHashKeys.clear();
		newHashKeys = null;
	}

	public static boolean isBlank(String val) {
		return val == null || val.isEmpty();
	}

	public static String join(StringBuffer buffer, String... strings) {
		for (int i = 0, len = strings.length; i < len; i++) {
			buffer.append(strings[i]);
		}

		String res = buffer.toString();
		buffer.setLength(0);
		return res;
	}

	private RedisUtil() {
	}
}
