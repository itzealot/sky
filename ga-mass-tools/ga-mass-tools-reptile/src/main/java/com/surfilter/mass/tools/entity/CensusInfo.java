package com.surfilter.mass.tools.entity;

/**
 * 户籍信息
 * 
 * @author zealot
 *
 */
public class CensusInfo {

	private String CERTIFICATE; // 身份证号
	private String NAME; // 姓名
	private String SEX; // 性别
	private String NATION; // 民族
	private String CENSUS_REGISTER_ADDR; // 户籍登记地址
	private String CENSUS_OFFICE; // 户籍地县级公安机关
	private String CENSUS_POLICE; // 户籍地派出所
	private String CENSUS_ADDR; // 户籍地
	private String BIRTH_DATE; // 出生日期
	private String USED_NAME; // 曾用名
	private String AGE; // 年龄
	private String BIRTHPLACE; // 出生地
	private String CENSUS; // 籍贯
	private String HEIGHT; // 身高
	private String CAREER; // 职业
	private String BIRTH_COUNTRY; // 出生地国家
	private String BIRTH_ADDR; // 出生地详址
	private String NATIVE_COUNTRY; // 籍贯国家
	private String SERVICE_PLACE; // 服务处所
	private String VETERAN_STATUS; // 兵役状况
	private String CULTURAL_LEVEL; // 文化程度
	private String DATA_OWN_UNIT_CODE; // 数据归属单位
	private String LAST_NAME; // 姓氏
	private String PROVINCE_UNIT; // 所属省级单位
	private String MARITAL_STATUS; // 婚姻状况
	private String NAME_PINYIN; // 姓名拼音
	private String BACKGROUND; // 背景名称

	public CensusInfo(String cERTIFICATE, String nAME, String sEX, String nATION, String cENSUS_REGISTER_ADDR,
			String cENSUS_OFFICE, String cENSUS_POLICE, String cENSUS_ADDR, String bIRTH_DATE, String uSED_NAME,
			String aGE, String bIRTHPLACE, String cENSUS, String hEIGHT, String cAREER, String bIRTH_COUNTRY,
			String bIRTH_ADDR, String nATIVE_COUNTRY, String sERVICE_PLACE, String vETERAN_STATUS,
			String cULTURAL_LEVEL, String dATA_OWN_UNIT_CODE, String lAST_NAME, String pROVINCE_UNIT,
			String mARITAL_STATUS, String nAME_PINYIN, String bACKGROUND) {
		super();
		CERTIFICATE = cERTIFICATE;
		NAME = nAME;
		SEX = sEX;
		NATION = nATION;
		CENSUS_REGISTER_ADDR = cENSUS_REGISTER_ADDR;
		CENSUS_OFFICE = cENSUS_OFFICE;
		CENSUS_POLICE = cENSUS_POLICE;
		CENSUS_ADDR = cENSUS_ADDR;
		BIRTH_DATE = bIRTH_DATE;
		USED_NAME = uSED_NAME;
		AGE = aGE;
		BIRTHPLACE = bIRTHPLACE;
		CENSUS = cENSUS;
		HEIGHT = hEIGHT;
		CAREER = cAREER;
		BIRTH_COUNTRY = bIRTH_COUNTRY;
		BIRTH_ADDR = bIRTH_ADDR;
		NATIVE_COUNTRY = nATIVE_COUNTRY;
		SERVICE_PLACE = sERVICE_PLACE;
		VETERAN_STATUS = vETERAN_STATUS;
		CULTURAL_LEVEL = cULTURAL_LEVEL;
		DATA_OWN_UNIT_CODE = dATA_OWN_UNIT_CODE;
		LAST_NAME = lAST_NAME;
		PROVINCE_UNIT = pROVINCE_UNIT;
		MARITAL_STATUS = mARITAL_STATUS;
		NAME_PINYIN = nAME_PINYIN;
		BACKGROUND = bACKGROUND;
	}

	public String getCERTIFICATE() {
		return CERTIFICATE;
	}

	public void setCERTIFICATE(String cERTIFICATE) {
		CERTIFICATE = cERTIFICATE;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public String getSEX() {
		return SEX;
	}

	public void setSEX(String sEX) {
		SEX = sEX;
	}

	public String getNATION() {
		return NATION;
	}

	public void setNATION(String nATION) {
		NATION = nATION;
	}

	public String getCENSUS_REGISTER_ADDR() {
		return CENSUS_REGISTER_ADDR;
	}

	public void setCENSUS_REGISTER_ADDR(String cENSUS_REGISTER_ADDR) {
		CENSUS_REGISTER_ADDR = cENSUS_REGISTER_ADDR;
	}

	public String getCENSUS_OFFICE() {
		return CENSUS_OFFICE;
	}

	public void setCENSUS_OFFICE(String cENSUS_OFFICE) {
		CENSUS_OFFICE = cENSUS_OFFICE;
	}

	public String getCENSUS_POLICE() {
		return CENSUS_POLICE;
	}

	public void setCENSUS_POLICE(String cENSUS_POLICE) {
		CENSUS_POLICE = cENSUS_POLICE;
	}

	public String getCENSUS_ADDR() {
		return CENSUS_ADDR;
	}

	public void setCENSUS_ADDR(String cENSUS_ADDR) {
		CENSUS_ADDR = cENSUS_ADDR;
	}

	public String getBIRTH_DATE() {
		return BIRTH_DATE;
	}

	public void setBIRTH_DATE(String bIRTH_DATE) {
		BIRTH_DATE = bIRTH_DATE;
	}

	public String getUSED_NAME() {
		return USED_NAME;
	}

	public void setUSED_NAME(String uSED_NAME) {
		USED_NAME = uSED_NAME;
	}

	public String getAGE() {
		return AGE;
	}

	public void setAGE(String aGE) {
		AGE = aGE;
	}

	public String getBIRTHPLACE() {
		return BIRTHPLACE;
	}

	public void setBIRTHPLACE(String bIRTHPLACE) {
		BIRTHPLACE = bIRTHPLACE;
	}

	public String getCENSUS() {
		return CENSUS;
	}

	public void setCENSUS(String cENSUS) {
		CENSUS = cENSUS;
	}

	public String getHEIGHT() {
		return HEIGHT;
	}

	public void setHEIGHT(String hEIGHT) {
		HEIGHT = hEIGHT;
	}

	public String getCAREER() {
		return CAREER;
	}

	public void setCAREER(String cAREER) {
		CAREER = cAREER;
	}

	public String getBIRTH_COUNTRY() {
		return BIRTH_COUNTRY;
	}

	public void setBIRTH_COUNTRY(String bIRTH_COUNTRY) {
		BIRTH_COUNTRY = bIRTH_COUNTRY;
	}

	public String getBIRTH_ADDR() {
		return BIRTH_ADDR;
	}

	public void setBIRTH_ADDR(String bIRTH_ADDR) {
		BIRTH_ADDR = bIRTH_ADDR;
	}

	public String getNATIVE_COUNTRY() {
		return NATIVE_COUNTRY;
	}

	public void setNATIVE_COUNTRY(String nATIVE_COUNTRY) {
		NATIVE_COUNTRY = nATIVE_COUNTRY;
	}

	public String getSERVICE_PLACE() {
		return SERVICE_PLACE;
	}

	public void setSERVICE_PLACE(String sERVICE_PLACE) {
		SERVICE_PLACE = sERVICE_PLACE;
	}

	public String getVETERAN_STATUS() {
		return VETERAN_STATUS;
	}

	public void setVETERAN_STATUS(String vETERAN_STATUS) {
		VETERAN_STATUS = vETERAN_STATUS;
	}

	public String getCULTURAL_LEVEL() {
		return CULTURAL_LEVEL;
	}

	public void setCULTURAL_LEVEL(String cULTURAL_LEVEL) {
		CULTURAL_LEVEL = cULTURAL_LEVEL;
	}

	public String getDATA_OWN_UNIT_CODE() {
		return DATA_OWN_UNIT_CODE;
	}

	public void setDATA_OWN_UNIT_CODE(String dATA_OWN_UNIT_CODE) {
		DATA_OWN_UNIT_CODE = dATA_OWN_UNIT_CODE;
	}

	public String getLAST_NAME() {
		return LAST_NAME;
	}

	public void setLAST_NAME(String lAST_NAME) {
		LAST_NAME = lAST_NAME;
	}

	public String getPROVINCE_UNIT() {
		return PROVINCE_UNIT;
	}

	public void setPROVINCE_UNIT(String pROVINCE_UNIT) {
		PROVINCE_UNIT = pROVINCE_UNIT;
	}

	public String getMARITAL_STATUS() {
		return MARITAL_STATUS;
	}

	public void setMARITAL_STATUS(String mARITAL_STATUS) {
		MARITAL_STATUS = mARITAL_STATUS;
	}

	public String getNAME_PINYIN() {
		return NAME_PINYIN;
	}

	public void setNAME_PINYIN(String nAME_PINYIN) {
		NAME_PINYIN = nAME_PINYIN;
	}

	public String getBACKGROUND() {
		return BACKGROUND;
	}

	public void setBACKGROUND(String bACKGROUND) {
		BACKGROUND = bACKGROUND;
	}

	@Override
	public String toString() {
		return "CensusInfo [CERTIFICATE=" + CERTIFICATE + ", NAME=" + NAME + ", SEX=" + SEX + ", NATION=" + NATION
				+ ", CENSUS_REGISTER_ADDR=" + CENSUS_REGISTER_ADDR + ", CENSUS_OFFICE=" + CENSUS_OFFICE
				+ ", CENSUS_POLICE=" + CENSUS_POLICE + ", CENSUS_ADDR=" + CENSUS_ADDR + ", BIRTH_DATE=" + BIRTH_DATE
				+ ", USED_NAME=" + USED_NAME + ", AGE=" + AGE + ", BIRTHPLACE=" + BIRTHPLACE + ", CENSUS=" + CENSUS
				+ ", HEIGHT=" + HEIGHT + ", CAREER=" + CAREER + ", BIRTH_COUNTRY=" + BIRTH_COUNTRY + ", BIRTH_ADDR="
				+ BIRTH_ADDR + ", NATIVE_COUNTRY=" + NATIVE_COUNTRY + ", SERVICE_PLACE=" + SERVICE_PLACE
				+ ", VETERAN_STATUS=" + VETERAN_STATUS + ", CULTURAL_LEVEL=" + CULTURAL_LEVEL + ", DATA_OWN_UNIT_CODE="
				+ DATA_OWN_UNIT_CODE + ", LAST_NAME=" + LAST_NAME + ", PROVINCE_UNIT=" + PROVINCE_UNIT
				+ ", MARITAL_STATUS=" + MARITAL_STATUS + ", NAME_PINYIN=" + NAME_PINYIN + ", BACKGROUND=" + BACKGROUND
				+ "]";
	}

}
