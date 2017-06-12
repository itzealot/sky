package com.surfilter.mass.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.surfilter.mass.tools.util.Closeables;
import com.surfilter.mass.tools.util.FileUtil;

/**
 * 卡口信息合并经纬度
 * 
 * @author zealot
 *
 */
public class KaKouInfoAddXYPoint {
	static String path = "C:/Users/Administrator/Desktop/fxj_licheng/kakou_noHead.txt";
	static String xypointPath = "C:/Users/Administrator/Desktop/fxj_licheng/kakou_xypoint.txt";

	public static void main(String[] args) {
		Map<Integer, KaKouAddress> map = map(xypointPath);

		System.out.println(map.size());

		writeKaKouInfo(path, map);
	}

	public static void writeKaKouInfo(String path, Map<Integer, KaKouAddress> map) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(path));
			String line = null;
			List<String> lines = new ArrayList<>(1500);

			StringBuffer buffer = new StringBuffer(512);
			int index = 1;
			while ((line = reader.readLine()) != null) {
				String[] arrays = line.split("\t");

				KaKouAddress addr = map.get(index);
				if (addr == null) {
					System.out.println("error line:" + line);
					continue;
				}

				for (int i = 0; i < 7; i++) {
					buffer.append(arrays[i]);
					buffer.append("\t");
				}

				buffer.append(addr.getLongtitude()); // longtitude
				buffer.append("\t");
				buffer.append(addr.getLatitude()); // latitude
				buffer.append("\t");

				for (int i = 9; i < arrays.length; i++) {
					buffer.append(arrays[i]);
					buffer.append("\t");
				}

				lines.add(buffer.deleteCharAt(buffer.length() - 1).toString());
				buffer.setLength(0);
				index++;
			}

			if (lines.size() > 0) {
				FileUtil.append(new File("C:/Users/Administrator/Desktop/fxj_licheng/kakou_info.txt"), lines);
				lines.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Closeables.close(reader);
		}
	}

	public static Map<Integer, KaKouAddress> map(String path) {
		Map<Integer, KaKouAddress> map = new HashMap<>(1500);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(path));
			String line = null;
			int index = 1;

			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}

				String[] arrays = line.split(" ");
				int len = arrays.length;

				String[] xys = arrays[len - 1].split(",");
				map.put(index, new KaKouAddress(arrays[0].trim(), sub(xys[0].trim()), sub(xys[1].trim())));

				index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Closeables.close(reader);
		}

		return map;
	}

	public static String sub(String str) {
		return str.length() > 10 ? str.substring(0, 10) : str;
	}

	public static class KaKouAddress {
		private String name;
		private String longtitude; // 经度
		private String latitude; // 纬度

		public KaKouAddress(String name, String longtitude, String latitude) {
			super();
			this.name = name;
			this.longtitude = longtitude;
			this.latitude = latitude;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLongtitude() {
			return longtitude;
		}

		public void setLongtitude(String longtitude) {
			this.longtitude = longtitude;
		}

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

	}
}
