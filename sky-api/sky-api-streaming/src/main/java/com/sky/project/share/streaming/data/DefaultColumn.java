package com.sky.project.share.streaming.data;

public class DefaultColumn implements Column {

	private String columnName; // 属性名称
	private String columnType; // 类型(String及对应的八种常见类型)
	private int columnLength; // 列对应的长度

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public Class<?> getColumnType() {
		return fetch(columnType);
	}

	private Class<?> fetch(String columnType2) {
		return null;
	}

	@Override
	public int getColumnLength() {
		return columnLength;
	}
}
