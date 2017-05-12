package com.surfilter.mass.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.surfilter.mass.entity.AlarmInfo;
import com.surfilter.mass.entity.ClusterAlarmResult;
import com.surfilter.mass.entity.MatchInfo;
import com.surfilter.mass.entity.MsgNotify;
import com.surfilter.mass.entity.ServiceInfo;
import com.surfilter.mass.services.match.algthm.AhoCorasick;

/**
 * 关键人员Dao
 * 
 * @author hapuer
 */
public interface KeyPerDao {

	/**
	 * 1. 查询在布控策略的有效期范围内符合要求的布控条件
	 *
	 * 2. 布控策略见表 focus_store
	 *
	 * @return
	 */
	List<String> getAllKeyPer();

	/**
	 * 查询所有满足条件的布控信息并初始化到 AhoCorasick
	 * 
	 * @param acinfo
	 * @return
	 */
	void addAllKeyPer(AhoCorasick<MatchInfo> acinfo);

	/**
	 * 批量保存报警信息
	 *
	 * @param alarmInfos
	 */
	void saveFocusAlarmInfos(Collection<AlarmInfo> alarmInfos);

	/**
	 * 批量保存重点人报警信息
	 *
	 * @param alarmInfos
	 */
	void saveZdPersonAlarmInfos(Collection<AlarmInfo> zdPersonAlarmInfos);

	/**
	 * 保存通知信息(邮件通知与短信通知)
	 *
	 * @param msgs
	 */
	void saveMsgNotifys(List<MsgNotify> msgs);

	/**
	 * 查询并保存场所编码与场所类型（围栏、非经、网吧等）的对应关系到一个MAP中
	 */
	Map<String, ServiceInfo> querySerCodeType();

	/**
	 * 查询基础配置信息并保存，如wl 源数据过滤配置信息及zd_person cluster配置信息
	 * 
	 * @return
	 */
	Map<String, String> getMacFilterConf();

	/**
	 * 获取不包括重点人的布控信息
	 *
	 * @return
	 */
	List<String> getNotZdAllKeyPer();

	/**
	 * 查询多少分钟内的报警信息(service_code==>List<AlarmInfo>)
	 *
	 * @param minutes
	 * @return
	 */
	Map<String, List<AlarmInfo>> getAlarmInfos(int minutes);

	/**
	 * 保存团伙报警信息
	 *
	 * @param map
	 */
	void saveClusterAlarmResults(Map<String, Set<ClusterAlarmResult>> map);
}
