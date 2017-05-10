package com.sky.projects.analysis.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sky.projects.analysis.entity.EquipLocation;
import com.sky.projects.analysis.entity.Location;
import com.sky.projects.analysis.entity.Region;

import scala.Tuple2;

public class JDBCUtil {
	public static enum DBType {
		MYSQL, ORACLE;

		private DBType() {
		}
	}

	private static final Log LOG = LogFactory.getLog(JDBCUtil.class);
	public static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	public static final String DRIVER_ORACLE = "";
	public static final String URL_MYSQL = "jdbc:mysql://[ip]:[port]/[database]?user=[user]&password=[password]";
	public static final String URL_ORACLE = "";
	public static final String SQL_EQUIP = "SELECT equipment_num,service_code,longitude,latitude FROM %s";
	public static final String SQL_FOCUS = "SELECT id,mac,phone,expiry_date FROM %s WHERE expiry_date IS NOT NULL";
	public static final String SQL_REGION_INFO = "SELECT id,points FROM %s";
	public static final String SQL_ALL_SERVICECODES = "SELECT DISTINCT service_code FROM %s";

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			LOG.error(e);
		}
		DriverManager.setLoginTimeout(30);
	}

	public static Connection createConnection(DBType type, String ip, int port, String db, String user, String pwd)
			throws SQLException {
		String url = "";
		switch (type) {
		case MYSQL:
			url = "jdbc:mysql://[ip]:[port]/[database]?user=[user]&password=[password]".replace("[ip]", ip)
					.replace("[port]", port + "").replace("[database]", db).replace("[user]", user)
					.replace("[password]", pwd);
			break;
		case ORACLE:
			url = "".replace("[ip]", ip).replace("[port]", port + "").replace("[database]", db).replace("[user]", user)
					.replace("[password]", pwd);
			break;
		default:
			LOG.info("unknown database type...");
		}
		return DriverManager.getConnection(url);
	}

	/**
	 * 获取设备信息
	 * 
	 * @param connection
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, EquipLocation> getEquipsInfo(Connection connection, String tableName)
			throws SQLException {
		Map<String, EquipLocation> res = new HashMap<>();
		PreparedStatement pst = connection.prepareStatement(
				String.format("SELECT equipment_num,service_code,longitude,latitude FROM %s", tableName), 1007, 1000);

		ResultSet rs = pst.executeQuery();
		rs.beforeFirst();
		while (rs.next()) {
			try {
				String equipNum = rs.getString("equipment_num".toUpperCase());
				String serviceCode = rs.getString("service_code".toUpperCase());
				double latitude = rs.getDouble("latitude".toUpperCase());
				double longitude = rs.getDouble("longitude".toUpperCase());
				res.put(equipNum, new EquipLocation(equipNum, serviceCode, latitude, longitude));
			} catch (Exception e) {
				LOG.error("get equip info error", e);
			}
		}
		rs.close();
		pst.close();
		return res;
	}

	public static List<String> getSerivceCodes(Connection conn, String tableName) throws SQLException {
		PreparedStatement pst = conn
				.prepareStatement(String.format("SELECT DISTINCT service_code FROM %s", new Object[] { tableName }));
		List<String> result = new ArrayList<>();
		ResultSet rs = pst.executeQuery();
		while (rs.next()) {
			result.add(rs.getString("SERVICE_CODE"));
		}
		return result;
	}

	public static Tuple2<Map<String, String>, Map<String, String>> getFocusUserList(Connection connection,
			String tableName) throws SQLException {
		Map<String, String> macAndID = new HashMap<>();
		Map<String, String> phoneAndId = new HashMap<>();
		PreparedStatement pst = connection
				.prepareStatement(String.format("SELECT id,mac,phone,expiry_date FROM %s WHERE expiry_date IS NOT NULL",
						new Object[] { tableName }), 1007, 1000);
		ResultSet rs = pst.executeQuery();

		long current = System.currentTimeMillis();
		rs.beforeFirst();

		while (rs.next()) {
			String mac = rs.getString("MAC");
			String phone = rs.getString("PHONE");
			String id = rs.getString("ID");
			long expiry_date;
			try {
				expiry_date = Long.parseLong(rs.getString("EXPIRY_DATE"));
			} catch (NumberFormatException e) {
				LOG.info(e);
				LOG.info("number parse error when get focus persons");
				continue;
			}

			if ((expiry_date < current) && (expiry_date > 0L)) {
				LOG.info("person expired,expire_date:" + expiry_date + ",current:" + current);
			} else {
				for (String str : mac.split("[;]")) {
					macAndID.put(str, id);
				}
				if (StringUtils.isNotBlank(phone)) {
					for (String str : phone.split("[;]")) {
						phoneAndId.put(str, id);
					}
				}
			}
		}
		rs.close();
		pst.close();
		return new Tuple2<>(macAndID, phoneAndId);
	}

	public static List<Region> getRegionInfo(Connection conn, String tableName) throws SQLException {
		List<Region> list = new ArrayList<>();
		PreparedStatement pst = conn
				.prepareStatement(String.format("SELECT id,points FROM %s", new Object[] { tableName }));
		ResultSet rs = pst.executeQuery();
		while (rs.next()) {
			Region r = new Region();
			List<Location> points = new ArrayList<>();
			int regionId = rs.getInt("ID");

			String info = rs.getString("POINTS");
			if ((info != null) && (!"".equals(info))) {
				String[] cons = info.split("[;]");
				for (String con : cons) {
					String[] sps = con.split("[,]");
					if ((!Objects.equals(sps, null)) && (sps.length == 2)) {
						try {
							Location l = new Location(Double.parseDouble(sps[1]), Double.parseDouble(sps[0]));
							points.add(l);
						} catch (NumberFormatException e) {
							LOG.info("ignore this point,since not number," + e);
						}
					}
				}
				r.setRegionId(regionId);
				r.setPoints(points);

				list.add(r);
			}
		}
		rs.close();
		pst.close();
		return list;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Connection conn = createConnection(DBType.MYSQL, "127.0.0.1", 3306, "s", "root", "0");
		System.out.println(conn);

		Tuple2<Map<String, String>, Map<String, String>> t = getFocusUserList(conn, "tbl_focus_person");
		System.out.println("mac=phone:" + t._1);
		System.out.println("tag=id:" + t._2);
	}
}