package com.surfilter.mass.services;

import java.util.List;

import com.surfilter.mass.entity.AlarmInfo;

/**
 * 数据匹配接口
 * 
 * @author zealot
 *
 */
public interface DataMatcher {

	String SEPERATOR = "\002";

	/**
	 * 根据传入数据进行匹配，返回匹配的结果
	 * 
	 * @param datas
	 * @return
	 */
	List<AlarmInfo> match(List<String> datas);

}
