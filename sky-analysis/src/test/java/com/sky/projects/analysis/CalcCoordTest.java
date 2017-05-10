package com.sky.projects.analysis;

import java.util.ArrayList;
import java.util.List;

import com.sky.projects.analysis.entity.Circle;
import com.sky.projects.analysis.entity.Location;
import com.sky.projects.analysis.service.CalcCoord;

import junit.framework.TestCase;

public class CalcCoordTest extends TestCase {

	public void testCalcCoord() {
		List<Circle> circles = new ArrayList<>();
		Circle circle1 = CalcCoord.toCircle(113.944267630577D, 23.080837209382D, -32.0D);
		Circle circle2 = CalcCoord.toCircle(113.6777179D, 22.928552D, -26.0D);
		Circle circle3 = CalcCoord.toCircle(113.6694342011D, 22.946511248099998D, -34.0D);
		circles.add(circle1);
		circles.add(circle2);
		circles.add(circle3);
		CalcCoord.sort(circles);
		for (Circle circle : circles) {
			System.out.println("x: " + circle.getX() + ",y: " + circle.getY() + ",radius: " + circle.getRadius());
		}
		Location location = CalcCoord.toLocation(circles);
		System.out.println("x: " + location.getLongitude() + ",y: " + location.getLatitude());
	}
}