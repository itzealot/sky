/**
 * 
 */
package com.surfilter.mass.services.parser;

import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.MatchType;
import com.surfilter.mass.entity.BasicAlarmInf;
import com.surfilter.mass.services.MsgParser;

/**
 * 行为日志即上网日志，对应日志类型004
 * 
 * @author zealot
 *
 */
public class XwMsgParser implements MsgParser {

	@Override
	public String parseMsg(String[] original) {
		return new StringBuilder(256).append(original[14].trim()).append(original[15].trim()).append(MatchType.CERT.getSimCode()).append(SEPERATOR)
				.append(original[4].trim()).append(MatchType.MAC.getSimCode()).append(SEPERATOR)
				.append(MatchType.ACCOUNT).append(original[9].trim()).append(original[7]).append(SEPERATOR)
				.append(original[16].trim()).append(MatchType.PHONE.getSimCode()).append(SEPERATOR)
				.append(original[39].trim()).append(MatchType.PHONE.getSimCode()).append(SEPERATOR)
				.append(original[39].trim()).append(MatchType.MAC.getSimCode()).append(SEPERATOR)
				.toString();
	}

	@Override
	public BasicAlarmInf parseBasicInf(String[] orginal) {
		return new BasicAlarmInf(Long.valueOf(orginal[3]),0L,orginal[0],null,null);
	}

	@Override
	public String getAlarmType() {
		return ImcaptureConsts.DataType.XW.getTypeCode();
	}

	@Override
	public String[] codes(String[] orginal) {
		String []codes = new String[3];
		
		codes[0] = orginal[27];
		codes[1] = orginal[28];
		codes[2] = orginal[29];
		
		return codes;
	}
}
