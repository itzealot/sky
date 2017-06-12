package com.surfilter.mass.tools.entity;

/**
 * 网站上抓取的视频车辆数据
 * 
 * @author zealot
 *
 */
public class VehicleData {

	private String passTime; // 过车时间
	private String address; // 卡口地址
	private String carNumber; // 车牌号
	private String type; // 号牌种类
	private String color; // 车身颜色
	private String direction; // 行驶方向
	private String speed; // 行驶速度
	private String photo; // 图片地址

	public VehicleData(String passTime, String address, String carNumber, String type, String color, String direction,
			String speed, String photo) {
		super();
		this.passTime = passTime;
		this.address = address;
		this.carNumber = carNumber;
		this.type = type;
		this.color = color;
		this.direction = direction;
		this.speed = speed;
		this.photo = photo;
	}

	public String getPassTime() {
		return passTime;
	}

	public void setPassTime(String passTime) {
		this.passTime = passTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@Override
	public String toString() {
		return "VehicleData [passTime=" + passTime + ", address=" + address + ", carNumber=" + carNumber + ", type="
				+ type + ", color=" + color + ", direction=" + direction + ", speed=" + speed + ", photo=" + photo
				+ "]";
	}

}
