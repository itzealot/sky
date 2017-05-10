package com.sky.projects.analysis.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import com.sky.projects.analysis.entity.Circle;
import com.sky.projects.analysis.entity.Location;
import com.sky.projects.analysis.entity.Point;
import com.sky.projects.analysis.entity.XYLocation;
import com.sky.projects.analysis.util.TransferUtil;

/**
 * 
 * 
 * @author zealot
 *
 */
public class CalcCoord {

	/**
	 * 经纬度转换为圆
	 * 
	 * @param longitude
	 * @param latitude
	 * @param power
	 * @return
	 */
	public static Circle toCircle(double longitude, double latitude, double power) {
		XYLocation xyLocation = TransferUtil.bLToGauss(new Location(longitude, latitude));
		return new Circle(xyLocation.getX(), xyLocation.getY(), TransferUtil.getDistance(power));
	}

	public static Location toLocation(List<Circle> circles) {
		sort(circles);
		List<Point> resultPoints = new ArrayList<>();
		if (circles.size() == 2) { // 区域中只有两个点
			List<Point> points = circles.get(0).getIntersectionPoints(circles.get(1));

			if (points.size() == 0) {
				return TransferUtil.gaussToBL(new XYLocation(circles.get(0).getX(), circles.get(0).getY()));
			}
			if (points.size() == 1) {
				resultPoints.add(points.get(0));
			}
			if (points.size() == 2) {
				for (Point point : points) {
					resultPoints.add(point);
				}
			}
		}

		if (circles.size() == 1) { // 区域中只有一个点
			return TransferUtil.gaussToBL(new XYLocation(circles.get(0).getX(), circles.get(0).getY()));
		}

		if (circles.size() == 3) { // 区域中只有三个点
			for (int i = 0; i < circles.size(); i++) {
				if (i == circles.size() - 1) {
					break;
				}
				List<Point> points = circles.get(i).getIntersectionPoints(circles.get(i + 1));
				for (Point point : points) {
					if (i == circles.size() - 2) {
						if (circles.get(0).getDisToCen(point.getX(), point.getY()) <= circles.get(0).getRadius()) {
							resultPoints.add(point);
						}
					} else if (circles.get(i + 2).getDisToCen(point.getX(), point.getY()) <= circles.get(i + 2)
							.getRadius()) {
						resultPoints.add(point);
					}
				}
			}
		}

		if (circles.size() > 3) { // 区域中大于三个点
			for (int i = 0; i < circles.size(); i++) {
				if (i == circles.size() - 2) {
					break;
				}
				List<Point> points = circles.get(i).getIntersectionPoints(circles.get(i + 1));
				for (Point point : points) {
					if (circles.get(i + 2).getDisToCen(point.getX(), point.getY()) <= circles.get(i + 2).getRadius()) {
						resultPoints.add(point);
					}
				}
			}
		}

		if (resultPoints.size() == 0) {
			for (Circle circle : circles) {
				resultPoints.add(new Point(circle.getX(), circle.getY()));
			}
		}

		Point point = getAverage(resultPoints);
		XYLocation xyLocation = new XYLocation();
		xyLocation.setX(point.getX());
		xyLocation.setY(point.getY());
		return TransferUtil.gaussToBL(xyLocation);
	}

	private static Point getAverage(List<Point> points) {
		double sumX = 0.0D;
		double sumY = 0.0D;
		for (Point point : points) {
			sumX += point.getX();
			sumY += point.getY();
		}
		double averageX = sumX / points.size();
		double averageY = sumY / points.size();
		return new Point(averageX, averageY);
	}

	/**
	 * 按照圆半径升序.<br />
	 * 如果要按照升序排序：则o1小于o2，返回-1(负数)，相等返回0，o1大于o2返回1(正数).<br />
	 * 如果要按照降序排序：则o1小于o2，返回1(正数)，相等返回0，o1大于o2返回-1(负数).<br />
	 * 
	 * @param circles
	 */
	public static void sort(List<Circle> circles) {
		Circle[] a = new Circle[circles.size()];
		Comparator<Circle> comparator = new Comparator<Circle>() {
			@Override
			public int compare(Circle o1, Circle o2) {
				if (o1.getRadius() < o2.getRadius()) {
					return -1;
				}
				if (o1.getRadius() == o2.getRadius()) {
					return 0;
				}
				return 1;
			}
		};
		Arrays.sort(circles.toArray(a), comparator);
		ListIterator<Circle> iterator = circles.listIterator();
		for (Circle e : a) {
			iterator.next();
			iterator.set(e);
		}
	}
}