package com.surfilter.mass.services;

import java.util.Collection;
import java.util.List;

import com.surfilter.mass.entity.AlarmInfo;

/**
 * @author hapuer
 *
 */
public interface DuplicateRemover {

	/**
	 * 1. 相同的告警信息只告警一次
	 * 
	 * 2. 根据配置文件 conf.properties 中 match.duplicate.remove 配置的值是1，报警信息进行 Redis
	 * 排重， 否则不进行 Redis 排重；默认是进行 Redis 排重
	 * 
	 * 3. Redis 排重即对于没有离开时间的数据如 WL，则一天只报警一次；对于有离开时间的数据 如 SJ，则会一条数据报警一次
	 * 
	 * @param datas
	 * @return
	 */
	Collection<AlarmInfo> removeDup(List<AlarmInfo> datas);

}
