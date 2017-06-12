package com.surfilter.mass.tools.conf;

import static com.surfilter.mass.tools.util.ValidateUtil.validateProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 车辆信息配置文件
 * 
 * @author zealot
 *
 */
public class CarInfoConfig {

	static final Logger LOG = LoggerFactory.getLogger(CarInfoConfig.class);

	public static final String CAR_INFO_URL = "car.info.url";
	public static final String CAR_INFO_PAGE_SIZE = "car.info.pageSize";
	public static final String CAR_INFO_PAGE_NO = "car.info.pageNo";
	public static final String CAR_INFO_MAX_PAGE_NO = "car.info.max.pageNo";
	public static final String CAR_INFO_DST = "car.info.dst";
	public static final int PAGE_SIZE = 2000;

	public static final String CAR_URL = "http://10.231.10.10:8888/EHL_ITGS/itgs/jsp/index/itgscommon.getPageUtil.action?pageNum=0&currPage=%s&exeSql=select+*++from+T_ITGS_VEHICLE&pageSize=%s&totalNum=0";

	private String catchUrl;
	private int pageSize;
	private int pageNo;
	private int maxPageNo;
	private String dst;

	public CarInfoConfig(MassConfiguration conf) {
		init(conf);
	}

	private void init(MassConfiguration conf) {
		this.catchUrl = validateProperty(conf.get(CAR_INFO_URL), CAR_INFO_URL);
		this.pageSize = conf.getInt(CAR_INFO_PAGE_SIZE, PAGE_SIZE);
		this.pageNo = conf.getInt(CAR_INFO_PAGE_NO, 1);
		this.maxPageNo = conf.getInt(CAR_INFO_MAX_PAGE_NO, 1517);
		this.dst = validateProperty(conf.get(CAR_INFO_DST), CAR_INFO_DST);

		LOG.debug("settings for app:{}", this);
	}

	public String getCatchUrl() {
		return catchUrl;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getMaxPageNo() {
		return maxPageNo;
	}

	public String getDst() {
		return dst;
	}

	@Override
	public String toString() {
		return "CarInfoConfig [catchUrl=" + catchUrl + ",pageSize=" + pageSize + ",dst=" + dst + ",pageNo=" + pageNo
				+ ",maxPageNo=" + maxPageNo + "]";
	}

}
