package com.surfilter.mass.services.parser;

import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.MatchType;
import com.surfilter.mass.entity.BasicAlarmInf;
import com.surfilter.mass.services.MsgParser;

/**
 * 手机特征数据，对应的是数据类型为 014
 * 
 * @author zealot
 *
 */
public class TzMsgParser implements MsgParser {

	@Override
	public String parseMsg(String[] original) {
		return new StringBuilder().append(original[6].trim()).append(MatchType.IMSI.getSimCode()).append(SEPERATOR)
				.append(original[7].trim()).append(MatchType.IMEI.getSimCode()).append(SEPERATOR).toString();
	}

	@Override
	public BasicAlarmInf parseBasicInf(String[] orginal) {
		return new BasicAlarmInf(Long.valueOf(orginal[0]), 0L, orginal[20], null, null);
	}

	@Override
	public String getAlarmType() {
		return ImcaptureConsts.DataType.TZ.getTypeCode();
	}

	@Override
	public String[] codes(String[] orginal) {
		String[] codes = new String[3];

		codes[0] = orginal[22];
		codes[1] = orginal[21];
		codes[2] = orginal[23];

		return codes;
	}

}
