package com.surfilter.mass.tools.conf;

/**
 * ZD type
 * 
 * @author zealot
 *
 */
public enum ZDType {
	SD("1", "涉毒人员"),

	SK("2", "涉恐人员"),

	SF("3", "上访人员"),

	SW("4", "涉稳人员"),

	XS("5", "刑事人员"),

	ZT("6", "在逃人员"),

	ZSJSB("7", "肇事精神病"),

	QT("8", "其他人员"),

	;

	private String code;
	private String label;

	private ZDType(String code, String label) {
		this.code = code;
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

}
