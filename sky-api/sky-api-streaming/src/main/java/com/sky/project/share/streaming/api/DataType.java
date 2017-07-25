package com.sky.project.share.streaming.api;

/**
 * 接入的数据类型
 * 
 * @author zealot
 */
public enum DataType {

	SF("sf", "身份关系");

	private final String name;
	private final String remark;

	private DataType(String name, String remark) {
		this.name = name;
		this.remark = remark;
	}

	public String getName() {
		return name;
	}

	public String getRemark() {
		return remark;
	}

}
