package com.surfilter.mass.tools.conf;

import java.util.List;

/**
 * Car Info Json
 * 
 * @author zealot
 *
 */
public class CarInfoJson {

	private String totals;
	private String status;
	List<Entities> entities;

	public String getTotals() {
		return totals;
	}

	public void setTotals(String totals) {
		this.totals = totals;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Entities> getEntities() {
		return entities;
	}

	public void setEntities(List<Entities> entities) {
		this.entities = entities;
	}

	public static class Entities {

		private String travelTime;
		private String kkid;
		private String location;
		private String plateNumber;
		private String region;
		private String plateType;
		private String plateTypeStr;
		private String plateColor;
		private String plateColorStr;
		private String vehicleBrand;
		private String vehicleBrandStr;
		private String vehicleColor;
		private String vehicleColorStr; // color
		private String travelOrientation;
		private String travelOrientationStr; // dirction
		private String speed;
		private String imgUrl;

		private CachedBayonet cachedBayonet;

		public String getTravelTime() {
			return travelTime;
		}

		public void setTravelTime(String travelTime) {
			this.travelTime = travelTime;
		}

		public String getKkid() {
			return kkid;
		}

		public void setKkid(String kkid) {
			this.kkid = kkid;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getPlateNumber() {
			return plateNumber;
		}

		public void setPlateNumber(String plateNumber) {
			this.plateNumber = plateNumber;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getPlateType() {
			return plateType;
		}

		public void setPlateType(String plateType) {
			this.plateType = plateType;
		}

		public String getPlateTypeStr() {
			return plateTypeStr;
		}

		public void setPlateTypeStr(String plateTypeStr) {
			this.plateTypeStr = plateTypeStr;
		}

		public String getPlateColor() {
			return plateColor;
		}

		public void setPlateColor(String plateColor) {
			this.plateColor = plateColor;
		}

		public String getPlateColorStr() {
			return plateColorStr;
		}

		public void setPlateColorStr(String plateColorStr) {
			this.plateColorStr = plateColorStr;
		}

		public String getVehicleBrand() {
			return vehicleBrand;
		}

		public void setVehicleBrand(String vehicleBrand) {
			this.vehicleBrand = vehicleBrand;
		}

		public String getVehicleBrandStr() {
			return vehicleBrandStr;
		}

		public void setVehicleBrandStr(String vehicleBrandStr) {
			this.vehicleBrandStr = vehicleBrandStr;
		}

		public String getVehicleColor() {
			return vehicleColor;
		}

		public void setVehicleColor(String vehicleColor) {
			this.vehicleColor = vehicleColor;
		}

		public String getVehicleColorStr() {
			return vehicleColorStr;
		}

		public void setVehicleColorStr(String vehicleColorStr) {
			this.vehicleColorStr = vehicleColorStr;
		}

		public String getTravelOrientation() {
			return travelOrientation;
		}

		public void setTravelOrientation(String travelOrientation) {
			this.travelOrientation = travelOrientation;
		}

		public String getTravelOrientationStr() {
			return travelOrientationStr;
		}

		public void setTravelOrientationStr(String travelOrientationStr) {
			this.travelOrientationStr = travelOrientationStr;
		}

		public String getSpeed() {
			return speed;
		}

		public void setSpeed(String speed) {
			this.speed = speed;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public CachedBayonet getCachedBayonet() {
			return cachedBayonet;
		}

		public void setCachedBayonet(CachedBayonet cachedBayonet) {
			this.cachedBayonet = cachedBayonet;
		}

		@Override
		public String toString() {
			return "Entities [travelTime=" + travelTime + ",kkid=" + kkid + ",location=" + location + ",plateNumber="
					+ plateNumber + "]";
		}

	}

	public static class CachedBayonet {

		private String kkid;
		private String kkmc;
		private String x;
		private String y;
		private String ssjgid;
		private String jgmc; // 所属区域
		private String sbzt;

		private List<LocationList> locationLists;

		public String getKkid() {
			return kkid;
		}

		public void setKkid(String kkid) {
			this.kkid = kkid;
		}

		public String getKkmc() {
			return kkmc;
		}

		public void setKkmc(String kkmc) {
			this.kkmc = kkmc;
		}

		public String getX() {
			return x;
		}

		public void setX(String x) {
			this.x = x;
		}

		public String getY() {
			return y;
		}

		public void setY(String y) {
			this.y = y;
		}

		public String getSsjgid() {
			return ssjgid;
		}

		public void setSsjgid(String ssjgid) {
			this.ssjgid = ssjgid;
		}

		public String getJgmc() {
			return jgmc;
		}

		public void setJgmc(String jgmc) {
			this.jgmc = jgmc;
		}

		public String getSbzt() {
			return sbzt;
		}

		public void setSbzt(String sbzt) {
			this.sbzt = sbzt;
		}

		public List<LocationList> getLocationLists() {
			return locationLists;
		}

		public void setLocationLists(List<LocationList> locationLists) {
			this.locationLists = locationLists;
		}

	}

	static class LocationList {
		private String ddid;
		private String ddmc;
		private String ddfx;
		private String kkid; // ka kou Id
		private String ddcd;

		public String getDdid() {
			return ddid;
		}

		public void setDdid(String ddid) {
			this.ddid = ddid;
		}

		public String getDdmc() {
			return ddmc;
		}

		public void setDdmc(String ddmc) {
			this.ddmc = ddmc;
		}

		public String getDdfx() {
			return ddfx;
		}

		public void setDdfx(String ddfx) {
			this.ddfx = ddfx;
		}

		public String getKkid() {
			return kkid;
		}

		public void setKkid(String kkid) {
			this.kkid = kkid;
		}

		public String getDdcd() {
			return ddcd;
		}

		public void setDdcd(String ddcd) {
			this.ddcd = ddcd;
		}

	}
}
