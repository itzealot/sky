package com.surfilter.mass.tools.conf;

import static com.surfilter.mass.tools.util.ValidateUtil.validate;
import static com.surfilter.mass.tools.util.ValidateUtil.validateProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.util.Dates;
import com.surfilter.mass.tools.util.FileUtil;

/**
 * 在逃人员查询及撤销查询配置类
 * 
 * @author zealot
 *
 */
public class ZtryConfig {

	static final Logger LOG = LoggerFactory.getLogger(ZtryConfig.class);

	/** ztry property */
	public static final String ZTRY_COOKIES = "ztry.cookies";
	public static final String ZTRY_URLS_DST = "ztry.urls.dst";
	public static final String ZTRY_CATCH_URL = "ztry.catch.url";
	public static final String ZTRY_CATCH_DATE = "ztry.catch.date";
	public static final String ZTRY_DETAIL_DST = "ztry.detail.dst";

	private Map<String, String> ztryCookies = new HashMap<>(4);
	private String ztryUrlsDst;
	private String ztryCatchUrl;
	private Date ztryCatchStartDate;
	private Date ztryCatchEndDate;
	private String ztryDetailDst;

	public ZtryConfig(MassConfiguration conf) {
		init(conf);
	}

	private void init(MassConfiguration conf) {
		initCookie(validateProperty(conf.get(ZTRY_COOKIES), ZTRY_COOKIES));

		this.ztryUrlsDst = FileUtil.pathWithSuffix(validateProperty(conf.get(ZTRY_URLS_DST), ZTRY_URLS_DST));
		this.ztryCatchUrl = validateProperty(conf.get(ZTRY_CATCH_URL), ZTRY_CATCH_URL);

		String[] dateArrays = validateProperty(conf.get(ZTRY_CATCH_DATE), ZTRY_CATCH_DATE).split(":");
		this.ztryCatchStartDate = validate(Dates.str2Year(dateArrays[0]), ZTRY_CATCH_DATE);

		if (dateArrays.length >= 2) {
			this.ztryCatchEndDate = validate(Dates.str2Year(dateArrays[1]), ZTRY_CATCH_DATE);
		} else {
			this.ztryCatchEndDate = new Date();
		}

		this.ztryDetailDst = FileUtil.pathWithSuffix(validateProperty(conf.get(ZTRY_DETAIL_DST), ZTRY_DETAIL_DST));

		LOG.debug("settings for app:{}", this);
	}

	private void initCookie(String cookie) {
		String[] vals = cookie.split(";");
		for (int i = 0, len = vals.length; i < len; i++) {
			String[] valArray = vals[i].split("\\|");
			if (valArray.length != 2) {
				throw new IllegalArgumentException("error settings for " + ZTRY_COOKIES + " config");
			}
			ztryCookies.put(valArray[0], valArray[1]);
		}
	}

	public Map<String, String> getZtryCookies() {
		return ztryCookies;
	}

	public String getZtryUrlsDst() {
		return ztryUrlsDst;
	}

	public String getZtryCatchUrl() {
		return ztryCatchUrl;
	}

	public String getZtryDetailDst() {
		return ztryDetailDst;
	}

	public Date getZtryCatchStartDate() {
		return ztryCatchStartDate;
	}

	public Date getZtryCatchEndDate() {
		return ztryCatchEndDate;
	}

	@Override
	public String toString() {
		return "ZtryConfig [ztryCookies=" + ztryCookies + ",ztryUrlsDst=" + ztryUrlsDst + ",ztryCatchUrl="
				+ ztryCatchUrl + ",ztryCatchStartDate=" + ztryCatchStartDate + ",ztryCatchEndDate=" + ztryCatchEndDate
				+ ",ztryDetailDst=" + ztryDetailDst + "]";
	}

}
