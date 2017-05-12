package com.surfilter.mass.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.surfilter.mass.ImcaptureContext;
import com.surfilter.mass.entity.AlarmInfo;

/**
 * 数据匹配抽象类
 * 
 * @author hapuer
 *
 */
public abstract class AbstractDataMatcher implements DataMatcher {

	protected Logger LOG = LoggerFactory.getLogger(this.getClass());
	protected ImcaptureContext context;

	protected List<String> combineKeys;

	public AbstractDataMatcher(ImcaptureContext context) {
		this.context = context;
	}

	@Override
	public List<AlarmInfo> match(List<String> datas) {
		List<AlarmInfo> results = Lists.newArrayList();

		for (String msg : datas) {
			String[] splits = msg.split(SEPERATOR);
			if (splits.length > 0) {
				for (String row : splits) {
					List<AlarmInfo> mresult = doMatch(row);

					if (mresult != null && mresult.size() > 0) {
						appendResult(results, mresult);
					}
					mresult.clear();
				}
			}
		}

		return results;
	}

	/**
	 * appendLists -merge-> orginal
	 * 
	 * @param orginal
	 * @param appendLists
	 */
	private void appendResult(List<AlarmInfo> orginal, List<AlarmInfo> appendLists) {
		for (int i = 0; i < appendLists.size(); i++) {
			orginal.add(appendLists.get(i));
		}
	}

	/**
	 * 文本高速匹配单条记录，返回批量结果
	 * 
	 * @param row
	 * @return
	 */
	public abstract List<AlarmInfo> doMatch(String row);

}
