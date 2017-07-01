package com.sky.project.share.api.zookeeper.util;

/**
 * Path Util
 * 
 * @author zealot
 */
public final class Paths {

	private static final String EMPTY_STRING = "";
	public static final char PATH_SEPARATOR = '/';
	public static final String ROOT_PATH = "/";

	/**
	 * 获取路径的子路径
	 * 
	 * @param path
	 * @return
	 */
	public static String subPath(final String path) {
		if (path == null || EMPTY_STRING.equals(path)) {
			return null;
		}

		String source = path;
		if (path.charAt(0) == PATH_SEPARATOR) {
			source = path.substring(1);
		}

		int index = source.indexOf(PATH_SEPARATOR);

		// don't have sub path
		return index == -1 ? EMPTY_STRING : source.substring(index);
	}

	public static String getParentPath(final String path) {
		if (path == null || EMPTY_STRING.equals(path) || ROOT_PATH.equals(path)) {
			return null;
		}

		int len = path.length();

		String source = path;
		if (path.charAt(len - 1) == PATH_SEPARATOR) {
			source = path.substring(0, len - 1);
		}

		int index = source.lastIndexOf(PATH_SEPARATOR);

		return index == 0 ? ROOT_PATH : source.substring(0, index);
	}

	/**
	 * 获取路径中目录名称
	 * 
	 * @param path
	 * @return
	 */
	public static String dirName(final String path) {
		if (path == null || path.equals(EMPTY_STRING) || ROOT_PATH.equals(path)) {
			return null;
		}

		int len = path.length();
		String source = path;

		if (path.charAt(len - 1) == PATH_SEPARATOR) {
			source = path.substring(0, len - 1);
		}

		return source.substring(source.lastIndexOf(ROOT_PATH) + 1);
	}

	/**
	 * 获取路径中第一个目录名称
	 * 
	 * @param path
	 * @return
	 */
	public static String firstDirName(String path) {
		if (path == null || path.equals(EMPTY_STRING) || ROOT_PATH.equals(path)) {
			return EMPTY_STRING;
		}

		String source = path.charAt(0) == PATH_SEPARATOR ? source = path.substring(1) : path;

		int index = source.indexOf(PATH_SEPARATOR);

		return index == -1 ? path : source.substring(0, index);
	}

	/**
	 * 获取路径中第一个路径名称
	 * 
	 * @param path
	 * @return
	 */
	public static String firstPath(final String path) {
		if (path == null || path.equals(EMPTY_STRING) || ROOT_PATH.equals(path)) {
			return EMPTY_STRING;
		}

		String source = firstDirName(path);

		return source.charAt(0) == PATH_SEPARATOR ? source : ROOT_PATH + source;
	}

	/**
	 * 是否包含多个路径
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isMulityPath(String path) {
		String source = subPath(path);
		return !(EMPTY_STRING.equals(source) || ROOT_PATH.equals(source));
	}

	private Paths() {
	}
}
