package com.sky.project.share.tool.hive;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * 过滤非法身份数据的UDF
 * 
 * @author zealot
 *
 */
public class TrimCertificationUDF extends UDF {

	public String evaluate(String id, String idType) {
		if (id == null || idType == null) {
			return null;
		}

		try {
			// validate idType
			if (idType.length() != 7) {
				return null;
			}
			Integer.parseInt(idType);
			return CertificationFilter.evaluate(idType, idType);
		} catch (Exception e) {
			return null;
		}
	}
}
