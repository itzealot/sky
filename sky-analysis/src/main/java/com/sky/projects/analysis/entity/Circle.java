package com.sky.projects.analysis.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 圆信息 Circle(x, y, radius)
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class Circle implements Serializable {

	private double radius; // 半径
	private double x; // 圆点坐标 x
	private double y; // 圆点坐标 y

	public Circle(double x, double y, double radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	/**
	 * 获取交叉计数
	 * 
	 * @param c
	 * @return 1:相切;2:相交且不包含;否则为 0
	 */
	public int getIntersectionCount(Circle c) {
		int count = 0;
		double xlen = this.x - c.getX();
		double ylen = this.y - c.getY();
		double dis = Math.sqrt(xlen * xlen + ylen * ylen);

		double i = dis - this.radius - c.getRadius();
		double j = Math.max(this.radius, c.getRadius()) - Math.min(this.radius, c.getRadius()) - dis;

		if (i == 0.0D) {
			count = 1;
		} else if (i < 0.0D && j < 0.0D) { // 相交且不包含
			count = 2;
		}

		return count;
	}

	/**
	 * 获取圆心之间的距离
	 * 
	 * @param x0
	 * @param y0
	 * @return
	 */
	public double getDisToCen(double x0, double y0) {
		return Math.sqrt(Math.pow(this.x - x0, 2.0D) + Math.pow(this.y - y0, 2.0D));
	}

	public List<Point> getIntersectionPoints(Circle cir) {
		List<Point> points = new ArrayList<>();
		int count = getIntersectionCount(cir);
		if (count == 0) {
			return points;
		}

		double a = 2.0D * this.radius * (this.x - cir.getX());
		double b = 2.0D * this.radius * (this.y - cir.getY());
		double c = Math.pow(cir.getRadius(), 2.0D) - Math.pow(this.radius, 2.0D) - Math.pow(this.x - cir.getX(), 2.0D)
				- Math.pow(this.y - cir.getY(), 2.0D);

		double p = Math.pow(a, 2.0D) + Math.pow(b, 2.0D);
		double q = -2.0D * a * c;
		double r = Math.pow(c, 2.0D) - Math.pow(b, 2.0D);

		double cos1 = (-q + Math.sqrt(Math.pow(q, 2.0D) - 4.0D * p * r)) / (2.0D * p);
		double sin1 = Math.sqrt(1.0D - Math.pow(cos1, 2.0D));

		double cos2 = (-q - Math.sqrt(Math.pow(q, 2.0D) - 4.0D * p * r)) / (2.0D * p);
		double sin2 = Math.sqrt(1.0D - Math.pow(cos2, 2.0D));

		Point point1 = new Point();
		point1.setX(this.radius * cos1 + this.x);
		double temp1_y1 = -this.radius * sin1 + this.y;
		double temp1_y2 = this.radius * sin1 + this.y;
		if ((Math.abs(this.radius - getDisToCen(point1.getX(), temp1_y1)) < 0.1D)
				&& (Math.abs(cir.getRadius() - cir.getDisToCen(point1.getX(), temp1_y1)) < 0.1D)) {
			point1.setY(temp1_y1);
		} else {
			point1.setY(temp1_y2);
		}

		Point point2 = new Point();
		point2.setX(this.radius * cos2 + this.x);
		double temp2_y1 = -this.radius * sin2 + this.y;
		double temp2_y2 = this.radius * sin2 + this.y;

		if ((Math.abs(this.radius - getDisToCen(point2.getX(), temp2_y1)) < 0.1D)
				&& (Math.abs(cir.getRadius() - cir.getDisToCen(point2.getX(), temp2_y1)) < 0.1D)) {
			point2.setY(temp2_y1);
		} else {
			point2.setY(temp2_y2);
		}

		points.add(point1);
		points.add(point2);

		return points;
	}

	public double getRadius() {
		return this.radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
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
}