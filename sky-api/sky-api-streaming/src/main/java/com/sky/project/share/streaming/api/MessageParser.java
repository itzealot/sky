package com.sky.project.share.streaming.api;

import java.util.List;

/**
 * MessageParser(适用于解析一类数据，对于相同结构的数据，字段对应位置相同)
 * 
 * @author zealot
 */
public interface MessageParser {

	/**
	 * 
	 * @param arrays
	 * @return
	 */
	String[] getValues(String[] arrays);

	/**
	 * get certifications, [0]:id, [1]:idType
	 * 
	 * @param arrays
	 * @return
	 */
	List<String[]> getIds(String[] arrays);

	/**
	 * [0]:provinceCode, [1]:cityCode, [2]:areaCode and so on
	 * 
	 * @param arrays
	 * @return
	 */
	String[] getCodes(String[] arrays);

	/**
	 * get serviceCode
	 * 
	 * @param arrays
	 * @return
	 */
	String getServiceCode(String[] arrays);

	/**
	 * [0]:sysSource, [1]:source, [2]:companyId and so on
	 * 
	 * @param arrays
	 * @return
	 */
	String[] getSources(String[] arrays);

	/**
	 * [0]:startTime, [1]:endTime, [2]:createTime, [3]:partitionTime and so on
	 * 
	 * @param arrays
	 * @return
	 */
	String[] getTimes(String[] arrays);

	/**
	 * get persistent way
	 * 
	 * @return
	 */
	Persistent getPersistent();
}
