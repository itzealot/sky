package com.surfilter.mass.tools.db.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.JdbcConfig;
import com.surfilter.mass.tools.conf.JdbcUtils;
import com.surfilter.mass.tools.db.MysqlDAO;
import com.surfilter.mass.tools.entity.InnList;
import com.surfilter.mass.tools.entity.ZdPerson;
import com.surfilter.mass.tools.entity.ZdPersonInfo;
import com.surfilter.mass.tools.util.Closeables;

/**
 * MysqlDAOImpl
 * 
 * @author zealot
 *
 */
public class MysqlDAOImpl implements MysqlDAO {

	private static Logger LOG = LoggerFactory.getLogger(MysqlDAOImpl.class);

	private static final String I_INN_LIST = "insert into InnList(Service_code,User_name,Certificate_type,Certificate_code,Inn_time,Off_time,Room_no,Floor,Memo,Org_name,Country,Mobile,City_code,Inn_time_server,Off_time_server) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String I_ZD_PERSON = "insert into zd_person(certificate_code,name,address,type,createTime,`creater`,province_code,city_code,area_code,sys_source,source,police_code,department,sex,nation,height,certArea,`both`,remark) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String I_ZD_PERSON_INFO = "insert into zd_person_info(zd_person_id,types,departments,province_code,city_code,area_code,police_code,creater) values(?,?,?,?,?,?,?,?)";
	private static final String Q_ZD_PERSON = "select id from zd_person where certificate_code=?";

	private JdbcConfig config;

	public MysqlDAOImpl(JdbcConfig config) {
		this.config = config;
		try {
			Class.forName(config.getDriverClassName());
		} catch (ClassNotFoundException e) {
			LOG.error("Can't find jdbc driver class.", e);
		}
	}

	@Override
	public void save(List<InnList> lists) {
		Connection conn = JdbcUtils.getConn(config.getUrl(), config.getUserName(), config.getPassword());
		PreparedStatement pstm = null;
		int size = lists.size();

		if (conn != null) {
			try {
				conn.setAutoCommit(false);
				pstm = conn.prepareStatement(I_INN_LIST);

				for (InnList l : lists) {
					pstm.setString(1, l.getServiceCode());
					pstm.setString(2, l.getUsername());
					pstm.setString(3, l.getCertificateType());
					pstm.setString(4, l.getCertificateCode());
					pstm.setDate(5, l.getInnTime());
					pstm.setDate(6, l.getOffTime());
					pstm.setString(7, l.getRoomNo());
					pstm.setString(8, l.getFloor());
					pstm.setString(9, l.getMemo());
					pstm.setString(10, l.getOrgName());
					pstm.setString(11, l.getCountry());
					pstm.setString(12, l.getMobile());
					pstm.setString(13, l.getCityCode());
					pstm.setTimestamp(14, l.getInnTimeServer());
					pstm.setTimestamp(15, l.getOffTimeServer());

					pstm.addBatch();
				}

				pstm.executeBatch();
				conn.commit();
			} catch (Exception e) {
				LOG.error("save InnList error.", e);
			} finally {
				Closeables.close(pstm, conn);
			}
		} else {
			LOG.error("get mysql connection error, size:{}", size);
		}
	}

	@Override
	public List<Long> saveZdPersons(List<ZdPerson> ps) {
		Connection conn = JdbcUtils.getConn(config.getUrl(), config.getUserName(), config.getPassword());
		PreparedStatement pstm = null;
		int size = ps.size();
		List<String> certs = new ArrayList<>(size);

		if (conn != null) {
			try {
				conn.setAutoCommit(false);
				pstm = conn.prepareStatement(I_ZD_PERSON);

				for (ZdPerson zd : ps) {
					pstm.setString(1, zd.getCertificateCode());
					pstm.setString(2, zd.getName());
					pstm.setString(3, zd.getAddress());
					pstm.setString(4, zd.getType());
					pstm.setTimestamp(5, new Timestamp(new Date().getTime()));
					pstm.setString(6, zd.getCreater());
					pstm.setString(7, zd.getProvinceCode());
					pstm.setString(8, zd.getCityCode());
					pstm.setString(9, zd.getAreaCode());
					pstm.setString(10, zd.getSysSource());
					pstm.setString(11, zd.getSource());
					pstm.setString(12, zd.getPoliceCode());
					pstm.setString(13, zd.getDepartment());
					pstm.setString(14, zd.getSex());
					pstm.setString(15, zd.getNation());
					pstm.setString(16, zd.getHeight());
					pstm.setString(17, zd.getCertArea());
					pstm.setString(18, zd.getBoth());
					pstm.setString(19, zd.getRemark());

					certs.add(zd.getCertificateCode());

					pstm.addBatch();
				}

				pstm.executeBatch();
				conn.commit();
			} catch (Exception e) {
				LOG.error("save zd_person error", e);
			} finally {
				Closeables.close(pstm, conn);
			}
		} else {
			LOG.error("save zd_person get mysql connection error, size:{}", size);
		}

		return queyZdPersonIds(certs);
	}

	@Override
	public List<Long> queyZdPersonIds(List<String> certs) {
		Connection conn = JdbcUtils.getConn(config.getUrl(), config.getUserName(), config.getPassword());
		PreparedStatement pstm = null;
		int size = certs.size();
		List<Long> ids = new ArrayList<>(size);

		if (conn != null) {
			try {
				pstm = conn.prepareStatement(Q_ZD_PERSON);

				for (String cert : certs) {
					try {
						pstm.setString(1, cert);
						ResultSet rs = pstm.executeQuery();

						if (rs != null && rs.next()) {
							ids.add(rs.getLong("id"));
						}
						Closeables.close(rs);
					} catch (Exception e) {
						LOG.error(String.format("query zdPerson id by certificate:%s error.", cert), e);
						return null;
					}
				}
				return ids;
			} catch (Exception e) {
				LOG.error(String.format("query zdPerson id by certificate error.size:%d", size), e);
				return null;
			} finally {
				Closeables.close(pstm, conn);
			}
		}

		return null;
	}

	@Override
	public void saveZdPersonInfos(List<ZdPersonInfo> ps) {
		Connection conn = JdbcUtils.getConn(config.getUrl(), config.getUserName(), config.getPassword());
		PreparedStatement pstm = null;
		int size = ps.size();

		if (conn != null) {
			try {
				conn.setAutoCommit(false);
				pstm = conn.prepareStatement(I_ZD_PERSON_INFO);

				for (ZdPersonInfo zd : ps) {
					pstm.setLong(1, zd.getZdPersonId());
					pstm.setString(2, zd.getTypes());
					pstm.setString(3, zd.getDepartments());
					pstm.setString(4, zd.getProvinceCode());
					pstm.setString(5, zd.getCityCode());
					pstm.setString(6, zd.getAreaCode());
					pstm.setString(7, zd.getPoliceCode());
					pstm.setString(8, zd.getCreater());

					pstm.addBatch();
				}

				pstm.executeBatch();
				conn.commit();
			} catch (Exception e) {
				LOG.error("save zd_person_info error.", e);
			} finally {
				Closeables.close(pstm, conn);
			}
		} else {
			LOG.error("save zd_person_info get mysql connection error, size:{}", size);
		}
	}

}
