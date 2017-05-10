package com.sky.projects.analysis.entity;

import java.io.Serializable;

/**
 * 位置信息(x, y)
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class XYLocation implements Serializable {

	private double x;
	private double y;

	public XYLocation() {
	}

	public XYLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "XYLocation [x=" + x + ",y=" + y + "]";
	}

}
