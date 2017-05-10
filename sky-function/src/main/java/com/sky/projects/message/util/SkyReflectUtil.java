package com.sky.projects.message.util;

/**
 * reflect util
 * 
 * @author zealot
 *
 */
public final class SkyReflectUtil {

	@SuppressWarnings("unchecked")
	public static <T> Class<T> reflect(String className) throws ClassNotFoundException {
		return (Class<T>) Class.forName(className);
	}

	private SkyReflectUtil() {
	}

}
