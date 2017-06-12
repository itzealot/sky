package com.surfilter.mass.tools.entity;

import com.surfilter.mass.tools.util.ZtryCatchUtil;

/**
 * 旅馆住宿信息
 * 
 * @author zealot
 *
 */
public class PeopleHotelInfo {

	protected static final String SPLITER = ZtryCatchUtil.SPLITER;

	private String name; // 姓名
	private String hotelId; // 旅客代码
	private String sex; // 性别
	private String nation; // 民族
	private String bDate; // 生日
	private String idName; // 证件类型
	private String idCode; // 证件号
	private String xzqh; // 行政区划
	private String adress; // 住址
	private String hotel; // 旅馆
	private String noRoom; // 房号
	private String inTime; // 入住时间
	private String outTime; // 退宿时间
	private String burCode; // 分局
	private String staCode; // 派出所
	private String photo; // 图片地址

	public PeopleHotelInfo(String name, String hotelId, String sex, String nation, String bDate, String idName,
			String idCode, String xzqh, String adress, String hotel, String noRoom, String inTime, String outTime,
			String burCode, String staCode, String photo) {
		super();
		this.name = name;
		this.hotelId = hotelId;
		this.sex = sex;
		this.nation = nation;
		this.bDate = bDate;
		this.idName = idName;
		this.idCode = idCode;
		this.xzqh = xzqh;
		this.adress = adress;
		this.hotel = hotel;
		this.noRoom = noRoom;
		this.inTime = inTime;
		this.outTime = outTime;
		this.burCode = burCode;
		this.staCode = staCode;
		this.photo = photo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHotelId() {
		return hotelId;
	}

	public void setHotelId(String hotelId) {
		this.hotelId = hotelId;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getbDate() {
		return bDate;
	}

	public void setbDate(String bDate) {
		this.bDate = bDate;
	}

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public String getIdCode() {
		return idCode;
	}

	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}

	public String getXzqh() {
		return xzqh;
	}

	public void setXzqh(String xzqh) {
		this.xzqh = xzqh;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getHotel() {
		return hotel;
	}

	public void setHotel(String hotel) {
		this.hotel = hotel;
	}

	public String getNoRoom() {
		return noRoom;
	}

	public void setNoRoom(String noRoom) {
		this.noRoom = noRoom;
	}

	public String getInTime() {
		return inTime;
	}

	public void setInTime(String inTime) {
		this.inTime = inTime;
	}

	public String getOutTime() {
		return outTime;
	}

	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}

	public String getBurCode() {
		return burCode;
	}

	public void setBurCode(String burCode) {
		this.burCode = burCode;
	}

	public String getStaCode() {
		return staCode;
	}

	public void setStaCode(String staCode) {
		this.staCode = staCode;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@Override
	public String toString() {
		return "PeopleHotelInfo [name=" + name + ", hotelId=" + hotelId + ", sex=" + sex + ", nation=" + nation
				+ ", bDate=" + bDate + ", idName=" + idName + ", idCode=" + idCode + ", xzqh=" + xzqh + ", adress="
				+ adress + ", hotel=" + hotel + ", noRoom=" + noRoom + ", inTime=" + inTime + ", outTime=" + outTime
				+ ", burCode=" + burCode + ", staCode=" + staCode + ", photo=" + photo + "]";
	}

	public String map2Str(StringBuffer buffer) {
		buffer.append(name).append(SPLITER).append(hotelId).append(SPLITER).append(sex).append(SPLITER).append(nation)
				.append(SPLITER).append(bDate).append(SPLITER).append(idName).append(SPLITER).append(idCode)
				.append(SPLITER).append(xzqh).append(SPLITER).append(adress).append(SPLITER).append(hotel)
				.append(SPLITER).append(noRoom).append(SPLITER).append(inTime).append(SPLITER).append(outTime)
				.append(SPLITER).append(burCode).append(SPLITER).append(staCode).append(SPLITER).append(photo);

		String result = buffer.toString();
		buffer.setLength(0);
		return result;
	}

}
