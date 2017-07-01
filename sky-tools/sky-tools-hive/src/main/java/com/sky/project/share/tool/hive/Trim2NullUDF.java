package com.sky.project.share.tool.hive;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * Trim to null UDF
 * 
 * @author zealot
 *
 */
public class Trim2NullUDF extends UDF {

	public String evaluate(String value) {
		if (value == null) {
			return null;
		}

		String newValue = value.trim();
		return newValue.isEmpty() || "MULL".equalsIgnoreCase(newValue) || "NULL".equalsIgnoreCase(newValue) ? null
				: newValue;
	}

}
