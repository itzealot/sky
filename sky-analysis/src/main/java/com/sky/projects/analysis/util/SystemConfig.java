package com.sky.projects.analysis.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SystemConfig {
	private static final String BUNDLE_NAME = "DataAnalysis";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key).trim();
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}