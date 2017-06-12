package com.surfilter.mass.tools.conf;

import static com.surfilter.mass.tools.util.ValidateUtil.validate;
import static com.surfilter.mass.tools.util.ValidateUtil.validateProperty;
import static com.surfilter.mass.tools.util.ValidateUtil.validateOthers;

import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.util.Dates;
import com.surfilter.mass.tools.util.FileUtil;

/**
 * 视频车辆数据抓取工具配置类
 * 
 * @author zealot
 *
 */
public class VehicleConfig {

	static final Logger LOG = LoggerFactory.getLogger(VehicleConfig.class);

	/** vehicle login property */
	public static final String VEHICLE_LOGIN_URL = "vehicle.login.url";
	public static final String VEHICLE_LOGIN_USERNAME = "vehicle.login.username";
	public static final String VEHICLE_LOGIN_PASSWORD = "vehicle.login.password";
	public static final String VEHICLE_LOGIN_PARAMS = "vehicle.login.params";
	public static final String VEHICLE_COOKIE_SESSION_KEY = "vehicle.cookie.sessionKey";
	public static final String VEHICLE_COOKIE_SESSION = "vehicle.cookie.session";

	/** property for catch */
	/** catch url property */
	public static final String VEHICLE_CATCH_URL = "vehicle.catch.url";
	/** query pageSize */
	public static final String VEHICLE_PAGE_SIZE = "vehicle.pageSize";
	/** query pageNo */
	public static final String VEHICLE_PAGE_NO = "vehicle.pageNo";
	/** 卡口数据存储路径 */
	public static final String VEHICLE_KAKOU_PATH = "vehicle.kakou.path";
	/** query date between */
	public static final String VEHICLE_DATE_BETWEEN = "vehicle.date.between";
	public static final String VEHICLE_HOUR_FROM = "vehicle.hour.from";

	/** property for json log */
	/** json 存储目录 */
	public static final String VEHICLE_JSON_DIR = "vehicle.json.dir";
	/** sysType */
	public static final String VEHICLE_SYS_TYPE = "vehicle.sysType";
	/** areaCode */
	public static final String VEHICLE_AREA_CODE = "vehicle.areaCode";
	/** companyId */
	public static final String VEHICLE_COMPANY_ID = "vehicle.companyId";

	/** 昨天的数据是否需要抓取 */
	public static final String VEHICLE_YESTERDAY_CATCH_ENABLE = "vehicle.yesterday.catch.enable";

	private String catchUrl; // 抓取 url
	private int pageSize; // 查询页大小
	private int pageNo; // 当前查询的页码
	private Date startDate; // 开始日期
	private Date endDate; // 结束日期

	private String kakouPath; // 卡口数据存储路径

	private String dst; // json 目标文件存储位置
	private String sysType; // 系统来源
	private String areaCode; // 区域编码
	private String companyId; // 厂商编码

	private String[] username;
	private String[] password;
	private String[] params;
	private String sessionKey;
	private String loginUrl;
	private boolean yesterdayCatchEnable;
	private String session;

	public VehicleConfig(MassConfiguration conf) {
		init(conf);
	}

	public void init(MassConfiguration conf) {
		this.pageSize = conf.getInt(VEHICLE_PAGE_SIZE, 1000);
		this.catchUrl = validateProperty(conf.get(VEHICLE_CATCH_URL), VEHICLE_CATCH_URL);
		this.pageNo = conf.getInt(VEHICLE_PAGE_NO, 1);
		this.kakouPath = validateProperty(conf.get(VEHICLE_KAKOU_PATH), VEHICLE_KAKOU_PATH);
		this.sysType = validateProperty(conf.get(VEHICLE_SYS_TYPE), VEHICLE_SYS_TYPE);
		this.areaCode = validateProperty(conf.get(VEHICLE_AREA_CODE), VEHICLE_AREA_CODE);
		this.companyId = validateProperty(conf.get(VEHICLE_COMPANY_ID), VEHICLE_COMPANY_ID);
		this.dst = FileUtil.pathWithSuffix(validateProperty(conf.get(VEHICLE_JSON_DIR), VEHICLE_JSON_DIR));

		/** login info */
		this.sessionKey = conf.get(VEHICLE_COOKIE_SESSION_KEY, SysConstant.COOKIE_SESSION_DEFAULT_VALUE);
		this.username = validate(conf.get(VEHICLE_LOGIN_USERNAME), VEHICLE_LOGIN_USERNAME);
		this.password = validate(conf.get(VEHICLE_LOGIN_PASSWORD), VEHICLE_LOGIN_PASSWORD);
		this.session = conf.get(VEHICLE_COOKIE_SESSION);

		this.params = validateOthers(conf.get(VEHICLE_LOGIN_PARAMS), VEHICLE_LOGIN_PARAMS);

		this.loginUrl = validateProperty(conf.get(VEHICLE_LOGIN_URL), VEHICLE_LOGIN_URL);

		String[] dateArrays = validateProperty(conf.get(VEHICLE_DATE_BETWEEN), VEHICLE_DATE_BETWEEN).split(":");
		if (!dateArrays[0].contains("+")) {
			dateArrays[0] = dateArrays[0] + "+00";
		}

		this.startDate = validate(Dates.str2DateWithPlusHour(dateArrays[0]), VEHICLE_DATE_BETWEEN);

		this.yesterdayCatchEnable = "true".equals(conf.get(VEHICLE_YESTERDAY_CATCH_ENABLE));

		if (dateArrays.length >= 2) {
			if (!dateArrays[1].contains("+")) { // 追加后缀
				dateArrays[1] = dateArrays[1] + "+59";
			}
			this.endDate = validate(Dates.str2DateWithPlusHour(dateArrays[1]), VEHICLE_DATE_BETWEEN);
		} else {
			this.endDate = new Date();
		}

		LOG.debug("settings for app:{}", this);
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public String getKakouPath() {
		return kakouPath;
	}

	public String getSysType() {
		return sysType;
	}

	public String getDst() {
		return dst;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public String getCompanyId() {
		return companyId;
	}

	public String getUsernameKey() {
		return username[0];
	}

	public String getUsernameValue() {
		return username[1];
	}

	public String getPasswordKey() {
		return password[0];
	}

	public String getPasswordValue() {
		return password[1];
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public String getCatchUrl() {
		return catchUrl;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public boolean isYesterdayCatchEnable() {
		return yesterdayCatchEnable;
	}

	public String[] getParams() {
		return params;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	@Override
	public String toString() {
		return "VehicleDataConfig [pageSize=" + pageSize + ", pageNo=" + pageNo + ", kakouPath=" + kakouPath + ", dst="
				+ dst + ", sysType=" + sysType + ", areaCode=" + areaCode + ", companyId=" + companyId + ", username="
				+ Arrays.toString(username) + ", password=" + Arrays.toString(password) + ", sessionKey=" + sessionKey
				+ ", loginUrl=" + loginUrl + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}

}
