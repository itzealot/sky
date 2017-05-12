package com.surfilter.mass.conf;

/**
 * 用户类型（包括实名和虚拟）
 * 
 * @author liuchen
 */
public enum UserType {
	// MAC地址
	MAC("1020002", true, "MAC地址", "001"),
	// 手机号码
	MOBILE("1020004", true, "手机号码", "002"),
	// 姓名
	NAME("1021902", true, "姓名", "003"),

	// IMEI
	IMEI("1021901", true, "IMEI", "004"),
	// IMSI
	IMSI("1020003", true, "IMSI", "005"),

	// 身份证号码
	CERTIFICATE("1021111", true, "身份证号码", "006"),
	// 学生证
	CERTIFICATE_XSZ("1021133", true, "学生证", "007"),
	// 驾驶证
	CERTIFICATE_JSZ("1021335", true, "驾驶证", "007"),
	// 军官证
	CERTIFICATE_JGZ("1021114", true, "军官证", "007"),
	// 警官证
	CERTIFICATE_JINGZ("1021123", true, "警官证", "007"),
	// 户口簿
	CERTIFICATE_HKB("1021113", true, "户口簿", "007"),
	// 护照
	CERTIFICATE_HZ("1021414", true, "护照", "007"),
	// 台胞证
	CERTIFICATE_TBZ("1021511", true, "台胞证", "007"),
	// 回乡证
	CERTIFICATE_HXZ("1021516", true, "回乡证", "007"),
	// 社保卡
	CERTIFICATE_SBZ("1021159", true, "社保卡", "007"),
	// 士兵证
	CERTIFICATE_SB("1021233", true, "士兵证/军人证", "007"),
	// 其他证件
	CERTIFICATE_OTHER("1021990", true, "其他证件", "007"),

	// 帐号密码
	USER_NAME("1029999", true, "帐号密码", "007"),

	// QQ号码
	QQ("1030001", false, "QQ号码", "008"),
	// 微信号码
	WX("1030036", false, "微信号", "009"),
	// 陌陌号码
	MOMO("1030044", false, "陌陌号码", "010"),
	// 车牌号码
	CAR_NUM("1021353", true, "车牌号码", "011"),
	// ADSL帐号
	ADSL("1020001", true, "ADSL", "012"),
	// 网吧上网卡
	NETBAR_CARD("1020005", true, "网吧上网卡", "013"),

	// 不包括以上虚拟类型的都归为其他类型
	OTHERS("9999999", false, "OTHERS", "014");

	// 类型编码
	private String value;
	// 是否是实名类型
	private boolean real;
	// 类型说明
	private String description;
	// 优先级（值越小，优先级越高）
	private String priority;

	UserType(String value, boolean real, String description, String priority) {
		this.value = value;
		this.real = real;
		this.description = description;
		this.priority = priority;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	public boolean isReal() {
		return real;
	}

	public String getPriority() {
		return priority;
	}

}
