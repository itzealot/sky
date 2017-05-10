package com.sky.projects.message;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * 多边形区域工具类
 * 
 * @author zealot
 *
 */
public final class Polygons {

	final int TIMES = 1000;

	/***
	 * 是否在多边形 Polygon 内
	 * 
	 * @param point
	 * @param polygon
	 * @return
	 */
	public boolean checkWithJdkPolygon(Point2D.Double point, List<Point2D.Double> polygon) {
		java.awt.Polygon p = new java.awt.Polygon();

		for (Point2D.Double d : polygon) {
			int x = (int) d.x * TIMES;
			int y = (int) d.y * TIMES;
			p.addPoint(x, y);
		}

		int x = (int) point.x * TIMES;
		int y = (int) point.y * TIMES;

		return p.contains(x, y);
	}

	private Polygons() {
	}
}
