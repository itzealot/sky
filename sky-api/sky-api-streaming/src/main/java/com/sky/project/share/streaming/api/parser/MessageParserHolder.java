package com.sky.project.share.streaming.api.parser;

import java.util.HashMap;
import java.util.Map;

import com.sky.project.share.streaming.api.DataType;
import com.sky.project.share.streaming.api.MessageParser;

/**
 * MessageParserHolder
 * 
 * @author zealot
 */
public final class MessageParserHolder {

	private static Map<DataType, MessageParser> parsers = new HashMap<>();

	static {
		// Add all parsers
	}

	public static MessageParser get(DataType dataType) {
		return parsers.get(dataType);
	}

	private MessageParserHolder() {
	}
}
