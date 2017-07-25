package com.sky.project.share.streaming.data;

import java.util.List;

/**
 * 数据约束
 * 
 * @author zealot
 *
 */
public interface Schema {

	List<Column> getColumns();

	/**
	 * 
	 * 
	 * @return
	 */
	int getCertCode();

	int getCertType();

	int getTelephone();

	int getMobile();

	int getImei();

	int getImsi();

	int getMac();

	int getQq();

	int getWx();

	int getEmail();

	int getOtherCerts();

	String getDataType();

	String getVersion();

	String getProvinceCode();

	String getCityCode();

	String getAreaCode();

	String getServiceCode();

	String getPoliceCode();

	short getSysSource();

	short getSource();

	String getCompanyId();

	String getRemark();

}
