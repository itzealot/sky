package com.sky.projects.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sky.projects.analysis.entity.Circle;
import com.sky.projects.analysis.entity.Location;
import com.sky.projects.analysis.entity.Point;
import com.sky.projects.analysis.entity.Region;
import com.sky.projects.analysis.entity.XYLocation;
import com.sky.projects.analysis.util.TransferUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase {
	public AppTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testTransferUtil() {
		System.out.println(System.currentTimeMillis() / 1000L);
		XYLocation xyLocation = TransferUtil.bLToGauss(new Location(0.0D, 0.0D));

		System.out.println("x:" + xyLocation.getX() + ",y:" + xyLocation.getY());

		Location location2 = TransferUtil.gaussToBL(xyLocation);
		System.out
				.println("longitude:" + location2.getLongitude() * 100000.0D + ",latitude:" + location2.getLatitude());
	}

	public void testCircle() {
		System.out.println(Math.hypot(13.0D, 14.0D));
		Circle c1 = new Circle(1.0D, 1.0D, 3.0D);
		Circle c2 = new Circle(3.0D, 3.0D, 5.0D);

		int count = c1.getIntersectionCount(c2);
		System.out.println(count);

		List<Point> resultList = c1.getIntersectionPoints(c2);
		for (int i = 0; i < resultList.size(); i++) {
			System.out.println("Intersection x: " + resultList.get(i).getX());
			System.out.println("Intersection y: " + resultList.get(i).getY());
		}

		System.out.println(c1.getDisToCen(1.870828693386971D, -1.87082869338697D));
	}

	public void testRegion() {
		List<Location> list = new ArrayList<>();
		Location p1 = new Location(100.0D, 30.0D);
		Location p2 = new Location(120.0D, 37.0D);
		Location p3 = new Location(125.0D, 27.0D);
		Location p4 = new Location(143.0D, 22.0D);
		Location p5 = new Location(135.0D, 10.0D);
		Location p6 = new Location(110.0D, 15.0D);

		list.add(p1);
		list.add(p2);
		list.add(p3);
		list.add(p4);
		list.add(p5);
		list.add(p6);

		Region r = new Region();
		r.setPoints(list);

		Location px = new Location(115.0D, 25.0D);
		Location py = new Location(122.0D, 20.0D);
		Location pz = new Location(130.0D, 34.0D);
		Location pw = new Location(143.0D, 15.0D);
		Location pp = new Location(179.0D, 10.0D);

		System.out.println("px:" + r.containLocation(px));
		System.out.println("py:" + r.containLocation(py));
		System.out.println("pz:" + r.containLocation(pz));
		System.out.println("pw:" + r.containLocation(pw));
		System.out.println("pp:" + r.containLocation(pp));
	}

	public void test() {
		List<String> vals = new ArrayList<>();
		vals.add("2");
		vals.add("22");
		vals.add("6");
		vals.add("12");
		String[] arrays = new String[vals.size()];
		Arrays.sort(vals.toArray(arrays));
		
		
	}
}
