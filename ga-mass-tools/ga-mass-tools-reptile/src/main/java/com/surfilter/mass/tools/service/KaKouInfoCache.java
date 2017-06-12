package com.surfilter.mass.tools.service;

import java.util.Map;

import com.surfilter.mass.tools.entity.KaKouInfo;
import com.surfilter.mass.tools.util.FileUtil;

/**
 * 卡口信息缓存表
 * 
 * @author zealot
 *
 */
public class KaKouInfoCache {

	private static KaKouInfoCache instance = null;
	private Map<String, KaKouInfo> map;

	public static KaKouInfoCache getInstance(String path) {
		if (instance == null) {
			synchronized (KaKouInfoCache.class) {
				if (instance == null) {
					instance = new KaKouInfoCache(path);
				}
			}
		}

		return instance;
	}

	private KaKouInfoCache(String path) {
		map = FileUtil.readKaKouInfo(path);
	}

	/**
	 * 根据 key 获取卡口信息
	 * 
	 * @param key
	 *            卡口名称
	 * @return
	 */
	public KaKouInfo get(String key) {
		return map.get(key);
	}

	public Map<String, KaKouInfo> getMap() {
		return map;
	}

}
