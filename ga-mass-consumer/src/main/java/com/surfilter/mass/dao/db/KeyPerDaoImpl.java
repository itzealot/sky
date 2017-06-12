package com.surfilter.mass.dao.db;

import static com.surfilter.mass.utils.Dates.unixTime;
import static com.surfilter.mass.utils.ImcaptureUtil.addIntoMap;
import static com.surfilter.mass.utils.ImcaptureUtil.getProtocolType;
import static com.surfilter.mass.utils.ImcaptureUtil.getValue;
import static com.surfilter.mass.utils.ImcaptureUtil.isEmpty;
import static com.surfilter.mass.utils.ImcaptureUtil.abs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.surfilter.mass.MatchType;
import com.surfilter.mass.dao.KeyPerDao;
import com.surfilter.mass.entity.AlarmInfo;
import com.surfilter.mass.entity.ClusterAlarmResult;
import com.surfilter.mass.entity.ClusterAlarmResultQueryObject;
import com.surfilter.mass.entity.MatchInfo;
import com.surfilter.mass.entity.MsgNotify;
import com.surfilter.mass.entity.ServiceInfo;
import com.surfilter.mass.services.match.algthm.AhoCorasick;
import com.surfilter.mass.utils.ImcaptureUtil;

/**
 * 关键人员实现类
 *
 * @author zealot
 */
public class KeyPerDaoImpl implements KeyPerDao {

	private final static Logger LOG = LoggerFactory.getLogger(KeyPerDaoImpl.class);

	// all key per for people
	private static final String Q_NOT_ZD_KEY_PER = "select fm.id, fm.store_id, fm.match_type, fm.match_child_type, fm.match_value, fs.mobile_phone, fs.mail_account, fm.service_range, fm.area_range, fs.day_alarm_count, fm.service_type_range,fs.alarm_interval,fm.certificate_type,fm.certificate_code,fm.zd_person_id,fm.zd_person_mobile,fm.user_name from focus_mac_info fm, focus_store fs where fs.id=fm.store_id and fm.use_status=1 and fs.use_status=1 and (fm.zd_person_id is null or fm.zd_person_id='') and fs.useful_time>=?";
	// all key per for zd person
	private static final String Q_ALL_KEY_PER = "select fm.id, fm.store_id, fm.match_type, fm.match_child_type, fm.match_value, fs.is_mobile_alarm, fs.mobile_phone, fs.is_mail_alarm, fs.mail_account, fm.service_range, fm.area_range, fs.day_alarm_count, fm.service_type_range,fs.alarm_interval,fm.certificate_type,fm.certificate_code,fm.zd_person_id,fm.zd_person_mobile,fm.zd_type,fm.user_name,fm.creatorArea from focus_mac_info fm, focus_store fs where fs.id=fm.store_id and fm.use_status=1 and fs.use_status=1 and fm.match_value!='0' and fs.useful_time>=?";
	private static final String I_FOCUS_ALARM_INFO = "insert into focus_alarm_info(store_id,mac_id,match_type,match_child_type,match_value,start_time,end_time,alarm_time,service_code,xpoint,ypoint,store_pid,data_type,certificate_type,certificate_code,zd_person_id,zd_person_mobile,user_name,zd_type) values(?,?,?,?,?,?,?,?,?,?,?,0,?,?,?,?,?,?,?)";
	private static final String I_ZD_PERSON_ALARM_INFO = "insert into zd_person_alarm_info(zd_person_id,zd_person_name,zd_person_mobile,zd_type,certificate_type,certificate_code,mac_id,match_type,match_child_type,match_value,service_code,xpoint,ypoint,start_time,end_time,alarm_time,data_type,zd_creater,zd_province_code,zd_city_code,zd_area_code,zd_police_code) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SEPARATOR = "\002";
	private static final String I_PHONE_MSG = "insert into phoneMsg(type,phone,content,createTime) values(2,?,?,?)";
	private static final String I_EMAIL_MSG = "insert into emailMsg(type,`to`,title,content,createTime) values(2,?,?,?,?)";
	private static final String Q_SERCODE_SERTYPE = "select service_code,substring(service_type,1,1) as service_type,police_code,province_code,city_code,area_code from service_info";
	private static final String SERVICE_TYPES_STRING = "123";
	private static final String Q_MAC_FILTER_CONF = "select name,value from systemParam where (type=8 and name<>'mass_filter_cap_mac_track_original') or (name='alarm_info_query_interval' or name='cluster_alarm_result_interval' or name='cluster_alarm_min_counts')";
	private static final String Q_ZD_ALARM_INFO = "select zd_person_id,zd_person_name,zd_person_mobile,zd_type,certificate_type,certificate_code,service_code,xpoint,ypoint,start_time,end_time,mac_id,match_type,match_child_type,match_value from zd_person_alarm_info where start_time >= now()-INTERVAL ? MINUTE order by start_time asc";
	private static final String I_CLUSTER_ALARM_RESULT = "insert into cluster_alarm_result(service_code,province_code,city_code,area_code,police_code,gang_list,first_alarm_time,last_alarm_time,create_time,zd_type,gang_time,cluster_time) values(?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String Q_CLUSTER_ALARM_RESULT = "select first_alarm_time,last_alarm_time,gang_time from cluster_alarm_result where service_code=? and gang_list=? and zd_type=? order by last_alarm_time desc limit 1";
	private static final String U_CLUSTER_ALARM_RESULT = "update cluster_alarm_result set first_alarm_time=?, last_alarm_time=?, gang_time=?,cluster_time=? where service_code=? and gang_list=? and zd_type=?";

	private static final String Q_ZD_ALARM_INFO_COUNT = "select count(*) as av from zd_person_alarm_info where start_time >= now()-INTERVAL ? MINUTE";

	private static final String D_ZD_PERSON_ALARM_INFO = "delete from zd_person_alarm_info where to_days(now())-to_days(alarm_time) > ?";

	private static final String SERVICE_INFO = "service_info";
	private static final String SPLITER = "|";
	private static final String EMPTY_VALUE = "0";
	private static final String EMPTY_INT_VALUE = "-1";

	private JdbcPool pool;

	public KeyPerDaoImpl(JdbcConfig jdbcConfig) {
		this.pool = JdbcPool.getInstance(jdbcConfig);
	}

	@Override
	public Map<String, String> getMacFilterConf() {
		Connection conn = pool.getConnection();

		if (conn != null) {
			Map<String, String> map = new HashMap<>(10);
			ResultSet rs = null;
			PreparedStatement pstm = null;
			try {
				pstm = conn.prepareStatement(Q_MAC_FILTER_CONF);
				rs = pstm.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						map.put(rs.getString("name"), rs.getString("value"));
					}
				}
			} catch (Exception e) {
				LOG.error("query wl mac filter or zd_person cluster setting failed.", e);
			} finally {
				JdbcUtils.close(pstm, rs);
				pool.returnConnection(conn);
			}
			return map;
		}
		return new HashMap<>(0);
	}

	@Override
	public void saveFocusAlarmInfos(Collection<AlarmInfo> alarmInfos) {
		Connection conn = pool.getConnection();
		List<MsgNotify> msgNotifys = Lists.newArrayListWithCapacity(alarmInfos.size());

		if (conn != null) {
			PreparedStatement pstm = null;
			try {
				conn.setAutoCommit(false);
				pstm = conn.prepareStatement(I_FOCUS_ALARM_INFO);
				StringBuffer buffer = new StringBuffer(128);

				for (AlarmInfo alarmInfo : alarmInfos) {
					String msg = buffer.append(alarmInfo.getMatchType()).append(SPLITER)
							.append(alarmInfo.getMatchValue()).append(SPLITER).append(alarmInfo.getStartTime())
							.append(SPLITER).append(alarmInfo.getServiceCode()).append(SPLITER)
							.append(alarmInfo.getStoreId()).append(SPLITER).append(getProtocolType(alarmInfo))
							.toString();

					buffer.setLength(0);

					if (alarmInfo.isEmailAlarm() && alarmInfo.getMailAccount().length() > 1) {
						msgNotifys.add(new MsgNotify(1, alarmInfo.getMailAccount(), msg));
					}

					if (alarmInfo.isPhoneAlarm() && alarmInfo.getPhoneAccount().length() > 1) {
						msgNotifys.add(new MsgNotify(0, alarmInfo.getPhoneAccount(), msg));
					}

					pstm.setLong(1, alarmInfo.getStoreId());
					pstm.setLong(2, alarmInfo.getId());
					pstm.setString(3, alarmInfo.getMatchType());
					pstm.setString(4, alarmInfo.getMatchChildValue());
					pstm.setString(5, alarmInfo.getMatchValue());
					pstm.setTimestamp(6, new Timestamp(alarmInfo.getStartTime() * 1000));
					pstm.setTimestamp(7, new Timestamp(alarmInfo.getEndTime() * 1000));
					pstm.setTimestamp(8, new Timestamp(new Date().getTime()));
					pstm.setString(9, alarmInfo.getServiceCode());
					pstm.setString(10, alarmInfo.getXpoint());
					pstm.setString(11, alarmInfo.getYpoint());

					pstm.setString(12, alarmInfo.getAlarmType());
					pstm.setString(13, isEmpty(alarmInfo.getCertType()) ? null : alarmInfo.getCertType());
					pstm.setString(14, isEmpty(alarmInfo.getCertCode()) ? null : alarmInfo.getCertCode());
					pstm.setLong(15, isEmpty(alarmInfo.getZdPersonId() + "") ? 0L : alarmInfo.getZdPersonId());
					pstm.setString(16, isEmpty(alarmInfo.getZdPersonMobile()) ? null : alarmInfo.getZdPersonMobile());
					pstm.setString(17, isEmpty(alarmInfo.getUserName()) ? null : alarmInfo.getUserName());
					pstm.setInt(18, isEmpty(alarmInfo.getZdType()) ? -1 : Integer.parseInt(alarmInfo.getZdType()));
					pstm.addBatch();
				}
				pstm.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				LOG.error("insert alarm info fail.", e);
			} finally {
				JdbcUtils.close(pstm);
				pool.returnConnection(conn);
			}
		}

		this.saveMsgNotifys(msgNotifys);
	}

	@Override
	public void saveZdPersonAlarmInfos(Collection<AlarmInfo> zdPersonAlarmInfos) {
		Connection conn = pool.getConnection();

		if (conn != null) {
			PreparedStatement pstm = null;
			try {
				conn.setAutoCommit(false);
				pstm = conn.prepareStatement(I_ZD_PERSON_ALARM_INFO);
				for (AlarmInfo alarmInfo : zdPersonAlarmInfos) {
					// 一个ZDR可能有多种类型，而且以","分开，所以需要拆开来入库，因为后续的ZDR聚集需要这个类型
					String zdTypeStr = "-1".equals(alarmInfo.getZdType()) ? null : alarmInfo.getZdType().trim();
					if (zdTypeStr == null) {
						LOG.error("There is not zdType for zdPerson where zdPersionId is " + alarmInfo.getZdPersonId());
						continue;
					}
					String creatorArea = "0".equals(alarmInfo.getCreatorArea()) ? " :,,,"
							: alarmInfo.getCreatorArea().trim();
					String[] zdTypeRows = zdTypeStr.split(",");
					String[] creatorAreaRows = creatorArea.split(";");
					for (String zdTypeRow : zdTypeRows) {
						// 一个ZDR可以由多个人下发，需要拆开，做查看权限控制
						for (String creatorAreaRow : creatorAreaRows) {
							String[] createrAndArea = creatorAreaRow.split(":");
							String creater = createrAndArea[0];
							if (creater == null || creater.trim().length() == 0) {
								creater = null;
							}
							String[] shengShiQu = createrAndArea[1].split(",", -1);
							for (int index = 0; index < shengShiQu.length; index++) {
								if (shengShiQu[index] == null || shengShiQu[index].trim().length() == 0) {
									shengShiQu[index] = null;
								} else {
									shengShiQu[index] = shengShiQu[index].trim();
								}
							}
							pstm.setLong(1, alarmInfo.getZdPersonId());
							pstm.setString(2, alarmInfo.getUserName());
							pstm.setString(3, alarmInfo.getZdPersonMobile());
							pstm.setInt(4, Integer.parseInt(zdTypeRow));
							pstm.setString(5, isEmpty(alarmInfo.getCertType()) ? null : alarmInfo.getCertType());
							pstm.setString(6, isEmpty(alarmInfo.getCertCode()) ? null : alarmInfo.getCertCode());
							pstm.setLong(7, alarmInfo.getId());
							pstm.setString(8, alarmInfo.getMatchType());
							pstm.setString(9, alarmInfo.getMatchChildValue());
							pstm.setString(10, alarmInfo.getMatchValue());
							pstm.setString(11, alarmInfo.getServiceCode());
							pstm.setString(12, alarmInfo.getXpoint());
							pstm.setString(13, alarmInfo.getYpoint());
							pstm.setTimestamp(14, new Timestamp(alarmInfo.getStartTime() * 1000));
							pstm.setTimestamp(15, new Timestamp(alarmInfo.getEndTime() * 1000));
							pstm.setTimestamp(16, new Timestamp(new Date().getTime()));
							pstm.setString(17, alarmInfo.getAlarmType());
							pstm.setString(18, creater);
							pstm.setString(19, shengShiQu[0]);
							pstm.setString(20, shengShiQu[1]);
							pstm.setString(21, shengShiQu[2]);
							pstm.setString(22, shengShiQu[3]);
							pstm.addBatch();
						}
					}
				}
				pstm.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				LOG.error("insert zd_person alarm info fail.", e);
			} finally {
				JdbcUtils.close(pstm);
				pool.returnConnection(conn);
			}
		}
	}

	@Override
	public List<String> getNotZdAllKeyPer() {
		return getAllKeyPer(Q_NOT_ZD_KEY_PER);
	}

	@Override
	public List<String> getAllKeyPer() {
		return getAllKeyPer(Q_ALL_KEY_PER);
	}

	@Override
	public void addAllKeyPer(AhoCorasick<MatchInfo> acinfo) {
		long start = System.currentTimeMillis();
		Connection conn = pool.getConnection();

		if (conn != null) {
			ResultSet rs = null;
			PreparedStatement pstm = null;

			try {
				pstm = conn.prepareStatement(Q_ALL_KEY_PER);
				pstm.setTimestamp(1, new Timestamp(new Date().getTime()));
				int counts = 0;

				if (pstm != null) {
					rs = pstm.executeQuery();

					if (rs != null) {
						boolean flag = true;
						StringBuffer buffer = new StringBuffer(64);

						while (rs.next()) {
							String matchType = rs.getString("match_type");
							if (StringUtils.isEmpty(matchType)) {
								LOG.error("focus_mac_info's match-type column has error value where id is "
										+ rs.getInt("id"));
								continue;
							}

							String matchValue = rs.getString("match_value");
							matchValue = matchValue == null ? "" : matchValue.trim();
							if (matchValue.isEmpty() || EMPTY_VALUE.equals(matchValue)) { // 过滤非法matchValue
								if (flag) {
									LOG.error("table=focus_mac_info match_value column has error value");
									flag = false;
								}
								continue;
							}

							Long storeId = rs.getLong("store_id");
							Long macId = rs.getLong("id");

							String matchChildType = rs.getString("match_child_type");
							if (isBlank(matchChildType)) {
								matchChildType = EMPTY_VALUE;
							}

							String alarmPhones = rs.getString("mobile_phone");
							if (isBlank(alarmPhones)) {
								alarmPhones = EMPTY_VALUE;
							}

							String alarmEmails = rs.getString("mail_account");
							if (isBlank(alarmEmails)) {
								alarmEmails = EMPTY_VALUE;
							}

							String serviceRange = rs.getString("service_range");
							if (isBlank(serviceRange)) {
								serviceRange = EMPTY_VALUE;
							}

							String areaRange = rs.getString("area_range");
							if (isBlank(areaRange)) {
								areaRange = EMPTY_VALUE;
							}

							int dayAlarmCount = ImcaptureUtil.getValue(rs.getString("day_alarm_count"), 0);

							String serviceType = rs.getString("service_type_range");
							if (!(isNotBlank(serviceType) && serviceType != null
									&& SERVICE_TYPES_STRING.indexOf(serviceType) != -1)) {
								serviceType = EMPTY_INT_VALUE;
							}

							String alarmInterval = rs.getString("alarm_interval");
							if (isBlank(alarmInterval)) {
								alarmInterval = EMPTY_VALUE;
							}

							String certType = rs.getString("certificate_type");
							if (isBlank(certType)) {
								certType = EMPTY_VALUE;
							}

							String certCode = rs.getString("certificate_code");
							if (isBlank(certCode)) {
								certCode = EMPTY_VALUE;
							}

							String zdPersonId = rs.getString("zd_person_id");
							if (isBlank(zdPersonId)) {
								zdPersonId = EMPTY_INT_VALUE;
							}

							String zdPersonMobile = rs.getString("zd_person_mobile");
							if (isBlank(zdPersonMobile)) {
								zdPersonMobile = EMPTY_VALUE;
							}

							String userName = rs.getString("user_name");
							if (isBlank(userName)) {
								userName = EMPTY_VALUE;
							}

							String zdType = rs.getString("zd_type");
							if (isBlank(zdType)) {
								zdType = EMPTY_INT_VALUE;
							}

							String isMobileAlarm = rs.getString("is_mobile_alarm");
							if (isBlank(isMobileAlarm)) {
								isMobileAlarm = EMPTY_VALUE;
							}

							String isMailAlarm = rs.getString("is_mail_alarm");
							if (isBlank(isMailAlarm)) {
								isMailAlarm = EMPTY_VALUE;
							}

							String creatorArea = rs.getString("creatorArea");
							if (isBlank(creatorArea)) {
								creatorArea = EMPTY_VALUE;
							}

							counts++;

							MatchInfo matchInfo = new MatchInfo(storeId, macId, matchType, matchValue, matchChildType,
									alarmPhones, alarmEmails, serviceRange, areaRange, dayAlarmCount, serviceType,
									alarmInterval, certType, certCode, zdPersonId, zdPersonMobile, userName, zdType,
									"1".equals(isMobileAlarm), "1".equals(isMailAlarm), creatorArea);

							Integer matchTypeValue = Integer.valueOf(matchType);

							if (MatchType.MAC.getCode() == matchTypeValue) {
								acinfo.add(buffer.append(matchValue).append(MatchType.MAC.getSimCode()).toString()
										.getBytes(), matchInfo);
							} else if (MatchType.PROTOCOL.getCode() == matchTypeValue) {
								acinfo.add(buffer.append(matchValue).append(MatchType.PROTOCOL.getSimCode()).toString()
										.getBytes(), matchInfo);
							} else if (MatchType.PHONE.getCode() == matchTypeValue) {
								acinfo.add(buffer.append(matchValue).append(MatchType.PHONE.getSimCode()).toString()
										.getBytes(), matchInfo);
							} else if (MatchType.IMEI.getCode() == matchTypeValue) {
								acinfo.add(buffer.append(matchValue).append(MatchType.IMEI.getSimCode()).toString()
										.getBytes(), matchInfo);
							} else if (MatchType.IMSI.getCode() == matchTypeValue) {
								acinfo.add(buffer.append(matchValue).append(MatchType.IMSI.getSimCode()).toString()
										.getBytes(), matchInfo);
							} else if (MatchType.CERT.getCode() == matchTypeValue) {
								acinfo.add(buffer.append(matchValue).append(matchChildType)
										.append(MatchType.CERT.getSimCode()).toString().getBytes(), matchInfo);
							} else if (MatchType.ACCOUNT.getCode() == matchTypeValue) {
								// match_type:match_value:match_child_type=>账号类数据解决后缀匹配的BUG
								acinfo.add(buffer.append(MatchType.ACCOUNT.getSimCode()).append(matchValue)
										.append(matchChildType).toString().getBytes(), matchInfo);
							}

							buffer.setLength(0);
						}
					}
				}

				long spends = System.currentTimeMillis() - start;
				LOG.info("Ac inited successfully! total size: {}, spends: {}ms", counts, spends);
			} catch (SQLException e) {
				LOG.error("Ac inited successfully fail.", e);
			} finally {
				JdbcUtils.close(rs, pstm);
				pool.returnConnection(conn);
			}
		}
	}

	private List<String> getAllKeyPer(String sql) {
		long start = System.currentTimeMillis();
		Connection conn = pool.getConnection();

		if (conn != null) {
			ResultSet rs = null;
			PreparedStatement pstm = null;
			List<String> resultList = new ArrayList<String>();

			try {
				pstm = conn.prepareStatement(sql);
				pstm.setTimestamp(1, new Timestamp(new Date().getTime()));

				if (pstm != null) {
					rs = pstm.executeQuery();

					if (rs != null) {
						boolean flag = true;
						StringBuilder strBuilder = new StringBuilder(300);

						while (rs.next()) {
							strBuilder.setLength(0);

							if (StringUtils.isEmpty(rs.getString("match_type"))) {
								LOG.error("focus_mac_info's match-type column has error value where id is "
										+ rs.getInt("id"));
								continue;
							}

							String matchValue = rs.getString("match_value");
							matchValue = matchValue == null ? "" : matchValue.trim();
							if (matchValue.isEmpty() || EMPTY_VALUE.equals(matchValue)) { // 过滤非法matchValue
								if (flag) {
									LOG.error("table=focus_mac_info match_value column has error value");
									flag = false;
								}
								continue;
							}

							strBuilder.append(rs.getLong("id") + SEPARATOR + rs.getLong("store_id"));

							strBuilder.append(SEPARATOR);
							String matchChildType = rs.getString("match_child_type");
							if (isNotBlank(matchChildType)) {
								strBuilder.append(rs.getString("match_type")).append(":").append(matchValue).append(":")
										.append(matchChildType);
							} else {
								strBuilder.append(rs.getString("match_type")).append(":").append(matchValue)
										.append(":0");
							}

							strBuilder.append(SEPARATOR);
							String mobilePhone = rs.getString("mobile_phone");
							if (isNotBlank(mobilePhone)) {
								strBuilder.append(mobilePhone);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String mailCount = rs.getString("mail_account");
							if (isNotBlank(mailCount)) {
								strBuilder.append(mailCount);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String serviceRange = rs.getString("service_range");
							if (isNotBlank(serviceRange)) {
								strBuilder.append(serviceRange);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String areaRange = rs.getString("area_range");
							if (isNotBlank(areaRange)) {
								strBuilder.append(areaRange);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String dayAlarmCount = rs.getString("day_alarm_count");
							if (isNotBlank(dayAlarmCount)) {
								strBuilder.append(dayAlarmCount);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String service_type_range = rs.getString("service_type_range");
							if (isNotBlank(service_type_range) && service_type_range != null
									&& SERVICE_TYPES_STRING.indexOf(service_type_range) != -1) {
								strBuilder.append(service_type_range);
							} else {
								strBuilder.append(EMPTY_INT_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String alarmInterval = rs.getString("alarm_interval");
							if (isNotBlank(alarmInterval)) {
								strBuilder.append(alarmInterval);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String certType = rs.getString("certificate_type");
							if (isNotBlank(certType)) {
								strBuilder.append(certType);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String certCode = rs.getString("certificate_code");
							if (isNotBlank(certCode)) {
								strBuilder.append(certCode);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String zdPersonId = rs.getString("zd_person_id");
							if (isNotBlank(zdPersonId)) {
								strBuilder.append(zdPersonId);
							} else {
								strBuilder.append(EMPTY_INT_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String zdPersonMobile = rs.getString("zd_person_mobile");
							if (isNotBlank(zdPersonMobile)) {
								strBuilder.append(zdPersonMobile);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String userName = rs.getString("user_name");
							if (isNotBlank(userName)) {
								strBuilder.append(userName);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String type = rs.getString("zd_type");
							if (isNotBlank(type)) {
								strBuilder.append(type);
							} else {
								strBuilder.append(EMPTY_INT_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String isMobileAlarm = rs.getString("is_mobile_alarm");
							if (isNotBlank(isMobileAlarm)) {
								strBuilder.append(isMobileAlarm);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							strBuilder.append(SEPARATOR);
							String isMailAlarm = rs.getString("is_mail_alarm");
							if (isNotBlank(isMailAlarm)) {
								strBuilder.append(isMailAlarm);
							} else {
								strBuilder.append(EMPTY_VALUE);
							}

							resultList.add(strBuilder.toString());
						}
					}
				}

				long spend = System.currentTimeMillis() - start;
				LOG.info("finish query math info, size:{}, spends:{} ms", resultList.size(), spend);

				return resultList;
			} catch (SQLException e) {
				LOG.error("query focus mac info fail.", e);
			} finally {
				JdbcUtils.close(rs, pstm);
				pool.returnConnection(conn);
			}
		}

		return new ArrayList<String>(0);
	}

	@Override
	public Map<String, ServiceInfo> querySerCodeType() {
		long start = System.currentTimeMillis();
		Connection conn = pool.getConnection();

		if (conn != null) {
			ResultSet rs = null;
			PreparedStatement pstm = null;
			Map<String, ServiceInfo> map = new HashMap<>(count(conn, SERVICE_INFO));

			try {
				pstm = conn.prepareStatement(Q_SERCODE_SERTYPE);
				if (pstm != null) {
					rs = pstm.executeQuery();

					if (rs != null) {
						while (rs.next()) {
							String provinceCode = rs.getString("province_code");
							String cityCode = rs.getString("city_code");
							String areaCode = rs.getString("area_code");
							String serviceCode = rs.getString("service_code");
							String serviceType = rs.getString("service_type");

							if (serviceCode != null && serviceCode.length() == 14 && serviceType != null
									&& SERVICE_TYPES_STRING.indexOf(serviceType) != -1) {

								if (isEmpty(provinceCode) || isEmpty(cityCode) || isEmpty(areaCode)) {
									LOG.warn("serviceCode={} empty provinceCode, cityCode, areaCode", serviceCode);
									continue;
								}

								ServiceInfo info = new ServiceInfo(serviceType, rs.getString("police_code"),
										provinceCode, cityCode, areaCode);

								map.put(serviceCode, info);
							}
						}
					}
				}

				long spend = System.currentTimeMillis() - start;
				LOG.info("finish query service_info, map size:{}, spends:{} ms", map.size(), spend);
				return map;
			} catch (SQLException e) {
				LOG.error("query service_info into map fail.", e);
			} finally {
				JdbcUtils.close(rs, pstm);
				pool.returnConnection(conn);
			}
		}

		return new HashMap<>(0);
	}

	@Override
	public void saveMsgNotifys(List<MsgNotify> msgs) {
		List<MsgNotify> emailMsgs = Lists.newArrayList();
		List<MsgNotify> phoneMsgs = Lists.newArrayList();

		for (MsgNotify msg : msgs) {
			if (msg.getType() == 0) {
				phoneMsgs.add(msg);
			} else {
				emailMsgs.add(msg);
			}
		}

		if (CollectionUtils.isNotEmpty(phoneMsgs)) {
			this.savePhoneMsg(phoneMsgs);
			phoneMsgs.clear();
		}
		phoneMsgs = null;

		if (CollectionUtils.isNotEmpty(emailMsgs)) {
			this.saveEmailMsg(emailMsgs);
			emailMsgs.clear();
		}
		emailMsgs = null;
	}

	private void savePhoneMsg(List<MsgNotify> msgs) {
		Connection conn = pool.getConnection();

		if (conn != null) {
			PreparedStatement pstm = null;

			try {
				conn.setAutoCommit(false);
				pstm = conn.prepareStatement(I_PHONE_MSG);
				for (MsgNotify msg : msgs) {
					String[] phones = msg.getNotifyAccount().split(",");

					if (phones.length > 0) {
						for (String s : phones) {
							pstm.setString(1, s);
							pstm.setString(2, msg.getMessage());
							pstm.setTimestamp(3, new Timestamp(new Date().getTime()));
							pstm.addBatch();
						}
					}
				}
				pstm.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				LOG.error("insert alarm info for phone message fail.", e);
			} finally {
				JdbcUtils.close(pstm);
				pool.returnConnection(conn);
			}
		}
	}

	private int count(Connection conn, String tableName) {
		int counts = 0;
		if (conn != null) {
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				pstm = conn.prepareStatement(String.format("select count(*) as av from %s", tableName));
				rs = pstm.executeQuery();
				if (rs.next()) {
					counts = rs.getInt("av");
				}
			} catch (SQLException e) {
				LOG.error(String.format("execute count table=%s fail.", tableName), e);
			} finally {
				JdbcUtils.close(rs, pstm);
			}
		}
		LOG.info("service info counts:{}", counts);
		return counts;
	}

	private int countZdPeronAlarmInfo(Connection conn, int minutes) {
		int counts = 0;

		if (conn != null) {
			PreparedStatement pstm = null;
			ResultSet rs = null;

			try {
				pstm = conn.prepareStatement(Q_ZD_ALARM_INFO_COUNT);
				pstm.setInt(1, minutes);
				rs = pstm.executeQuery();

				if (rs.next()) {
					counts = rs.getInt("av");
				}
			} catch (SQLException e) {
				LOG.error("Insert alarm info for email message fail.", e);
			} finally {
				JdbcUtils.close(rs, pstm);
			}
		}

		return counts;
	}

	private void saveEmailMsg(List<MsgNotify> msgs) {
		Connection conn = pool.getConnection();

		if (conn != null) {
			PreparedStatement pstm = null;
			try {
				conn.setAutoCommit(false);
				pstm = conn.prepareStatement(I_EMAIL_MSG);
				for (MsgNotify msg : msgs) {
					String[] emails = msg.getNotifyAccount().split(",");
					if (emails.length > 0) {
						for (String s : emails) {
							pstm.setString(1, s);
							pstm.setString(2, "Mail Alarm");
							pstm.setString(3, msg.getMessage());
							pstm.setTimestamp(4, new Timestamp(new Date().getTime()));
							pstm.addBatch();
						}
					}
				}
				pstm.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				LOG.error("Insert alarm info for email message fail.", e);
			} finally {
				JdbcUtils.close(pstm);
				pool.returnConnection(conn);
			}
		}
	}

	@Override
	public Map<String, List<AlarmInfo>> getAlarmInfos(int minutes) {
		Connection conn = pool.getConnection();

		if (conn != null) {
			PreparedStatement pstm = null;
			ResultSet rs = null;
			long start = System.currentTimeMillis();
			Map<String, List<AlarmInfo>> map = new HashMap<String, List<AlarmInfo>>(
					countZdPeronAlarmInfo(conn, minutes));

			try {
				pstm = conn.prepareStatement(Q_ZD_ALARM_INFO);

				pstm.setInt(1, minutes);
				rs = pstm.executeQuery();

				if (rs != null) {
					while (rs.next()) {
						String matchValue = getValue(rs.getString("match_value"));
						String matchType = getValue(rs.getString("match_type"));
						String matchChildValue = getValue(rs.getString("match_child_type"));
						Long startTime = unixTime(rs.getString("start_time"));
						Long endTime = unixTime(rs.getString("end_time"));
						String serviceCode = getValue(rs.getString("service_code"));
						String xpoint = getValue(rs.getString("xpoint"));
						String ypoint = getValue(rs.getString("ypoint"));
						String certType = getValue(rs.getString("certificate_type"));
						String certCode = getValue(rs.getString("certificate_code"));
						Long zdPersonId = getValue(rs.getString("zd_person_id"), -1L);
						String zdPersonMobile = getValue(rs.getString("zd_person_mobile"));
						String zd_person_name = getValue(rs.getString("zd_person_name"));
						String zd_type = getValue(rs.getString("zd_type"));

						AlarmInfo a = new AlarmInfo(matchValue, matchType, matchChildValue, null, null, startTime,
								endTime, null, null, serviceCode, xpoint, ypoint, null, 0, 0, certType, certCode,
								zdPersonId, zdPersonMobile, zd_person_name, zd_type, false, false, null);
						addIntoMap(serviceCode + "|" + zd_type, a, map);
					}
				}

				long spend = System.currentTimeMillis() - start;
				LOG.info("finish query between {} minutes zd_person alarm infos, map size:{}, spends:{}ms", minutes,
						map.size(), spend);
				return map;
			} catch (Exception e) {
				LOG.error("query zd_person alarm infos fail.", e);
			} finally {
				JdbcUtils.close(rs, pstm);
				pool.returnConnection(conn);
			}
		}

		return new HashMap<>(0);
	}

	@Override
	public void saveClusterAlarmResults(Map<String, Set<ClusterAlarmResult>> map, int stayLimitSeconds) {
		Connection conn = pool.getConnection();

		if (conn != null) {
			long start = System.currentTimeMillis();
			PreparedStatement pstm = null;
			PreparedStatement pQ = null;
			PreparedStatement pU = null;

			try {
				conn.setAutoCommit(false);
				pstm = conn.prepareStatement(I_CLUSTER_ALARM_RESULT);

				int counts = 0;
				pQ = conn.prepareStatement(Q_CLUSTER_ALARM_RESULT);
				pU = conn.prepareStatement(U_CLUSTER_ALARM_RESULT);

				for (Entry<String, Set<ClusterAlarmResult>> entry : map.entrySet()) {
					Set<ClusterAlarmResult> vals = entry.getValue();

					for (ClusterAlarmResult r : vals) {
						counts++;

						ClusterAlarmResultQueryObject obj = clusterAlarmResultIsExist(pQ, r);

						if (obj == null) { // 不存在则插入
							pstm.setString(1, r.getServiceCode());
							pstm.setString(2, r.getProvinceCode());
							pstm.setString(3, r.getCityCode());
							pstm.setString(4, r.getAreaCode());
							pstm.setString(5, r.getPoliceCode());
							pstm.setString(6, r.getGangList());
							pstm.setTimestamp(7, new Timestamp(r.getFirstAlarmTime() * 1000));
							pstm.setTimestamp(8, new Timestamp(r.getLastAlarmTime() * 1000));
							pstm.setTimestamp(9, new Timestamp(new Date().getTime()));
							pstm.setInt(10, r.getZdType());
							pstm.setString(11, r.getGangTime());
							pstm.setInt(12, (int) (r.getLastAlarmTime() - r.getFirstAlarmTime()));
							pstm.addBatch();
						} else {
							long firstAlarmTime = r.getFirstAlarmTime();
							long lastAlarmTime = r.getLastAlarmTime();

							boolean flag = false;
							if (abs(r.getFirstAlarmTime() - obj.getLastAlarmTime()) <= stayLimitSeconds) {
								firstAlarmTime = min(r.getFirstAlarmTime(), obj.getFirstAlarmTime());
								lastAlarmTime = max(r.getLastAlarmTime(), obj.getLastAlarmTime());
								flag = true;
							}

							pU.setTimestamp(1, new Timestamp(firstAlarmTime * 1000));
							pU.setTimestamp(2, new Timestamp(lastAlarmTime * 1000));

							if (flag) { // 需要合并
								pU.setString(3, ImcaptureUtil.mergeGangTime(obj, r, stayLimitSeconds));
							} else {
								pU.setString(3, r.getGangTime());
							}

							pU.setInt(4, (int) (lastAlarmTime - firstAlarmTime));

							pU.setString(5, r.getServiceCode());
							pU.setString(6, r.getGangList());
							pU.setInt(7, r.getZdType());
							pU.addBatch();
						}
					}
					vals.clear();
					vals = null;
				}
				pstm.executeBatch();
				pU.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				if (counts > 0) {
					long spends = System.currentTimeMillis() - start;
					LOG.info("finish save cluster alarm results, total:{}, spends:{}ms", counts, spends);
				}
			} catch (SQLException e) {
				LOG.error("insert cluster alarm results, table=cluster_alarm_results fail.", e);
			} finally {
				JdbcUtils.close(pstm, pQ, pU);
				pool.returnConnection(conn);
			}
		}
	}

	private ClusterAlarmResultQueryObject clusterAlarmResultIsExist(PreparedStatement pstm, ClusterAlarmResult r) {
		if (pstm != null) {
			ResultSet rs = null;

			try {
				pstm.setString(1, r.getServiceCode());
				pstm.setString(2, r.getGangList());
				pstm.setInt(3, r.getZdType());

				rs = pstm.executeQuery();

				if (rs != null) {
					if (rs.next()) {
						long firstAlarmTime = unixTime(rs.getString("first_alarm_time"));
						long lastAlarmTime = unixTime(rs.getString("last_alarm_time"));
						String gangTime = rs.getString("gang_time");
						return new ClusterAlarmResultQueryObject(firstAlarmTime, lastAlarmTime, gangTime);
					}
				}
			} catch (SQLException e) {
				LOG.error("query cluster alarm results, table=cluster_alarm_results fail.", e);
			} finally {
				JdbcUtils.close(rs);
			}
		}
		return null;
	}

	@Override
	public void cleanZdPersonAlarmInfo(int days) {
		Connection conn = pool.getConnection();
		if (conn != null) {
			PreparedStatement pstm = null;
			try {
				pstm = conn.prepareStatement(D_ZD_PERSON_ALARM_INFO);
				pstm.setInt(1, days);
				pstm.executeUpdate();
				LOG.info("finish clean table=zd_person_alarm_info days:{}", days);
			} catch (SQLException e) {
				LOG.error("clean table=zd_person_alarm_info fail.", e);
			} finally {
				JdbcUtils.close(pstm);
				pool.returnConnection(conn);
			}
		}
	}
}
