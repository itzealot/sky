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
public class ZtryRevokeConfig {

	static final Logger LOG = LoggerFactory.getLogger(ZtryRevokeConfig.class);

	/** ztry property */
	public static final String REVOKE_COOKIES = "revoke.cookies";
	public static final String REVOKE_URLS_DST = "revoke.urls.dst";
	public static final String REVOKE_CATCH_URL = "revoke.catch.url";
	public static final String REVOKE_CATCH_DATE = "revoke.catch.date";
	public static final String REVOKE_DETAIL_DST = "revoke.detail.dst";

	private Map<String, String> ztryCookies = new HashMap<>(4);
	private String ztryUrlsDst;
	private String ztryCatchUrl;
	private Date ztryCatchStartDate;
	private Date ztryCatchEndDate;
	private String ztryDetailDst;

	public ZtryRevokeConfig(MassConfiguration conf) {
		init(conf);
	}

	private void init(MassConfiguration conf) {
		initCookie(validateProperty(conf.get(REVOKE_COOKIES), REVOKE_COOKIES));

		this.ztryUrlsDst = FileUtil.pathWithSuffix(validateProperty(conf.get(REVOKE_URLS_DST), REVOKE_URLS_DST));
		this.ztryCatchUrl = validateProperty(conf.get(REVOKE_CATCH_URL), REVOKE_CATCH_URL);

		String[] dateArrays = validateProperty(conf.get(REVOKE_CATCH_DATE), REVOKE_CATCH_DATE).split(":");
		this.ztryCatchStartDate = validate(Dates.str2Year(dateArrays[0]), REVOKE_CATCH_DATE);

		if (dateArrays.length >= 2) {
			this.ztryCatchEndDate = validate(Dates.str2Year(dateArrays[1]), REVOKE_CATCH_DATE);
		} else {
			this.ztryCatchEndDate = new Date();
		}

		this.ztryDetailDst = FileUtil.pathWithSuffix(validateProperty(conf.get(REVOKE_DETAIL_DST), REVOKE_DETAIL_DST));

		LOG.debug("settings for app:{}", this);
	}

	private void initCookie(String cookie) {
		String[] vals = cookie.split(";");
		for (int i = 0, len = vals.length; i < len; i++) {
			String[] valArray = vals[i].split("\\|");
			if (valArray.length != 2) {
				throw new IllegalArgumentException("error settings for " + REVOKE_COOKIES + " config");
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
