package com.sky.projects.analysis.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 区域信息(多个 Location组成，保存 minX,maxX,minY,maxY)
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public class Region implements Serializable {

	private int regionId;
	private List<Location> points;
	private double minX = 181.0D; // 最小纬度
	private double maxX = -1.0D; // 最大纬度

	private double minY = 181.0D; // 最小经度
	private double maxY = -1.0D; // 最大经度

	public void setPoints(List<Location> points) {
		this.points = points;
		for (Location l : points) {
			this.minX = Math.min(this.minX, l.getLatitude());
			this.maxX = Math.max(this.maxX, l.getLatitude());

			this.minY = Math.min(this.minY, l.getLongitude());
			this.maxY = Math.max(this.maxY, l.getLongitude());
		}
		points.add(points.get(0));
	}

	public List<Location> getPoints() {
		return this.points;
	}

	public boolean containLocation(Location o) {
		boolean result = bondingBOX(o);
		if (!result) {
			return result;
		}
		double sum = 0.0D;

		int i = 0;
		for (int j = i + 1; j < this.points.size(); j++) {
			Location src = this.points.get(i);
			Location des = this.points.get(j);

			double ab = (des.getLatitude() - o.getLatitude()) * (src.getLatitude() - o.getLatitude())
					+ (des.getLongitude() - o.getLongitude()) * (src.getLongitude() - o.getLongitude());

			double size = Math
					.sqrt(Math.pow(o.getLatitude() - src.getLatitude(), 2.0D)
							+ Math.pow(o.getLongitude() - src.getLongitude(), 2.0D))
					* Math.sqrt(Math.pow(des.getLatitude() - o.getLatitude(), 2.0D)
							+ Math.pow(des.getLongitude() - o.getLongitude(), 2.0D));

			double slope = Math.acos(ab / size) * 180.0D / 3.141592653589793D;
			sum += slope;
			i++;
		}

		return Math.abs(sum - 360.0D) <= 1.0D;
	}

	/**
	 * 区域是否在边界范围内[(minX, maxX), (minY, maxY)]
	 * 
	 * @param o
	 * @return
	 */
	public boolean bondingBOX(Location o) {
		return !((o.getLatitude() < this.minX) || (o.getLatitude() > this.maxX) || (o.getLongitude() < this.minY)
				|| (o.getLongitude() > this.maxY));
	}

	public int getRegionId() {
		return this.regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public String toString() {
		return "Region{regionId=" + this.regionId + ", points=" + this.points + ", minX=" + this.minX + ", maxX="
				+ this.maxX + ", minY=" + this.minY + ", maxY=" + this.maxY + '}';
	}
}
