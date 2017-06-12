package com.surfilter.mass.tools.entity;

/**
 * 卡口信息，用于缓存
 * 
 * @author zealot
 *
 */
public class KaKouInfo {

	private String id; // 卡口ID
	private String name; // 卡口名称
	private String vpnName; // VPN名称
	private String image; // 图像路径
	private String remark; // 备注信息(详细安装信息)
	private String parentId; // 父卡口ID
	private String ownArea; // 所属区
	private String longtitude; // 经度
	private String latitude; // 纬度
	private String address; // 地址
	private String ownOffice; // 所属分局
	private String time; // 时间
	private String areaCode; // 区域编码

	public KaKouInfo(String id, String name, String vpnName, String image, String remark, String parentId,
			String ownArea, String longtitude, String latitude, String address, String ownOffice, String time,
			String areaCode) {
		super();
		this.id = id;
		this.name = name;
		this.vpnName = vpnName;
		this.image = image;
		this.remark = remark;
		this.parentId = parentId;
		this.ownArea = ownArea;
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.address = address;
		this.ownOffice = ownOffice;
		this.time = time;
		this.areaCode = areaCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVpnName() {
		return vpnName;
	}

	public void setVpnName(String vpnName) {
		this.vpnName = vpnName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getOwnArea() {
		return ownArea;
	}

	public void setOwnArea(String ownArea) {
		this.ownArea = ownArea;
	}

	public String getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOwnOffice() {
		return ownOffice;
	}

	public void setOwnOffice(String ownOffice) {
		this.ownOffice = ownOffice;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	@Override
	public String toString() {
		return "KaKouInfo [id=" + id + ", name=" + name + ", vpnName=" + vpnName + ", image=" + image + ", remark="
				+ remark + ", parentId=" + parentId + ", ownArea=" + ownArea + ", longtitude=" + longtitude
				+ ", latitude=" + latitude + ", address=" + address + ", ownOffice=" + ownOffice + ", time=" + time
				+ ", areaCode=" + areaCode + "]";
	}

}
