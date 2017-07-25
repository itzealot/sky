package com.sky.project.share.streaming.api.converter.impl;

import com.sky.project.share.streaming.api.converter.StringArrayMessageConverter;

/**
 * SfConverter
 * 
 * @author zealot
 */
public class SfConverter implements StringArrayMessageConverter {

	@Override
	public String[] convert(String line) {
		return null;
	}

	@Override
	public boolean validate(String[] message) {
		return false;
	}

	@Override
	public String[] update(String[] message) {
		return null;
	}

	@Override
	public String getStreamingType() {
		return null;
	}

}
