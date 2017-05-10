package com.sky.projects.message.conf;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Config
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class SkyConf implements Serializable {
	private final Map<String, String> map = Maps.newHashMap();

	public SkyConf() {
		super();
	}

	public String get(String key) {
		return get(key, null);
	}

	public String get(String key, String defaultValue) {
		String val = map.get(key);
		return val == null ? defaultValue : val;
	}

	public SkyConf set(String key, String value) {
		map.put(key, value);
		return this;
	}
}
