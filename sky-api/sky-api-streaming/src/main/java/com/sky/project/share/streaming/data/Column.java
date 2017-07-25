package com.sky.project.share.streaming.data;

/**
 * 
 * @author zealot
 *
 */
public interface Column {

	String getColumnName();

	Class<?> getColumnType();

	int getColumnLength();
}
