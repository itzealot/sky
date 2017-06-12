package com.surfilter.mass.tools.entity;

/**
 * 视频车辆数据
 * 
 * @author zealot
 *
 */
public class VideoVehicleTrack {

	private String DEVICE_NUM; // 卡点编号
	private String DEVICE_NAME; // 卡点名称
	private String XPOINT; // 卡口经度
	private String YPOINT; // 卡口纬度
	private String AREA_CODE; // 行政区划(到区县一级)
	private String ADDR; // 卡口地址
	private String DIRECTION_NAME; // 方向名称
	private String PLATE_NUM; // 号牌号码
	private String BPLATE_NUM; // 车尾号牌号码
	private int PASS_TIME; // 过车时间

	private String BRAND; // 车辆品牌
	private String COLOR; // 车身颜色
	private String IMAGE; // 图像地址

	public VideoVehicleTrack() {
		super();
	}

	public VideoVehicleTrack(String dEVICE_NUM, String dEVICE_NAME, String xPOINT, String yPOINT, String aREA_CODE,
			String aDDR, String dIRECTION_NAME, String pLATE_NUM, String bPLATE_NUM, int pASS_TIME, String bRAND,
			String cOLOR, String iMAGE) {
		super();
		DEVICE_NUM = dEVICE_NUM;
		DEVICE_NAME = dEVICE_NAME;
		XPOINT = xPOINT;
		YPOINT = yPOINT;
		AREA_CODE = aREA_CODE;
		ADDR = aDDR;
		DIRECTION_NAME = dIRECTION_NAME;
		PLATE_NUM = pLATE_NUM;
		BPLATE_NUM = bPLATE_NUM;
		PASS_TIME = pASS_TIME;
		BRAND = bRAND;
		COLOR = cOLOR;
		IMAGE = iMAGE;
	}

	public String getDEVICE_NUM() {
		return DEVICE_NUM;
	}

	public void setDEVICE_NUM(String dEVICE_NUM) {
		DEVICE_NUM = dEVICE_NUM;
	}

	public String getDEVICE_NAME() {
		return DEVICE_NAME;
	}

	public void setDEVICE_NAME(String dEVICE_NAME) {
		DEVICE_NAME = dEVICE_NAME;
	}

	public String getXPOINT() {
		return XPOINT;
	}

	public void setXPOINT(String xPOINT) {
		XPOINT = xPOINT;
	}

	public String getYPOINT() {
		return YPOINT;
	}

	public void setYPOINT(String yPOINT) {
		YPOINT = yPOINT;
	}

	public String getAREA_CODE() {
		return AREA_CODE;
	}

	public void setAREA_CODE(String aREA_CODE) {
		AREA_CODE = aREA_CODE;
	}

	public String getADDR() {
		return ADDR;
	}

	public void setADDR(String aDDR) {
		ADDR = aDDR;
	}

	public String getDIRECTION_NAME() {
		return DIRECTION_NAME;
	}

	public void setDIRECTION_NAME(String dIRECTION_NAME) {
		DIRECTION_NAME = dIRECTION_NAME;
	}

	public String getPLATE_NUM() {
		return PLATE_NUM;
	}

	public void setPLATE_NUM(String pLATE_NUM) {
		PLATE_NUM = pLATE_NUM;
	}

	public String getBPLATE_NUM() {
		return BPLATE_NUM;
	}

	public void setBPLATE_NUM(String bPLATE_NUM) {
		BPLATE_NUM = bPLATE_NUM;
	}

	public int getPASS_TIME() {
		return PASS_TIME;
	}

	public void setPASS_TIME(int pASS_TIME) {
		PASS_TIME = pASS_TIME;
	}

	public String getBRAND() {
		return BRAND;
	}

	public void setBRAND(String bRAND) {
		BRAND = bRAND;
	}

	public String getCOLOR() {
		return COLOR;
	}

	public void setCOLOR(String cOLOR) {
		COLOR = cOLOR;
	}

	public String getIMAGE() {
		return IMAGE;
	}

	public void setIMAGE(String iMAGE) {
		IMAGE = iMAGE;
	}

}
