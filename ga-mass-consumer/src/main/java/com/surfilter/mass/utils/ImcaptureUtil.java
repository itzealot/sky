package com.surfilter.mass.utils;

import com.surfilter.mass.conf.UserType;
import com.surfilter.mass.entity.AlarmInfo;
import com.surfilter.mass.entity.ClusterAlarmResult;
import com.surfilter.mass.entity.ServiceInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

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
	 * 从多个报警信息构建报警团伙信息
	 * 
	 * @param serviceInfoMap
	 * @param alarms
	 * @return
	 */
	public static ClusterAlarmResult extractClusterAlarmResult(Map<String, ServiceInfo> serviceInfoMap,
			List<AlarmInfo> alarms, int counts) {
		String serviceCode = alarms.get(0).getServiceCode();
		ServiceInfo info = serviceInfoMap.get(serviceCode);
		if (info == null) {
			LOG.error("error serviceInfo where serviceCode={} in serviceInfoMap", serviceCode);
			return null;
		}
		String policeCode = info.getPoliceCode(); // 获取policeCode
		long firstAlarmTime = Long.MAX_VALUE; // min value in
		long lastAlarmTime = 0L; // max

		StringBuffer buffer = new StringBuffer(64);
		for (int i = 0, len = alarms.size(); i < len; i++) {
			AlarmInfo a = alarms.get(i);
			if (firstAlarmTime > a.getStartTime()) {
				firstAlarmTime = a.getStartTime();
			}

			if (lastAlarmTime < a.getEndTime()) {
				lastAlarmTime = a.getEndTime();
			}

			if (lastAlarmTime < a.getStartTime()) {
				lastAlarmTime = a.getStartTime();
			}

			if (!buffer.toString().contains("" + a.getZdPersonId())) {
				buffer.append(a.getZdPersonId()).append(SPLITER);
			}
		}

		String result = buffer.deleteCharAt(buffer.length() - 1).toString();
		if (result.split(",").length < counts) {
			return null;
		}
		return new ClusterAlarmResult(serviceCode, info.getProvinceCode(), info.getCityCode(), info.getAreaCode(),
				policeCode, result, firstAlarmTime, lastAlarmTime, Integer.parseInt(alarms.get(0).getZdType()));
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

		cs.add(new ClusterAlarmResult(serviceCode, provinceCode, cityCode, areaCode, policeCode,
				first.getZdPersonId() + "", first.getStartTime(), first.getEndTime(), zdType));
		i++; // 值为 1
		while (i < size) {
			AlarmInfo a = alarmInfos.get(i);
			String zdId = a.getZdPersonId() + "";

			boolean flag = true;
			for (ClusterAlarmResult r : cs) { // 遍历团伙成员集合，把当前的成员合并到团伙中
				String gangList = r.getGangList();

				if (gangList.contains(zdId) || distanceIsBetween(r, a, seconds)) { // 当前报警信息与该团伙距离相符合
					if (!gangList.contains(zdId)) { // 不包含则追加
						r.setGangList(gangList + "," + zdId);
					}

					updateTime(r, a); // 更新时间
					flag = false;
					break;
				}
			}

			if (flag) {// 新增一个团伙并加入团伙列表
				cs.add(new ClusterAlarmResult(serviceCode, provinceCode, cityCode, areaCode, policeCode,
						a.getZdPersonId() + "", a.getStartTime(), a.getEndTime(), zdType));
			}

			i++;
		}

		return cs;
	}

	private static void updateTime(ClusterAlarmResult r, AlarmInfo a) {
		if (r.getFirstAlarmTime() > a.getStartTime() && a.getStartTime() > 0L) {
			r.setFirstAlarmTime(a.getStartTime());
		}

		if (r.getLastAlarmTime() < a.getEndTime()) {
			r.setLastAlarmTime(a.getEndTime());
		}

		if (r.getLastAlarmTime() < a.getStartTime()) {
			r.setLastAlarmTime(a.getStartTime());
		}
	}

	public static Set<ClusterAlarmResult> analysisAlarmInfosBak(List<AlarmInfo> alarmInfos,
			Map<String, ServiceInfo> serviceInfoMap, long seconds, int counts) {
		Set<ClusterAlarmResult> clusters = new TreeSet<>();

		int size = alarmInfos.size();
		for (int i = 0; i < size; i++) {
			List<AlarmInfo> alarmsI = new ArrayList<AlarmInfo>(); // 团伙成员
			AlarmInfo aI = alarmInfos.get(i);
			alarmsI.add(aI);

			for (int j = 0; j < size; j++) {
				AlarmInfo aJ = alarmInfos.get(j);

				if (aI.getZdPersonId() != aJ.getZdPersonId() && !alarmsI.contains(aJ)
						&& distanceIsBetween(aI, aJ, seconds)) { // 不是同一个人且在时间范围内
					alarmsI.add(aJ);
				}

				if (aI.getZdPersonId() == aJ.getZdPersonId()) { // 是同一个人，则更新时间信息
					updateTime(aJ, aI);
				}

				if (alarmsI.contains(aJ)) { // 相同报警信息则更新时间
					updateTime(aJ, alarmInfos.get(alarmsI.indexOf(aJ)));
				}
			}

			if (alarmsI.size() >= counts) { // 团伙人数达到指定范围内，构建团伙成员信息
				ClusterAlarmResult result = extractClusterAlarmResult(serviceInfoMap, alarmsI, counts);

				if (result != null) {
					if (!clusters.contains(result)) { // 是否是存在相同团伙
						clusters.add(result);
					} else { // 存在相同团伙，则合并
						mergeClusterAlarmResult(clusters, result);
					}
				}
			} else {
				alarmsI.clear();
				alarmsI = null;
			}
		}

		return clusters;
	}

	private static void mergeClusterAlarmResult(Set<ClusterAlarmResult> clusters, ClusterAlarmResult src) {
		Iterator<ClusterAlarmResult> it = clusters.iterator();

		while (it.hasNext()) {
			ClusterAlarmResult dst = it.next();

			if (dst.equals(src)) { // 相同团伙，时间与gangList 合并
				mergeClusterAlarmResult(src, dst);
			}
		}
	}

	private static void mergeClusterAlarmResult(ClusterAlarmResult src, ClusterAlarmResult dst) {
		// 时间合并
		long firstAlarmTime = dst.getFirstAlarmTime();
		long lastAlarmTime = dst.getLastAlarmTime();

		if (src.getFirstAlarmTime() < firstAlarmTime) {
			firstAlarmTime = src.getFirstAlarmTime();
		}

		if (lastAlarmTime < src.getFirstAlarmTime()) {
			lastAlarmTime = src.getFirstAlarmTime();
		}

		if (lastAlarmTime < src.getLastAlarmTime()) {
			lastAlarmTime = src.getLastAlarmTime();
		}

		if (dst.getGangList().length() < src.getGangList().length()) {
			dst.setGangList(src.getGangList());
		}

		dst.setFirstAlarmTime(firstAlarmTime);
		dst.setLastAlarmTime(lastAlarmTime);
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

		for (Entry<String, List<AlarmInfo>> entry : map.entrySet()) {
			List<AlarmInfo> alarmInfos = entry.getValue();
			if (alarmInfos != null && !alarmInfos.isEmpty()) {
				List<ClusterAlarmResult> clusterAlarmResults = ImcaptureUtil.analysisAlarmInfos(alarmInfos,
						serviceInfoMap, seconds);
				if (clusterAlarmResults != null && clusterAlarmResults.size() > 0) {
					Set<ClusterAlarmResult> results = mergeClusterAlarmResult(clusterAlarmResults, counts);
					if (results != null && results.size() > 0) {
						clusterMap.put(entry.getKey(), results);
					}
				}
			}
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
		StringBuffer buffer = new StringBuffer(64);

		for (ClusterAlarmResult c : clusterAlarmResults) {
			if (c.getGangList().split(",").length >= counts) {
				String[] arrays = c.getGangList().split(",");
				Arrays.sort(arrays);
				c.setGangList(join(arrays, buffer));
				results.add(c);
			}
		}

		return results;
	}

	public static boolean hasSameGangList(String[] gs, String ganglist) {
		for (int i = 0, lenI = gs.length; i < lenI; i++) {
			if (ganglist.contains(gs[i])) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 合并 gangList
	 * 
	 * @param gs
	 * @param gangList
	 * @return
	 */
	public static String joinGangList(String[] gs, String gangList) {
		StringBuffer buffer = new StringBuffer(64);
		buffer.append(gangList);

		for (int i = 0, lenI = gs.length; i < lenI; i++) {
			if (!gangList.contains(gs[i])) {
				buffer.append(gs[i]).append(SPLITER);
			}
		}

		return buffer.deleteCharAt(buffer.length() - 1).toString();
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

		String[] small = smallGangList.split(",");
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

	private ImcaptureUtil() {
	}
}
