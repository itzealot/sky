package com.surfilter.mass.utils;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.conf.UserType;
import com.surfilter.mass.entity.AlarmInfo;
import com.surfilter.mass.entity.ClusterAlarmResult;
import com.surfilter.mass.entity.ClusterAlarmResultQueryObject;
import com.surfilter.mass.entity.ServiceInfo;

/**
 * cluster alarm info analysis util
 * 
 * @author zealot
 *
 */
public final class ImcaptureUtil {

	private static Logger LOG = LoggerFactory.getLogger(ImcaptureUtil.class);

	private static final String FILED_SP = "|"; // matchValue|matchType
	private static final String MESSAGE_SP = ";";
	private static final String SPECIAL_SP = "#"; // name#certCode
	private static final String SPLITER = ",";
	private static final String FILED_SP_REGEX = "\\|";

	public static long abs(long val) {
		return val >= 0 ? val : -val;
	}

	public static boolean between(long a, long b, long range) {
		return abs(a - b) <= range;
	}

	/**
	 * 根据告警信息返回告警信息的协议类型
	 * 
	 * @param alarmInfo
	 * @return
	 */
	public static String getProtocolType(AlarmInfo alarmInfo) {
		switch (alarmInfo.getMatchType()) {
		case "1":
			return UserType.MAC.getValue();
		case "2":
			return UserType.MOBILE.getValue();
		case "3":
			return UserType.IMEI.getValue();
		case "4":
			return UserType.IMSI.getValue();
		}

		return alarmInfo.getMatchChildValue();
	}

	public static int getValue(String val, int defaultValue) {
		try {
			return Integer.parseInt(val);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static long getValue(String val, long defaultValue) {
		try {
			return Long.parseLong(val);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static String getValue(String val, String defaultValue) {
		return StringUtils.isEmpty(val) ? defaultValue : val;
	}

	public static String getValue(String val) {
		return getValue(val, "0");
	}

	public static boolean isEmpty(String val) {
		return StringUtils.isEmpty(val) ? true : ("0".equals(val) || "-1".equals(val));
	}

	public static boolean isEmpty(long val) {
		return val == 0L || val == -1L;
	}

	public static void addIntoMap(String key, AlarmInfo value, Map<String, List<AlarmInfo>> map) {
		List<AlarmInfo> vals = map.get(key);
		if (vals == null) {
			vals = new ArrayList<>(128);
		}
		vals.add(value);
		map.put(key, vals);
	}

	/**
	 * 出现时间是否在时间范围内
	 * 
	 * @param a
	 * @param b
	 * @param seconds
	 * @return
	 */
	public static boolean distanceIsBetween(AlarmInfo a, AlarmInfo b, long seconds) {
		Long startTimeA = a.getStartTime();
		Long endTimeA = a.getEndTime();

		Long startTimeB = b.getStartTime();
		Long endTimeB = b.getEndTime();

		if (endTimeA < 1l && endTimeB < 1l) { // endTime 都为空
			return between(startTimeA, startTimeB, seconds);
		} else if (endTimeA < 1l) { // A endTime 为空，B endTime 不为空
			return between(endTimeB, startTimeA, seconds);
		} else if (endTimeB < 1l) { // B endTime 为空，A endTime 不为空
			return between(endTimeA, startTimeB, seconds);
		} else { // endTime 都不为空
			return between(endTimeA, endTimeB, seconds) || between(startTimeA, startTimeB, seconds)
					|| between(endTimeA, startTimeB, seconds) || between(endTimeB, startTimeA, seconds);
		}
	}

	public static boolean distanceIsBetween(ClusterAlarmResult r, AlarmInfo a, long seconds) {
		Long startTimeA = r.getFirstAlarmTime();
		Long endTimeA = r.getLastAlarmTime();

		Long startTimeB = a.getStartTime();
		Long endTimeB = a.getEndTime();

		if (endTimeA < 1l && endTimeB < 1l) { // endTime 都为空
			return between(startTimeA, startTimeB, seconds);
		} else if (endTimeA < 1l) { // A endTime 为空，B endTime 不为空
			return between(endTimeB, startTimeA, seconds) || between(startTimeB, startTimeA, seconds);
		} else if (endTimeB < 1l) { // B endTime 为空，A endTime 不为空
			return between(endTimeA, startTimeB, seconds) || between(startTimeA, startTimeB, seconds);
		} else { // endTime 都不为空
			return between(endTimeA, endTimeB, seconds) || between(startTimeA, startTimeB, seconds)
					|| between(endTimeA, startTimeB, seconds) || between(endTimeB, startTimeA, seconds);
		}
	}

	/**
	 * 拼接告警信息，有身份证和姓名时，则为:name#cert_code；否则返回:matchValue:matchChildType
	 * 
	 * @param a
	 * @return
	 */
	public static String joinAlarmInfo(AlarmInfo a) {
		StringBuffer buffer = new StringBuffer();

		// name#certificateCode
		if (UserType.CERTIFICATE.getValue().equals(a.getCertType()) && !isEmpty(a.getCertCode())) {
			if (!isEmpty(a.getUserName())) {
				buffer.append(a.getUserName());
			}

			return buffer.append(SPECIAL_SP).append(a.getCertCode()).append(MESSAGE_SP).toString();
		}

		// others:matchValue|matchType
		return buffer.append(a.getMatchValue()).append(FILED_SP).append(getProtocolType(a)).append(MESSAGE_SP)
				.toString();
	}

	public static void joinAlarmInfo(AlarmInfo a, StringBuffer buffer) {
		// name#certificateCode
		if (UserType.CERTIFICATE.getValue().equals(a.getCertType()) && !isEmpty(a.getCertCode())) {
			if (!isEmpty(a.getUserName())) {
				buffer.append(a.getUserName());
			}

			buffer.append(SPECIAL_SP).append(a.getCertCode()).append(MESSAGE_SP);
		}

		// others:matchValue|matchType
		buffer.append(a.getMatchValue()).append(FILED_SP).append(getProtocolType(a)).append(MESSAGE_SP);
	}

	/**
	 * src=>dst，根据优先级更新告警信息
	 * 
	 * @param src
	 * @param dst
	 */
	public static void updateWithPriority(AlarmInfo src, AlarmInfo dst) {
		updateZdInfoWithPriority(src, dst);
		compareMathChildValueAndUpdate(src, dst);
	}

	public static void updateTime(AlarmInfo src, AlarmInfo dst) {
		if (dst.getEndTime() < src.getStartTime()) {
			dst.setEndTime(src.getStartTime());
		}

		if (dst.getEndTime() < src.getEndTime()) {
			dst.setEndTime(src.getEndTime());
		}

		if (dst.getStartTime() > src.getStartTime() && src.getStartTime() > 0L) {
			dst.setStartTime(src.getStartTime());
		}
	}

	/**
	 * 比较报警信息，并根据权限更新 certType与certCode,userName,mobile;src=>dst
	 * 
	 * @param src
	 * @param dst
	 */
	public static void updateZdInfoWithPriority(AlarmInfo src, AlarmInfo dst) {
		String certType = src.getCertType();
		String certCode = src.getCertCode();

		String dstCertType = dst.getCertType();
		// certType 与 certCode 优先级最高
		if (!isEmpty(certCode) && !isEmpty(certType) && !certType.equals(dstCertType)
				&& !UserType.CERTIFICATE.getValue().equals(dstCertType)) {
			dst.setCertType(certType);
			dst.setCertCode(certCode);
		}

		// 姓名其次
		String userName = src.getUserName();
		String dstUserName = dst.getUserName();
		// src userName 不为空且 dst userName 为空
		if (isEmpty(dstUserName) && !isEmpty(userName)) {
			dst.setUserName(userName);
		}

		// mobile
		String mobile = src.getZdPersonMobile();
		String dstMobile = dst.getZdPersonMobile();
		// src mobile 不为空且与 dst mobile 为空
		if (!isEmpty(mobile) && isEmpty(dstMobile)) {
			dst.setZdPersonMobile(mobile);
		}
	}

	/**
	 * 比较报警信息，并根据权限更新 matchType,matchValue,matchChildValue;src=>dst
	 * 
	 * @param src
	 * @param dst
	 */
	public static void compareMathChildValueAndUpdate(AlarmInfo src, AlarmInfo dst) {
		if (compareWithIdType(src.getMatchChildValue(), src.getMatchChildValue())) {
			updateMathInfo(src, dst);
		}
	}

	/**
	 * 比较协议类型返回是否更新(true:更新,false:不更新)
	 * 
	 * certificate>mobile>imei=imsi>mac>wx>qq
	 * 
	 * 
	 * @param srcIdType
	 * @param dstIdType
	 * @return
	 */
	public static boolean compareWithIdType(String srcIdType, String dstIdType) {
		// src type 为空或者dst type为身份证或者 Type相同则不进行更新
		if (isEmpty(srcIdType) || dstIdType.equals(UserType.CERTIFICATE.getValue()) || srcIdType.equals(dstIdType)) {
			return false;
		}

		if (isEmpty(dstIdType) || srcIdType.equals(UserType.CERTIFICATE.getValue())) {
			return true;
		}

		if (dstIdType.equals(UserType.QQ.getValue())) {
			return srcIdType.equals(UserType.MOBILE.getValue()) || srcIdType.equals(UserType.IMEI.getValue())
					|| srcIdType.equals(UserType.IMSI.getValue()) || srcIdType.equals(UserType.MAC.getValue())
					|| srcIdType.equals(UserType.WX.getValue());
		}

		if (dstIdType.equals(UserType.WX.getValue())) {
			return srcIdType.equals(UserType.MOBILE.getValue()) || srcIdType.equals(UserType.IMEI.getValue())
					|| srcIdType.equals(UserType.IMSI.getValue()) || srcIdType.equals(UserType.MAC.getValue());
		}

		if (dstIdType.equals(UserType.MAC.getValue())) {
			return srcIdType.equals(UserType.MOBILE.getValue()) || srcIdType.equals(UserType.IMEI.getValue())
					|| srcIdType.equals(UserType.IMSI.getValue());
		}

		if ((dstIdType.equals(UserType.IMSI.getValue()) || dstIdType.equals(UserType.IMEI.getValue()))
				&& (srcIdType.equals(UserType.MOBILE.getValue()))) {
			return true;
		}

		return false;
	}

	/**
	 * 更新报警信息中的匹配信息
	 * 
	 * @param src
	 * @param dst
	 */
	public static void updateMathInfo(AlarmInfo src, AlarmInfo dst) {
		dst.setMatchChildValue(src.getMatchChildValue());
		dst.setMatchValue(src.getMatchValue());
		dst.setMatchType(src.getMatchType());
	}

	/**
	 * 分析报警日志
	 * 
	 * @param alarmInfos
	 * @param serviceInfoMap
	 * @param seconds
	 * @return
	 */
	public static List<ClusterAlarmResult> analysisAlarmInfos(List<AlarmInfo> alarmInfos,
			Map<String, ServiceInfo> serviceInfoMap, long seconds) {
		String serviceCode = alarmInfos.get(0).getServiceCode();

		ServiceInfo info = serviceInfoMap.get(serviceCode);
		if (info == null) {
			return null;
		}

		List<ClusterAlarmResult> cs = new ArrayList<>(); // 报警结果
		int size = alarmInfos.size();
		int i = 0;
		AlarmInfo first = alarmInfos.get(i);
		int zdType = Integer.parseInt(first.getZdType());
		String provinceCode = info.getProvinceCode();
		String cityCode = info.getCityCode();
		String areaCode = info.getAreaCode();
		String policeCode = info.getPoliceCode(); // 获取policeCode

		StringBuffer buffer = new StringBuffer(48);

		String firstGangTime = buffer.append(first.getStartTime()).append(FILED_SP)
				.append(fetchMaxTime(first.getStartTime(), first.getEndTime())).toString();
		buffer.setLength(0);
		cs.add(new ClusterAlarmResult(serviceCode, provinceCode, cityCode, areaCode, policeCode,
				String.valueOf(first.getZdPersonId()), first.getStartTime(), first.getEndTime(), zdType,
				firstGangTime));

		i++; // 值为 1
		while (i < size) {
			AlarmInfo a = alarmInfos.get(i);
			String zdId = String.valueOf(a.getZdPersonId());

			boolean flag = true;
			for (ClusterAlarmResult r : cs) { // 遍历团伙成员集合，把当前的成员合并到团伙中
				String gangList = r.getGangList();
				int index = indexOf(gangList.split(SPLITER), zdId);

				if (index != -1 || distanceIsBetween(r, a, seconds)) { // 当前报警信息与该团伙距离相符合
					if (index == -1) { // 不包含对应的zdPersonId则追加
						// gangList追加
						r.setGangList(buffer.append(gangList).append(SPLITER).append(zdId).toString());
						buffer.setLength(0);

						// gangTime追加
						r.setGangTime(buffer.append(r.getGangTime()).append(SPLITER).append(a.getStartTime())
								.append(FILED_SP).append(fetchMaxTime(a.getStartTime(), a.getEndTime())).toString());
						buffer.setLength(0);
					} else { // 存在，更新对应zdPersonId所在位置的 gangTime
						updateGangTime(r, a, index);
					}

					updateTime(r, a); // 更新时间
					flag = false;
					break;
				}
			}

			if (flag) {// 新增一个团伙并加入团伙列表
				String gangTime = buffer.append(a.getStartTime()).append(FILED_SP)
						.append(fetchMaxTime(a.getStartTime(), a.getEndTime())).toString();
				buffer.setLength(0);
				cs.add(new ClusterAlarmResult(serviceCode, provinceCode, cityCode, areaCode, policeCode,
						String.valueOf(a.getZdPersonId()), a.getStartTime(), a.getEndTime(), zdType, gangTime));
			}

			i++;
		}

		return cs;
	}

	private static Long fetchMaxTime(Long startTime, Long endTime) {
		return endTime == null || startTime >= endTime ? startTime : endTime;
	}

	/**
	 * 根据zdPersonId更新报警团伙指定位置的 gangTime
	 * 
	 * @param r
	 * @param a
	 */
	private static void updateGangTime(ClusterAlarmResult r, AlarmInfo a, int index) {
		String[] gangTime = r.getGangTime().split(SPLITER);
		StringBuffer buffer = new StringBuffer(80);

		for (int i = 0; i < index; i++) {
			buffer.append(gangTime[i]);
			buffer.append(SPLITER);
		}

		String[] times = gangTime[index].split(FILED_SP_REGEX);

		long startTime = Long.valueOf(times[0]);
		if (startTime > a.getStartTime() && a.getStartTime() > 1L) {
			startTime = a.getStartTime();
		}

		long endTime = Long.valueOf(times[1]);

		if (endTime < a.getStartTime()) {
			endTime = a.getStartTime();
		}

		if (endTime < a.getEndTime()) {
			endTime = a.getEndTime();
		}

		buffer.append(startTime).append(FILED_SP).append(endTime).append(SPLITER);

		for (int i = index + 1; i < gangTime.length; i++) {
			buffer.append(gangTime[i]);
			buffer.append(SPLITER);
		}
		r.setGangTime(buffer.deleteCharAt(buffer.length() - 1).toString());
	}

	private static int indexOf(String[] ids, String id) {
		for (int i = 0; i < ids.length; i++) {
			if (ids[i].equals(id)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 更新报警团伙的首次发现时间及最晚发现时间
	 * 
	 * @param r
	 * @param a
	 */
	private static void updateTime(ClusterAlarmResult r, AlarmInfo a) {
		if (r.getFirstAlarmTime() > a.getStartTime() && a.getStartTime() > 1L) {
			r.setFirstAlarmTime(a.getStartTime());
		}

		if (r.getLastAlarmTime() < a.getEndTime()) {
			r.setLastAlarmTime(a.getEndTime());
		}

		if (r.getLastAlarmTime() < a.getStartTime()) {
			r.setLastAlarmTime(a.getStartTime());
		}
	}

	/**
	 * 分析报警 cluster
	 * 
	 * @param map
	 *            serviceCode=>List<AlarmInfo>
	 * @param serviceInfoMap
	 *            场所信息缓存 map
	 * @param seconds
	 *            多少秒内认定为cluster
	 * @param counts
	 *            报警团伙人数限制
	 * @return
	 */
	public static Map<String, Set<ClusterAlarmResult>> analysisCluster(Map<String, List<AlarmInfo>> map,
			Map<String, ServiceInfo> serviceInfoMap, long seconds, int counts) {
		Map<String, Set<ClusterAlarmResult>> clusterMap = new HashMap<>();

		if (map == null) {
			LOG.error("analysis alarm info map is null.");
			return clusterMap;
		}

		int sum = 0;
		for (Entry<String, List<AlarmInfo>> entry : map.entrySet()) {
			List<AlarmInfo> alarmInfos = entry.getValue();
			if (alarmInfos != null && !alarmInfos.isEmpty()) {
				List<ClusterAlarmResult> clusterAlarmResults = analysisAlarmInfos(alarmInfos, serviceInfoMap, seconds);
				if (clusterAlarmResults != null && clusterAlarmResults.size() > 0) {
					Set<ClusterAlarmResult> results = mergeClusterAlarmResult(clusterAlarmResults, counts);
					if (results != null && results.size() > 0) {
						sum += results.size();
						clusterMap.put(entry.getKey(), results);
					}
				}
			}
		}

		if (sum > 0) {
			LOG.info("Total analysis cluster:{}", sum);
		}

		return clusterMap;
	}

	/**
	 * 合并报警团伙
	 * 
	 * @param clusterAlarmResults
	 * @return
	 */
	public static Set<ClusterAlarmResult> mergeClusterAlarmResult(List<ClusterAlarmResult> clusterAlarmResults,
			int counts) {
		Set<ClusterAlarmResult> results = new HashSet<>();
		StringBuffer buffer = new StringBuffer(80);

		for (ClusterAlarmResult c : clusterAlarmResults) {
			String[] gangList = c.getGangList().split(SPLITER);

			if (gangList.length >= counts) {
				String[] gangTime = c.getGangTime().split(SPLITER);
				sortAsc(gangList, gangTime);
				c.setGangList(join(gangList, buffer));
				c.setGangTime(join(gangTime, buffer));
				c.setClusterTime((int) (c.getLastAlarmTime() - c.getFirstAlarmTime()));

				results.add(c);
			}
		}

		return results;
	}

	public static String join(String[] arrays, StringBuffer buffer) {
		int i = 0, len = arrays.length - 1;
		for (; i < len; i++) {
			buffer.append(arrays[i]).append(SPLITER);
		}
		String result = buffer.append(arrays[i]).toString();
		buffer.setLength(0);
		return result;
	}

	/**
	 * gangList 是否是包含关系
	 * 
	 * @param gangListA
	 * @param gangListB
	 * @return
	 */
	public static boolean containsGangList(String gangListA, String gangListB) {
		String bigGangList = gangListA;
		String smallGangList = gangListA;

		if (gangListA.length() > gangListB.length()) {
			smallGangList = gangListB;
		} else {
			bigGangList = gangListB;
		}

		String[] small = smallGangList.split(SPLITER);
		for (int i = 0, len = small.length; i < len; i++) {
			if (!bigGangList.contains(small[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 根据页面配置wl数据是否过滤无厂商数据，ap数据，信号强度为-1或为0的数据
	 * 
	 * 过滤无厂商数据;<br />
	 * ap数据ap_type:arrays[1]=1;<br />
	 * 信号强度-1的数据:arrays[4]=-1;<br />
	 * 过滤信号强度 0且 end_time不为零:arrays[4]=0 and arrays[3]!=0;<br />
	 * 
	 * @param arrays
	 * @return
	 */
	public static boolean filter(Set<String> macCompanyKeys, Map<String, String> macFilterMap, String[] arrays) {
		return (!macCompanyKeys.contains(arrays[0].length() >= 8 ? arrays[0].substring(0, 8) : arrays[0])
				&& "true".equals(macFilterMap.get("mass_filter_mac_no_company_id")))
				|| ("1".equals(arrays[1]) && "true".equals(macFilterMap.get("mass_filter_mac_ap")))
				|| ("-1".equals(arrays[4]) && "true".equals(macFilterMap.get("mass_filter_power_negative_one")))
				|| ("0".equals(arrays[4]) && !"0".equals(arrays[3])
						&& "true".equals(macFilterMap.get("mass_filter_power_zero")));
	}

	/**
	 * sort gangList and gangTime
	 * 
	 * @param gangList
	 * @param gangTime
	 */
	public static void sortAsc(String[] gangList, String[] gangTime) {
		for (int i = 0; i < gangList.length; i++) {
			for (int j = i + 1; j < gangList.length; j++) {
				long val1 = Long.valueOf(gangList[i]);
				long val2 = Long.valueOf(gangList[j]);

				if (val1 > val2) {
					String temp = gangList[i];
					gangList[i] = gangList[j];
					gangList[j] = temp;

					temp = gangTime[i];
					gangTime[i] = gangTime[j];
					gangTime[j] = temp;
				}
			}
		}
	}

	/**
	 * 合并 gangTime
	 * 
	 * @param obj
	 * @param r
	 * @return
	 */
	public static String mergeGangTime(ClusterAlarmResultQueryObject obj, ClusterAlarmResult r, int stayLimitSeconds) {
		if (isEmpty(obj.getGangTime())) {
			return r.getGangTime();
		}

		String[] oldGangTime = obj.getGangTime().split(SPLITER);
		String[] gangTime = r.getGangTime().split(SPLITER);

		if (oldGangTime.length != gangTime.length) {
			LOG.warn("old gang_time from mysql is not equal to current.");
			return r.getGangTime();
		}

		StringBuffer buffer = new StringBuffer(128);

		for (int i = 0; i < oldGangTime.length; i++) {
			String[] oldTimes = oldGangTime[i].split(FILED_SP_REGEX);
			String[] times = gangTime[i].split(FILED_SP_REGEX);

			long oldStartTime = NumberUtil.parseLong(oldTimes[0]);
			long oldEndTime = NumberUtil.parseLong(oldTimes[1]);

			long startTime = NumberUtil.parseLong(times[0]);
			long endTime = NumberUtil.parseLong(times[1]);

			// 间隔时长在指定范围内
			if (abs(startTime - oldEndTime) <= stayLimitSeconds) {
				startTime = min(startTime, oldStartTime);
				endTime = max(oldEndTime, endTime);
			}

			buffer.append(startTime).append(FILED_SP).append(endTime).append(SPLITER);
		}

		return buffer.deleteCharAt(buffer.length() - 1).toString();
	}

	private ImcaptureUtil() {
	}
}
