package com.surfilter.mass.services.support;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.surfilter.mass.dao.KeyPerDao;
import com.surfilter.mass.entity.AlarmInfo;
import com.surfilter.mass.entity.ClusterAlarmResult;
import com.surfilter.mass.entity.ServiceInfo;
import com.surfilter.mass.utils.ImcaptureUtil;

/**
 * AlarmInfo Analysis
 * 
 * @author zealot
 *
 */
public class AlarmInfoAnalysis {
	private KeyPerDao keyPerDao;

	public AlarmInfoAnalysis(KeyPerDao keyPerDao) {
		this.keyPerDao = keyPerDao;
	}

	/**
	 * zd_person 聚集处警分析
	 * 
	 * @param serviceInfoMap
	 * @param minutes
	 * @param seconds
	 * @param counts
	 */
	public void alalysis(Map<String, ServiceInfo> serviceInfoMap, int minutes, long seconds, int counts,
			int stayLimitSeconds) {
		Map<String, List<AlarmInfo>> map = keyPerDao.getAlarmInfos(minutes + (int) (seconds / 60));
		Map<String, Set<ClusterAlarmResult>> result = ImcaptureUtil.analysisCluster(map, serviceInfoMap, seconds,
				counts);
		map.clear();
		map = null;

		if (result.size() > 0) {
			keyPerDao.saveClusterAlarmResults(result, stayLimitSeconds);
			result.clear();
		}
		result = null;
	}

}
