package com.surfilter.mass.services;

import java.util.Map;

import com.google.common.collect.Maps;
import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.services.parser.FjMsgParser;
import com.surfilter.mass.services.parser.SjMsgParser;
import com.surfilter.mass.services.parser.TzMsgParser;
import com.surfilter.mass.services.parser.WlMsgParser;
import com.surfilter.mass.services.parser.XwMsgParser;

/**
 * 消息解析器
 * 
 * @author zealot
 *
 */
public final class MsgParserHolder {

	private final Map<Integer, MsgParser> msgParserMap = Maps.newHashMapWithExpectedSize(5);

	private static final WlMsgParser WL_PARSER = new WlMsgParser();
	private static final FjMsgParser FJ_PARSER = new FjMsgParser();
	private static final XwMsgParser XW_PARSER = new XwMsgParser();
	private static final SjMsgParser SJ_PARSER = new SjMsgParser();
	private static final TzMsgParser TZ_PARSER = new TzMsgParser();

	public static volatile MsgParserHolder instance = null;

	private MassConfiguration conf;

	public static MsgParserHolder getInstance(MassConfiguration conf) {
		if (instance == null) {
			synchronized (MsgParserHolder.class) {
				if (instance == null) {
					instance = new MsgParserHolder(conf);
				}
			}
		}

		return instance;
	}

	private MsgParserHolder(MassConfiguration conf) {
		this.conf = conf;
	}

	public MsgParserHolder addWlParser() {
		msgParserMap.put(conf.getInt(ImcaptureConsts.WL_PARSER_KEY), WL_PARSER);
		return this;
	}

	public MsgParserHolder addFjParser() {
		msgParserMap.put(conf.getInt(ImcaptureConsts.FJ_PARSER_KEY), FJ_PARSER);
		return this;
	}

	public MsgParserHolder addXwParer() {
		msgParserMap.put(conf.getInt(ImcaptureConsts.XW_PARSER_KEY), XW_PARSER);
		return this;
	}

	public MsgParserHolder addSjParser() {
		msgParserMap.put(conf.getInt(ImcaptureConsts.SJ_PARSER_KEY), SJ_PARSER);
		return this;
	}

	public MsgParserHolder addTzParser() {
		msgParserMap.put(conf.getInt(ImcaptureConsts.TZ_PARSER_KEY), TZ_PARSER);
		return this;
	}

	public MsgParser getMsgParser(Integer columnNum) {
		return msgParserMap.get(columnNum);
	}

}
