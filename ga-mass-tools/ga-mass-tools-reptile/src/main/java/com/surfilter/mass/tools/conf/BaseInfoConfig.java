package com.surfilter.mass.tools.conf;

import static com.surfilter.mass.tools.util.ValidateUtil.validateProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础信息
 * 
 * @author zealot
 *
 */
public class BaseInfoConfig {
	static final Logger LOG = LoggerFactory.getLogger(BaseInfoConfig.class);

	private String provinceCode; // 所属省份编码
	private String cityCode; // 所属城市编码
	private String areaCode; // 所属分区
	private String policeCode; // 派出所代码
	private String sysSource; // 大类
	private String source; // 小类
	private String creater; // 创建者

	/** ztry property */
	public static final String BASE_PROVINCE_CODE = "base.provinceCode";
	public static final String BASE_CITY_CODE = "base.cityCode";
	public static final String BASE_AREA_CODE = "base.areaCode";
	public static final String BASE_POLICE_CODE = "base.policeCode";
	public static final String BASE_SYS_SOURCE = "base.sysSource";
	public static final String BASE_SOURCE = "base.source";
	public static final String BASE_CREATER = "base.creater";

	public BaseInfoConfig(MassConfiguration conf) {
		init(conf);
	}

	private void init(MassConfiguration conf) {
		this.provinceCode = validateProperty(conf.get(BASE_PROVINCE_CODE), BASE_PROVINCE_CODE);
		this.cityCode = validateProperty(conf.get(BASE_CITY_CODE), BASE_CITY_CODE);
		this.areaCode = validateProperty(conf.get(BASE_AREA_CODE), BASE_AREA_CODE);
		this.policeCode = conf.get(BASE_POLICE_CODE);
		this.sysSource = validateProperty(conf.get(BASE_SYS_SOURCE), BASE_SYS_SOURCE);
		this.source = validateProperty(conf.get(BASE_SOURCE), BASE_SOURCE);
		this.creater = validateProperty(conf.get(BASE_CREATER), BASE_CREATER);
		validateCode();
	}

	private void validateCode() {
		if (this.provinceCode.length() != 6) {
			throw new IllegalArgumentException("error setting for provinceCode config");
		}

		if (this.cityCode.length() != 6) {
			throw new IllegalArgumentException("error setting for cityCode config");
		}

		if (this.areaCode.length() != 6) {
			throw new IllegalArgumentException("error setting for areaCode config");
		}

		if (!this.provinceCode.equals(this.areaCode.substring(0, 2) + "0000")) {
			throw new IllegalArgumentException("error setting for provinceCode,areaCode config");
		}

		if (!this.cityCode.equals(this.areaCode.substring(0, 4) + "00")) {
			throw new IllegalArgumentException("error setting for cityCode,areaCode config");
		}
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getPoliceCode() {
		return policeCode;
	}

	public void setPoliceCode(String policeCode) {
		this.policeCode = policeCode;
	}

	public String getSysSource() {
		return sysSource;
	}

	public void setSysSource(String sysSource) {
		this.sysSource = sysSource;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

}
