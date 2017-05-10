package com.sky.projects.analysis.util;

import com.sky.projects.analysis.entity.Location;
import com.sky.projects.analysis.entity.XYLocation;

/**
 * 距离转换函数
 * 
 * @author zealot
 *
 */
public final class TransferUtil {

	public static final double DOUBLE_1 = 1.0D;

	/**
	 * 根据信号强度获取距离
	 * 
	 * @param power
	 * @return
	 */
	public static double getDistance(double power) {
		double pd0 = -26.0D;
		double r = 4.0D;
		return DOUBLE_1 * Math.pow(10.0D, (pd0 - power) / (10.0D * r));
	}

	/**
	 * 经度度转换为坐标
	 * 
	 * @param location
	 * @return
	 */
	public static XYLocation bLToGauss(Location location) {
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		int ProjNo = 0;

		double iPI = 0.0174532925199433D;
		int ZoneWide = 6;
		double a = 6378245.0D;
		double f = 0.00335232986925914D;

		ProjNo = (int) (longitude / ZoneWide);
		double longitude0 = ProjNo * ZoneWide + ZoneWide / 2;
		longitude0 *= iPI;
		double longitude1 = longitude * iPI;
		double latitude1 = latitude * iPI;
		double e2 = 2.0D * f - f * f;
		double ee = e2 / (1.0D - e2);
		double NN = a / Math.sqrt(1.0D - e2 * Math.sin(latitude1) * Math.sin(latitude1));

		double T = Math.tan(latitude1) * Math.tan(latitude1);
		double C = ee * Math.cos(latitude1) * Math.cos(latitude1);
		double A = (longitude1 - longitude0) * Math.cos(latitude1);
		double M = a * ((1.0D - e2 / 4.0D - 3.0D * e2 * e2 / 64.0D - 5.0D * e2 * e2 * e2 / 256.0D) * latitude1
				- (3.0D * e2 / 8.0D + 3.0D * e2 * e2 / 32.0D + 45.0D * e2 * e2 * e2 / 1024.0D)
						* Math.sin(2.0D * latitude1)
				+ (15.0D * e2 * e2 / 256.0D + 45.0D * e2 * e2 * e2 / 1024.0D) * Math.sin(4.0D * latitude1)
				- 35.0D * e2 * e2 * e2 / 3072.0D * Math.sin(6.0D * latitude1));

		double xval = NN * (A + (1.0D - T + C) * A * A * A / 6.0D
				+ (5.0D - 18.0D * T + T * T + 14.0D * C - 58.0D * ee) * A * A * A * A * A / 120.0D);

		double yval = M + NN * Math.tan(latitude1)
				* (A * A / 2.0D + (5.0D - T + 9.0D * C + 4.0D * C * C) * A * A * A * A / 24.0D
						+ (61.0D - 58.0D * T + T * T + 270.0D * C - 330.0D * ee) * A * A * A * A * A * A / 720.0D);

		double X0 = 1000000L * (ProjNo + 1) + 500000L;
		double Y0 = 0.0D;
		xval += X0;
		yval += Y0;
		return new XYLocation(xval, yval);
	}

	public static Location gaussToBL(XYLocation xyLocation) {
		double X = xyLocation.getX();
		double Y = xyLocation.getY();

		double iPI = 0.0174532925199433D;

		double a = 6378245.0D;
		double f = 0.00335232986925914D;

		int ZoneWide = 6;
		int ProjNo = (int) (X / 1000000.0D);
		double longitude0 = (ProjNo - 1) * ZoneWide + ZoneWide / 2;
		longitude0 *= iPI;
		double X0 = ProjNo * 1000000L + 500000L;
		double Y0 = 0.0D;
		double xval = X - X0;
		double yval = Y - Y0;
		double e2 = 2.0D * f - f * f;
		double e1 = (1.0D - Math.sqrt(1.0D - e2)) / (1.0D + Math.sqrt(1.0D - e2));
		double ee = e2 / (1.0D - e2);
		double M = yval;
		double u = M / (a * (1.0D - e2 / 4.0D - 3.0D * e2 * e2 / 64.0D - 5.0D * e2 * e2 * e2 / 256.0D));
		double fai = u + (3.0D * e1 / 2.0D - 27.0D * e1 * e1 * e1 / 32.0D) * Math.sin(2.0D * u)
				+ (21.0D * e1 * e1 / 16.0D - 55.0D * e1 * e1 * e1 * e1 / 32.0D) * Math.sin(4.0D * u)
				+ 151.0D * e1 * e1 * e1 / 96.0D * Math.sin(6.0D * u)
				+ 1097.0D * e1 * e1 * e1 * e1 / 512.0D * Math.sin(8.0D * u);

		double C = ee * Math.cos(fai) * Math.cos(fai);
		double T = Math.tan(fai) * Math.tan(fai);
		double NN = a / Math.sqrt(1.0D - e2 * Math.sin(fai) * Math.sin(fai));
		double R = a * (1.0D - e2) / Math.sqrt((1.0D - e2 * Math.sin(fai) * Math.sin(fai))
				* (1.0D - e2 * Math.sin(fai) * Math.sin(fai)) * (1.0D - e2 * Math.sin(fai) * Math.sin(fai)));

		double D = xval / NN;
		double longitude1 = longitude0 + (D - (1.0D + 2.0D * T + C) * D * D * D / 6.0D
				+ (5.0D - 2.0D * C + 28.0D * T - 3.0D * C * C + 8.0D * ee + 24.0D * T * T) * D * D * D * D * D / 120.0D)
				/ Math.cos(fai);

		double latitude1 = fai - NN * Math.tan(fai) / R
				* (D * D / 2.0D - (5.0D + 3.0D * T + 10.0D * C - 4.0D * C * C - 9.0D * ee) * D * D * D * D / 24.0D
						+ (61.0D + 90.0D * T + 298.0D * C + 45.0D * T * T - 256.0D * ee - 3.0D * C * C) * D * D * D * D
								* D * D / 720.0D);

		return new Location(longitude1 / iPI, latitude1 / iPI);
	}

	private TransferUtil() {
	}
}