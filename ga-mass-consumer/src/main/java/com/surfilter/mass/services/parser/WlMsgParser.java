/**
 * 
 */
package com.surfilter.mass.services.parser;

import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.MatchType;
import com.surfilter.mass.entity.BasicAlarmInf;
import com.surfilter.mass.services.MsgParser;

/**
 * 围栏日志，，对应日志类型为001
 * 
 * @author hapuer
 *
 */
public class WlMsgParser implements MsgParser{

	
	@Override
	public String parseMsg(String[] original) {
		return new StringBuilder(256).append(original[0]).append(MatchType.MAC.getSimCode()).append(SEPERATOR)
        .append(original[10].trim()).append(MatchType.IMEI.getSimCode()).append(SEPERATOR)
        .append(original[11].trim()).append(MatchType.IMSI.getSimCode()).append(SEPERATOR)
        .append(original[15].trim()).append(MatchType.PHONE.getSimCode()).append(SEPERATOR)
        .append(MatchType.ACCOUNT.getSimCode()).append(original[24].trim()).append(original[23]).append(SEPERATOR)
        .toString();
	}

	@Override
	public BasicAlarmInf parseBasicInf(String[] orginal) {
		String xpoint = null;
		String ypoint = null;
		if(!"MULL".equals(orginal[13])){
			xpoint = orginal[13];
		}
		if (!"MULL".equals(orginal[14])){
			ypoint = orginal[14];
		}
		return new BasicAlarmInf(Long.valueOf(orginal[2]),Long.valueOf(orginal[3]),orginal[18],xpoint,ypoint);
	}

	@Override
	public String getAlarmType() {
		return ImcaptureConsts.DataType.WL.getTypeCode();
	}

	@Override
	public String[] codes(String[] orginal) {
		String []codes = new String[3];
		
		codes[0] = orginal[20];
		codes[1] = orginal[19];
		codes[2] = orginal[21];
		
		return codes;
	}
	
}
