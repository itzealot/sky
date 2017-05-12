package com.surfilter.mass.services.parser;

import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.MatchType;
import com.surfilter.mass.entity.BasicAlarmInf;
import com.surfilter.mass.services.MsgParser;

/**
 * 上机日志即终端上下线日志，对应日志类型为005
 * 
 * @author hapuer
 *
 */
public class SjMsgParser implements MsgParser {

	@Override
	public String parseMsg(String[] original) {
		return new StringBuilder(256).append(original[3].trim()).append(original[2].trim()).append(MatchType.CERT.getSimCode()).append(SEPERATOR)
				.append(original[13].trim()).append(MatchType.PHONE.getSimCode()).append(SEPERATOR)
				.append(original[8].trim()).append(MatchType.MAC.getSimCode()).append(SEPERATOR)
				.append(original[28].trim()).append(MatchType.PHONE.getSimCode()).append(SEPERATOR)
				.append(original[28].trim()).append(MatchType.MAC.getSimCode()).append(SEPERATOR)
				.toString();
	}

	@Override
	public BasicAlarmInf parseBasicInf(String[] orginal) {
		return new BasicAlarmInf(Long.valueOf(orginal[4]),Long.valueOf(orginal[5]),orginal[0],null,null);
	}

	@Override
	public String getAlarmType() {
		return ImcaptureConsts.DataType.SJ.getTypeCode();
	}

	@Override
	public String[] codes(String[] orginal) {
		String []codes = new String[3];
		
		codes[0] = orginal[29];
		codes[1] = orginal[30];
		codes[2] = orginal[31];
		
		return codes;
	}

}
