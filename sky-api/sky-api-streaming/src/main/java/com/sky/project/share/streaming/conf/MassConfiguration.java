package com.sky.project.share.streaming.conf;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 配置文件读取
 * 
 * @author zealot
 *
 */
public class MassConfiguration {

	private Configuration config;
	private static final String CONFIG_DIR_NAME = "/conf/conf.properties";

	public MassConfiguration() {
		try {
			// 正式的情况下,需要在配置目录下读取配置文件
			if (System.getProperty("sky.test") == null) {
				config = new PropertiesConfiguration(System.getProperty("user.dir") + CONFIG_DIR_NAME);
			} else { // for eclipse test
				config = new PropertiesConfiguration("conf.properties");
			}
		} catch (ConfigurationException e) {
			throw new IllegalArgumentException(String.format("load config:%s fail, please to check.", CONFIG_DIR_NAME),
					e);
		}
	}

	public String get(String key) {
		return this.config.getString(key);
	}

	public String get(String key, String defaultValue) {
		return this.config.getString(key, defaultValue);
	}

	public Integer getInt(String key) {
		return this.config.getInt(key);
	}

	public Integer getInt(String key, int defaultValue) {
		return this.config.getInt(key, defaultValue);
	}

	public String[] getArray(String key) {
		return this.config.getStringArray(key);
	}

	public String getStrings(String key) {
		String[] results = getArray(key);
		if (results == null || results.length == 0)
			return null;
		StringBuilder stringBuilder = new StringBuilder();
		if (results != null && results.length > 0) {
			for (int i = 0; i < results.length; i++) {
				if (i == results.length - 1) {
					stringBuilder.append(results[i]);
				} else {
					stringBuilder.append(results[i]).append(",");
				}
			}
		}
		return stringBuilder.toString();
	}
}
