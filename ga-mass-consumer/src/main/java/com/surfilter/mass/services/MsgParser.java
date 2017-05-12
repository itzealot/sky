package com.surfilter.mass.services;

import com.surfilter.mass.entity.BasicAlarmInf;

/**
 * @author hapuer 消息解析器
 */
public interface MsgParser {

	String SEPERATOR = "|";

	/**
	 * 根据原始消息解析出需要匹配的消息
	 * 
	 * @param original
	 * @return
	 */
	String parseMsg(String[] original);

	BasicAlarmInf parseBasicInf(String[] orginal);

	String getAlarmType();

	/**
	 * String[0] is provinceCode,
	 * 
	 * String[1] is cityCode,
	 * 
	 * String[2] is areaCode.
	 * 
	 * @param orginal
	 * @return
	 */
	String[] codes(String[] orginal);

}
