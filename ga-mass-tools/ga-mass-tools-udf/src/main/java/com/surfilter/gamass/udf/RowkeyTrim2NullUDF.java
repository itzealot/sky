package com.surfilter.gamass.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * 过滤非法行健的UDF
 * 
 * @author zealot
 *
 */
public class RowkeyTrim2NullUDF extends UDF {

	/**
	 * 
	 * @param rowkey
	 * @param mode
	 *            r|c
	 * @param hasPrefix
	 *            true|false
	 * @return
	 */
	public String evaluate(String rowkey, String mode, String hasPrefix) {
		if (rowkey == null) {
			return null;
		}

		String newRowkey = rowkey.trim();
		String[] arrays = newRowkey.split("\\|");
		boolean flag = "true".equalsIgnoreCase(hasPrefix);

		if ("c".equals(mode)) { // certification
			String id = "";
			String idType = "";

			if (flag) { // 有前缀
				// 校验数组长度及前缀
				if (arrays.length != 3 || arrays[0].length() != 8) {
					return null;
				}

				id = arrays[1];
				idType = arrays[2];
			} else {
				// 校验数组长度
				if (arrays.length != 2) {
					return null;
				}
				id = arrays[0];
				idType = arrays[1];
			}

			return CertificationFilter.evaluate(id, idType) == null ? null : newRowkey;
		} else if ("r".equals(mode)) { // relation
			String idFrom = "";
			String fromType = "";
			String idTo = "";
			String toType = "";

			if (flag) { // 有前缀
				// 校验数组长度及前缀
				if (arrays.length != 5 || arrays[0].length() != 8) {
					return null;
				}

				idFrom = arrays[1];
				fromType = arrays[2];
				idTo = arrays[3];
				toType = arrays[4];
			} else {
				// 校验数组长度
				if (arrays.length != 4) {
					return null;
				}

				idFrom = arrays[0];
				fromType = arrays[1];
				idTo = arrays[2];
				toType = arrays[3];
			}

			// 校验idFrom与fromType
			String result = CertificationFilter.evaluate(idFrom, fromType);
			if (result == null) {
				return null;
			}
			// 校验idTo与toType
			return CertificationFilter.evaluate(idTo, toType) == null ? null : newRowkey;
		}
		return newRowkey;
	}

}
