package com.sky.project.share.streaming.api.parser.impl;

import java.util.List;

import com.sky.project.share.streaming.api.MessageParser;
import com.sky.project.share.streaming.api.Persistent;

public class SfMessageParser implements MessageParser {

	@Override
	public String[] getValues(String[] arrays) {
		return arrays;
	}

	@Override
	public List<String[]> getIds(String[] arrays) {
		return null;
	}

	@Override
	public String[] getCodes(String[] arrays) {
		return null;
	}

	@Override
	public String getServiceCode(String[] arrays) {
		return null;
	}

	@Override
	public String[] getSources(String[] arrays) {
		return null;
	}

	@Override
	public String[] getTimes(String[] arrays) {
		return null;
	}

	@Override
	public Persistent getPersistent() {
		return null;
	}

}
