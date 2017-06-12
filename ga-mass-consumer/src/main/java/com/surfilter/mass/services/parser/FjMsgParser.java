package com.surfilter.mass.services.parser;

import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.MatchType;
import com.surfilter.mass.entity.BasicAlarmInf;
import com.surfilter.mass.services.MsgParser;

/**
 * 非经轨迹日志，对应日志类型为002
 * 
 * @author zealot
 *
 */
public class FjMsgParser implements MsgParser {

	@Override
	public String parseMsg(String[] original) {
		return new StringBuilder(300).append(original[35]).append(original[34]).append(MatchType.CERT.getSimCode()).append(SEPERATOR)
			.append(original[32].trim()).append(MatchType.PHONE.getSimCode()).append(SEPERATOR)
			.append(original[31].trim()).append(MatchType.MAC.getSimCode()).append(SEPERATOR)
			.append(MatchType.ACCOUNT.getSimCode()).append(original[16].trim()).append(original[1].trim()).append(SEPERATOR)
			.append(original[36].trim()).append(MatchType.IMEI.getSimCode()).append(SEPERATOR)
			.append(original[37].trim()).append(MatchType.IMSI.getSimCode()).append(SEPERATOR)
			.append(original[43].trim()).append(MatchType.PHONE.getSimCode()).append(SEPERATOR)
			.append(original[43].trim()).append(MatchType.MAC.getSimCode()).append(SEPERATOR)
			.toString();
	}

	@Override
	public BasicAlarmInf parseBasicInf(String[] orginal) {
		return new BasicAlarmInf(Long.valueOf(orginal[4]),Long.valueOf(orginal[5]),orginal[2],null,null);
	}

	@Override
	public String getAlarmType() {
		return ImcaptureConsts.DataType.FJ.getTypeCode();
	}

	@Override
	public String[] codes(String[] orginal) {
		String []codes = new String[3];
		
		codes[0] = orginal[28];
		codes[1] = orginal[29];
		codes[2] = orginal[30];
		
		return codes;
	}

}
