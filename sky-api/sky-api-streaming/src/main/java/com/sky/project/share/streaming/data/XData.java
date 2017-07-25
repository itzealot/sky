package com.sky.project.share.streaming.data;

/**
 * XData
 * 
 * @author zealot
 *
 */
public final class XData {

	/** 数据约束，用于生成 json 信息，使用动态代理根据约束创建出 json 信息 */
	private transient Schema schema;

	public XData(Schema schema) {
		this.schema = schema;
	}

	// 证件信息
	private String certCode;
	private String certType;

	// 座机
	private String telephone;

	/** 手机特征信息 */
	private String mobile;
	private String imei;
	private String imsi;
	private String mac;

	/** 虚拟账号信息 */
	private String qq;
	private String wx;
	private String email;

	/** 其他身份信息，数据格式:id|idType，多个使用逗号分割 */
	private String otherCerts;

	// 数据所属类型
	private String dataType;

	// 数据所属版本信息
	private String version;

	/** 区域信息，对应的省编码、市编码、区编码、场所编码、派出所编码，可以用于统计 */
	private String provinceCode;
	private String cityCode;
	private String areaCode;
	private String serviceCode;
	private String policeCode;

	/** 对应数据的系统来源、数据来源、厂商编码 */
	private short sysSource;
	private short source;
	private String companyId;

	// 备注信息
	private String remark;

	/** 数据json格式 */
	private String json;

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public String getCertCode() {
		return certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWx() {
		return wx;
	}

	public void setWx(String wx) {
		this.wx = wx;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOtherCerts() {
		return otherCerts;
	}

	public void setOtherCerts(String otherCerts) {
		this.otherCerts = otherCerts;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getPoliceCode() {
		return policeCode;
	}

	public void setPoliceCode(String policeCode) {
		this.policeCode = policeCode;
	}

	public short getSysSource() {
		return sysSource;
	}

	public void setSysSource(short sysSource) {
		this.sysSource = sysSource;
	}

	public short getSource() {
		return source;
	}

	public void setSource(short source) {
		this.source = source;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
