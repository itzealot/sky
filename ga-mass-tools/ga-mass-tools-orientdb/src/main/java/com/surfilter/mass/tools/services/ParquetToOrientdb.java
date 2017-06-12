package com.surfilter.mass.tools.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.entity.Relation;
import com.surfilter.mass.tools.utils.Constants;
import com.surfilter.mass.tools.utils.DataValid;
import com.surfilter.mass.tools.utils.DateUtil;

/**
 * Created by wengchangwen on 2017/2/21.
 */

public class ParquetToOrientdb {

	/**
	 * 
	 */
	private final static Logger logger = LoggerFactory.getLogger(ParquetToOrientdb.class);
	// private OrientDaoSingleton orientObj;

	public ParquetToOrientdb() {
		// orientObj =
		// OrientDaoSingleton.getInstance("odb:jdbc:orient:remote:10.10.10.104/gacenter");
	}
	
	
	public void fileOrientdb(String filepath){
		if (StringUtils.isEmpty(filepath)){
			logger.error("file2Orientdb filepath is empty!");
			return;
		}
		
		
		
		
	}
	

	// 对数据做基本校验
	public boolean validRelation(Relation r) {
		// valid phone.
		if (r.getForm_type().equals(Constants.PROTOCOL_TYPE_PHONE)) {
			String phone = DataValid.validPhone(r.getId_from());
			if (phone.equals("")) {
				return false;
			}
			r.setId_from(phone);
		}
		if (r.getTo_type().equals(Constants.PROTOCOL_TYPE_PHONE)) {
			String phone = DataValid.validPhone(r.getId_to());
			if (phone.equals("")) {
				return false;
			}
			r.setId_to(phone);
		}

		// valid mac
		if (r.getForm_type().equals(Constants.PROTOCOL_TYPE_MAC)) {
			// source子类为1是围栏
			String mac = DataValid.validMac(r.getId_from(), r.getSource().equals("1") ? 1 : 0);
			if (mac.equals("")) {
				return false;
			}
			r.setId_from(mac);
		}
		if (r.getTo_type().equals(Constants.PROTOCOL_TYPE_MAC)) {
			String mac = DataValid.validMac(r.getId_to(), r.getSource().equals("1") ? 1 : 0);
			if (mac.equals("")) {
				return false;
			}
			r.setId_to(mac);
		}

		// valid imei
		if (r.getForm_type().equals(Constants.PROTOCOL_TYPE_IMEI)) {
			// source子类为1是围栏
			String imei = DataValid.validImei(r.getId_from());
			if (imei.equals("")) {
				return false;
			}
			r.setId_from(imei);
		}
		if (r.getTo_type().equals(Constants.PROTOCOL_TYPE_IMEI)) {
			String imei = DataValid.validImei(r.getId_to());
			if (imei.equals("")) {
				return false;
			}
			r.setId_to(imei);
		}

		// valid imsi
		if (r.getForm_type().equals(Constants.PROTOCOL_TYPE_IMSI)) {
			// source子类为1是围栏
			String imsi = DataValid.validImsi(r.getId_from());
			if (imsi.equals("")) {
				return false;
			}
			r.setId_from(imsi);
		}
		if (r.getTo_type().equals(Constants.PROTOCOL_TYPE_IMSI)) {
			String imsi = DataValid.validImsi(r.getId_to());
			if (imsi.equals("")) {
				return false;
			}
			r.setId_to(imsi);
		}

		// valid qqwx
		if (r.getForm_type().equals(Constants.PROTOCOL_TYPE_QQ)
				|| r.getForm_type().equals(Constants.PROTOCOL_TYPE_WX)) {
			// source子类为1是围栏
			String qqwx = DataValid.validQqwx(r.getId_from());
			if (qqwx.equals("")) {
				return false;
			}
			r.setId_from(qqwx);
		}
		if (r.getTo_type().equals(Constants.PROTOCOL_TYPE_QQ) || r.getTo_type().equals(Constants.PROTOCOL_TYPE_WX)) {
			String qqwx = DataValid.validQqwx(r.getId_to());
			if (qqwx.equals("")) {
				return false;
			}
			r.setId_to(qqwx);
		}

		// valid cert
		if (r.getForm_type().equals(Constants.PROTOCOL_TYPE_CERT)) {
			// source子类为1是围栏
			String cert = DataValid.validCert(r.getId_from());
			if (cert.equals("")) {
				return false;
			}
			r.setId_from(cert);
		}
		if (r.getTo_type().equals(Constants.PROTOCOL_TYPE_CERT)) {
			String cert = DataValid.validCert(r.getId_to());
			if (cert.equals("")) {
				return false;
			}
			r.setId_to(cert);
		}

		// 如果是IP,则不入图
		if (DataValid.validIp(r.getId_from()).isEmpty() || DataValid.validIp(r.getId_to()).isEmpty()
				|| StringUtils.length(r.getId_from()) < 5 || StringUtils.length(r.getId_from()) > 25
				|| StringUtils.length(r.getId_to()) < 5 || StringUtils.length(r.getId_to()) > 25) {
			return false;
		}

		return true;
	}

	// 采用ODB SQL的方式入库
	public String addVertex(String c_id, Connection conn, String name) {
		String fromRid = "";

		Statement statementSelect = null;
		Statement statementInsert = null;
		Statement statementUpdate = null;
		ResultSet resultSet = null;
		String errSql = ""; // 记录异常的SQL
		try {
			statementSelect = conn.createStatement();

			// 查询SQL
			String selectSql = "select @rid, name from certification where c_id='" + c_id + "';";
			errSql = selectSql;

			if (statementSelect != null) {

				resultSet = statementSelect.executeQuery(selectSql);
				// 顶点是否存在，存在则获取RID
				if (resultSet != null && resultSet.next()) {
					// FROM的边RID集合
					fromRid = resultSet.getString(1);

					if (!name.isEmpty()) {
						String tmp_name = resultSet.getString(2);
						tmp_name = tmp_name + " " + name;
						if (StringUtils.length(tmp_name) < 100) {
							String updateSql = "update " + fromRid + " set name = '" + tmp_name + "', update_time" + "="
									+ System.currentTimeMillis();
							errSql = updateSql;
							statementUpdate = conn.createStatement();
							statementUpdate.executeQuery(updateSql);
						}
					}
					// logger.info("weng addVertex selectSql: " + selectSql + ",
					// fromRid: " + fromRid);
				} else {
					// 创建SQL
					String insertSql = "insert into certification(c_id, name, create_time, update_time) values('" + c_id
							+ "', '" + name + "', " + System.currentTimeMillis() + ", " + System.currentTimeMillis()
							+ ");";
					errSql = insertSql;

					statementInsert = conn.createStatement();
					resultSet.close();
					resultSet = statementInsert.executeQuery(insertSql);

					if (resultSet != null && resultSet.next()) {
						fromRid = resultSet.getRowId("@rid").toString();
					}
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.info("addVertex exception: " + e + ", sql: " + errSql);
		} finally {
			try {
				if (statementSelect != null)
					statementSelect.close();
			} catch (SQLException ignore) {
			}
			try {
				if (statementInsert != null)
					statementInsert.close();
			} catch (SQLException ignore) {
			}
			try {
				if (statementUpdate != null)
					statementUpdate.close();
			} catch (SQLException ignore) {
			}
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException ignore) {
			}
		}

		return fromRid;
	}

	public void addVertexEdgeNoTx(Relation r, Connection conn) {
		String from = "";
		String to = "";

		if (r.getTo_type().equals(Constants.PROTOCOL_TYPE_NAME)) {
			from = addVertex(r.getId_from() + "|" + r.getForm_type(), conn, r.getId_to());
			return;
		}

		if (r.getForm_type().equals(Constants.PROTOCOL_TYPE_NAME)) {
			to = addVertex(r.getId_to() + "|" + r.getForm_type(), conn, r.getId_from());
			return;
		}

		from = addVertex(r.getId_from() + "|" + r.getForm_type(), conn, "");
		to = addVertex(r.getId_to() + "|" + r.getTo_type(), conn, "");

		if (!from.isEmpty() && !to.isEmpty()) {
			addFoundCount(r, from, to, conn);
		} else {
			logger.info("addFoundCount from or to is empty! threadid: {}, from_to:{}", Thread.currentThread().getId(),
					r.getId_from() + "|" + r.getForm_type() + "|" + r.getId_to() + "|" + r.getTo_type());
		}
	}

	public void addFoundCount(Relation r, String from, String to, Connection conn) {
		// 根据来源得到级别
		String level_source = Constants.SOURCE_LEVEL_MAP.get(r.getSource());
		if (level_source == null || level_source.isEmpty()) {
			level_source = Constants.SCORE_LEVEL_10;
		}

		// 根据厂商得到级别
		String level_company = Constants.COMPANYID_LEVEL_MAP.get(r.getCompany_id());
		if (level_company == null || level_company.isEmpty()) {
			level_company = Constants.SCORE_LEVEL_10;
		}

		// 判断from与to是否已建立关系
		createEdge(r, level_source, level_company, from, to, conn);

	}

	// 创建及更新关系（边）
	public void createEdge(Relation r, String level_source, String level_company, String from, String to,
			Connection conn) {

		String firstStarttime = DateUtil.formatUnixlt2Str(Long.valueOf(r.getFirst_start_time()), "yyMMdd");
		int relation_found_count = Integer.valueOf(r.getFound_count());

		Long lastFoundCount_Source = Long.valueOf(firstStarttime) * 1000000L;
		Long lastFoundCount_Company = Long.valueOf(firstStarttime) * 1000000L;

		Statement statementSelect1 = null;
		Statement statementSelect2 = null;
		Statement statementInsert = null;
		Statement statementUpdate = null;
		ResultSet resultSet = null;
		String r_id = r.getId_from() + "|" + r.getForm_type() + "|" + r.getId_to() + "|" + r.getTo_type();
		String errSql = "";

		try {
			statementSelect1 = conn.createStatement();

			// 更新SQL
			String updateSql = "";
			String edgeRid = "";

			if (statementSelect1 != null) {
				// 查询SQL
				String selectSql = "select bothE() as rids, bothE().size() as rids_size from [" + from + ", " + to
						+ "]";
				errSql = selectSql;
				resultSet = statementSelect1.executeQuery(selectSql);
				// 关系（边）是否存在，存在则获取边的RID
				if (resultSet != null && resultSet.next()) {
					// FROM的边RID集合
					String edgeRid1 = resultSet.getString("rids");
					int edgeRidSize1 = resultSet.getInt("rids_size");
					if (edgeRidSize1 > 100) {
						logger.info("vertex from :" + from + ", rids_size = " + edgeRidSize1 + " > 100, continue!");
						return;
					}

					String edgeRid2 = "";
					if (resultSet.next()) {
						// TO的边RID集合
						edgeRid2 = resultSet.getString("rids");
						int edgeRidSize2 = resultSet.getInt("rids_size");
						if (edgeRidSize2 > 100) {
							logger.info("vertex to :" + to + ", rids_size = " + edgeRidSize2 + " > 100, continue!");
							return;
						}
					}

					// 从两个集合找出共有的RID，也就是这两个顶点的边（关系）
					edgeRid = getEdgeRid(edgeRid1, edgeRid2);

					// 不存在则创建边
					if (edgeRid.length() == 0) {
						// 不带属性的CREATE SQL
						// String insertSql = "create edge relation from " +
						// from + " to " + to;

						// 创建SQL(带属性的CREATE SQL)
						// 注意：不存在直接用最新时间和发现次数
						lastFoundCount_Source = lastFoundCount_Source + relation_found_count;
						lastFoundCount_Company = lastFoundCount_Company + relation_found_count;

						String insertSql = "create edge relation from " + from + " to " + to + " set r_id='" + r_id
								+ "', " + level_source + "=" + lastFoundCount_Source + ", " + level_company + "="
								+ lastFoundCount_Company + ", " + "s" + r.getSys_source() + "_" + r.getSource() + "_"
								+ r.getCompany_id() + "=" + System.currentTimeMillis() / 1000 + ", " + "ltn"
								+ r.getSys_source() + "='" + r.getFirst_terminal_num() + "', " + "lst"
								+ r.getSys_source() + "=" + Long.valueOf(r.getFirst_start_time()) + ", " + "create_time"
								+ "=" + System.currentTimeMillis() + ", " + "update_time" + "="
								+ System.currentTimeMillis();
						errSql = insertSql;

						statementInsert = conn.createStatement();
						statementInsert.executeQuery(insertSql);
					} else {
						// 通过边（关系）RID得到level_source和level_company的发现次数
						selectSql = "select " + level_source + ", " + level_company + " from " + edgeRid;
						statementSelect2 = conn.createStatement();

						resultSet.close();
						resultSet = statementSelect2.executeQuery(selectSql);

						if (resultSet != null && resultSet.next()) {
							String lastFoundCount_tmp = resultSet.getString(1); // 当没有对应级别的，则返回null，对于此情况赋初值
							lastFoundCount_Source = Long.valueOf(StringUtils.isNotEmpty(lastFoundCount_tmp)
									? lastFoundCount_tmp : Long.valueOf(firstStarttime) * 1000000L + "");
							lastFoundCount_tmp = resultSet.getString(2);
							lastFoundCount_Company = Long.valueOf(StringUtils.isNotEmpty(lastFoundCount_tmp)
									? lastFoundCount_tmp : Long.valueOf(firstStarttime) * 1000000L + "");
						}

						lastFoundCount_Source = getLastTimeAndCount(firstStarttime, lastFoundCount_Source);
						lastFoundCount_Company = getLastTimeAndCount(firstStarttime, lastFoundCount_Company);

						// 注意：只有获取到图中的次数且更新了最新发现时间才加上新发现次数
						lastFoundCount_Source = lastFoundCount_Source + relation_found_count;
						lastFoundCount_Company = lastFoundCount_Company + relation_found_count;

						updateSql = "update " + edgeRid + "set " + level_source + "=" + lastFoundCount_Source + ", "
								+ level_company + "=" + lastFoundCount_Company + ", " + "s" + r.getSys_source() + "_"
								+ r.getSource() + "_" + r.getCompany_id() + "=" + System.currentTimeMillis() / 1000
								+ ", " + "ltn" + r.getSys_source() + "='" + r.getFirst_terminal_num() + "', " + "lst"
								+ r.getSys_source() + "=" + Long.valueOf(r.getFirst_start_time()) + ", " + "update_time"
								+ "=" + System.currentTimeMillis();
						errSql = updateSql;

						statementUpdate = conn.createStatement();
						statementUpdate.executeQuery(updateSql);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.info("createEdge SQLException: " + e + ", sql : " + errSql);
		} catch (Exception e) {
			logger.info("createEdge Exception: " + e);
		} finally {
			try {
				if (statementSelect1 != null)
					statementSelect1.close();
			} catch (SQLException ignore) {
			}
			try {
				if (statementSelect2 != null)
					statementSelect2.close();
			} catch (SQLException ignore) {
			}
			try {
				if (statementInsert != null)
					statementInsert.close();
			} catch (SQLException ignore) {
			}
			try {
				if (statementUpdate != null)
					statementUpdate.close();
			} catch (SQLException ignore) {
			}
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException ignore) {
			}
		}
	}

	// 从FROM和TO的关系（边）列表中，找出这两个顶点共同的边
	public String getEdgeRid(String rids_from, String rids_to) {
		if (rids_from.isEmpty() || rids_to.isEmpty() || rids_from.length() == 2 || rids_to.length() == 2) {
			return "";
		}

		// 去除[]，如[#107:0, #108:0, #109:0, #110:0, #111:0]
		rids_from = rids_from.substring(1, rids_from.length() - 1);

		String[] split_from = rids_from.split(", ");
		for (String from : split_from) {
			if (rids_to.contains(from)) {
				return from;
			}
		}

		return "";

	}

	public Long getLastTimeAndCount(String firstStarttime, Long lastFoundCount) {
		// 保存的次数（long型）是由两部分组成：前6位是日期(YYMMDD)，后面6位是次数
		String firstStarttime_tmp = lastFoundCount / 1000000L + "";// 图里的发现时间
		if (firstStarttime_tmp.compareTo(firstStarttime) >= 0) {
			firstStarttime = firstStarttime_tmp;
		}
		lastFoundCount = lastFoundCount - (lastFoundCount / 1000000L * 1000000L);// 发现次数

		if (lastFoundCount > 999999L) {
			lastFoundCount = 999999L;
		}

		lastFoundCount = Long.valueOf(firstStarttime) * 1000000L + lastFoundCount;

		return lastFoundCount;
	}

}
