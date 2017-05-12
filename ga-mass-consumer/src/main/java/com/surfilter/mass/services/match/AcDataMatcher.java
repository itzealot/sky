package com.surfilter.mass.services.match;

import com.google.common.collect.Lists;
import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.ImcaptureContext;
import com.surfilter.mass.dao.KeyPerDao;
import com.surfilter.mass.entity.*;
import com.surfilter.mass.services.AbstractDataMatcher;
import com.surfilter.mass.services.MsgParser;
import com.surfilter.mass.services.MsgParserHolder;
import com.surfilter.mass.services.match.algthm.AhoCorasick;
import com.surfilter.mass.services.match.algthm.SearchResult;
import com.surfilter.mass.utils.ImcaptureUtil;
import org.joda.time.DateTime;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 专注文本高速匹配(匹配单行记录)
 * 
 * @author hapuer
 */
public class AcDataMatcher extends AbstractDataMatcher{

	private int interval;
	private KeyPerDao keyPerDao;
	private MsgParserHolder msgParserHolder;

	public AcDataMatcher(ImcaptureContext context, KeyPerDao keyPerDao){
		super(context);
		this.interval = context.getInt(ImcaptureConsts.MATCH_INIT_INTERVAL, 5);
		this.keyPerDao = keyPerDao;
		this.msgParserHolder = MsgParserHolder.getInstance(context.getConf());
	}

	/**
	 * 如果是重点人，不做serviceRang以及areaRang过滤
	 * 
	 * @param row
	 * @return
	 */
	public List<AlarmInfo> doMatch(String row){
		// 构建在布控策略有效期范围内的查询器
		AhoCorasick<MatchInfo> ac = ACHelper.getInstance(interval).getAC(this.keyPerDao);
		ServiceInfoHelper helper = ServiceInfoHelper.getInstance(context.getConf());
		
		if(ac == null) {
			LOG.error("create AhoCorasick<MatchInfo> match object fail.");
			return null;
		}

		List<AlarmInfo> matchResults = Lists.newArrayList();
		String arrays[] = row.split("\\|");
		
		if(arrays != null && arrays.length > 0){
			MsgParser msgParser = msgParserHolder.getMsgParser(arrays.length);
			if(msgParser != null){
				String matchRow = msgParser.parseMsg(arrays);

				// 根据传入的信息在已保存的查询器中进行匹配
				Iterator<SearchResult<MatchInfo>> scResults = ac.search(matchRow.getBytes());
				if(scResults != null){
					/** 初始化场所信息 */
					Map<String, ServiceInfo> serCodeTypeMap = helper.getServiceInfo();
					
					while(scResults.hasNext()){
						SearchResult<MatchInfo> result = scResults.next();
						if(result!=null){
							Set<MatchInfo> matchSet = result.getOutputs();
							BasicAlarmInf basicInf = msgParser.parseBasicInf(arrays);
							for(MatchInfo s:matchSet){
								AlarmInfo info = new AlarmInfo(
										s.getMatchValue(),
										s.getMatchType(),
										s.getMatchChildValue(),
										s.getMacId(),
										s.getStoreId(),
										basicInf.getStartTime(),
										basicInf.getEndTime(),
										s.getAlarmPhones(),
										s.getAlarmEmails(),
										basicInf.getServiceCode(),
										basicInf.getXpoint(),
										basicInf.getYpoint(),
										msgParser.getAlarmType(),
										s.getDayAlarmCount(),
										s.getAlarmInterval(),
										s.getCertType(),
										s.getCertCode(),
										s.getZdPersonId(),
										s.getZdPersonMobile(),
										s.getUserName(),
										s.getZdType(),
										s.isPhoneAlarm(),
										s.isEmailAlarm());

								if(timeInRange(info) && isInRange(arrays, msgParser, info, s, serCodeTypeMap)) {
									if("1".equals(info.getMatchType()) && ImcaptureUtil.filter(this.context.getMacCompanyKeys(), helper.getMacFilterMap(), arrays)){ // wl数据校验
										LOG.debug("wl mac: {} had been filtered.", info.getMatchValue());
										continue;
									}
									LOG.debug("Match inf:{}|{}|{} : is in range, serviceRange={}, areaRange={}, ServiceType={}",
											s.getMatchValue(), basicInf.getServiceCode(), basicInf.getStartTime(),
											s.getServiceRange(), s.getAreaRange(), s.getServiceType());
									matchResults.add(info);
								} else {
									LOG.debug("Match inf:{}|{}|{} : is not in range, serviceRange={}, areaRange={}, ServiceType={}",
											s.getMatchValue(), basicInf.getServiceCode(), basicInf.getStartTime(),
											s.getServiceRange(), s.getAreaRange(), s.getServiceType());
								}
							}
						}
					}
				}
			}
		}
		return matchResults;
	}

	/**
	 * @param arrays
	 * @param msgParser
	 * @param info
	 * @param matchInfo
	 * @param serCodeTypeMap
	 * @return
	 * 如果是重点人，直接返回true，不做区域和场所过滤
	 */
	private boolean isInRange(String []arrays, MsgParser msgParser, AlarmInfo info, MatchInfo matchInfo,
							  Map<String, ServiceInfo> serCodeTypeMap) {
		if(!ImcaptureUtil.isEmpty(info.getZdPersonId())){
			return true;
		}
		String serviceRange = matchInfo.getServiceRange();
		String areaRange = matchInfo.getAreaRange();
		String serviceType = matchInfo.getServiceType();

		if(!"0".equals(serviceRange)) {
			return isInServiceRange(info, serviceRange);
		} else if(!"0".equals(areaRange) && !",,".equals(areaRange)) {
			if(isInAreaRange(arrays, msgParser, info, areaRange)) { // 区域范围内是否有场所布控类型
				return !"-1".equals(serviceType) ? isServiceTypeInRange(info, serCodeTypeMap, matchInfo) : true;
			}
			
			return false;
		}
		
		return true;
	}

	/**
	 * @param info
	 * @return
	 */
	private boolean timeInRange(AlarmInfo info) {
		Integer offset = context.getInt(ImcaptureConsts.ALARM_TIME_RANGE,180);
		Long startTime = info.getStartTime();
		if(startTime==null || startTime==0L) return false;
		return !DateTime.now().minusMinutes(offset).isAfter(startTime * 1000);
	}

	/**
	 * 告警信息是否在 serviceCode 范围内.<br />
	 * --1). 先根据 MatchInfo 的 macId 获取 FocusMacInfo 的Service_Range 信息<br />
	 * --2). 再根据 BasicAlarmInf 的 ServiceCode 是否在返回值中；若在，返回true；否则返回 false
	 *
	 * @param info
	 * @return
	 */
	private boolean isInServiceRange(AlarmInfo info, String serviceRange) {
		return serviceRange.indexOf(info.getServiceCode().trim())!=-1;
	}

	/**
	 * 告警信息是否在 areaRange 范围内
	 *
	 * @param info
	 * @return
	 */
	private boolean isInAreaRange(String []arrays, MsgParser msgParser, AlarmInfo info, String areaRange) {
		return isInAreaRange(msgParser.codes(arrays), areaRange.split("\\,"));
	}

	private boolean isInAreaRange(String[] codes, String[] rangeArray) {
		int len = rangeArray.length;
		if(len == 1)
			return codes[0].equals(rangeArray[0].trim());
		if(len == 2)
			return codes[1].equals(rangeArray[1].trim());
		if(len == 3)
			return codes[2].equals(rangeArray[2].trim());
		return false;
	}

	private boolean isServiceTypeInRange(AlarmInfo info, Map<String , ServiceInfo> map, MatchInfo matchInfo) {
		if("1".equals(matchInfo.getServiceType())){
			return "3".equals(map.get(info.getServiceCode()).getServiceType());
		}else if("3".equals(matchInfo.getServiceType())){
			return "1".equals(map.get(info.getServiceCode()).getServiceType());
		}
		return matchInfo.getServiceType().equals(map.get(info.getServiceCode()).getServiceType());
	}

}
