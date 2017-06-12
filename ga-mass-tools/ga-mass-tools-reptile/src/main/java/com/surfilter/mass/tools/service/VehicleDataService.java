package com.surfilter.mass.tools.service;

import java.io.File;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.VehicleConfig;
import com.surfilter.mass.tools.entity.PageInfo;
import com.surfilter.mass.tools.util.JsoupUtil;
import com.surfilter.mass.tools.util.Threads;
import com.surfilter.mass.tools.util.VehicleDataUtil;

/**
 * 抓取视频车辆数据的方法
 * 
 * @author zealot
 *
 */
public class VehicleDataService {

	static final Logger LOG = LoggerFactory.getLogger(VehicleDataService.class);
	static final int LIMIT_TIMES = 5;

	private VehicleConfig config;
	private KaKouInfoCache cache; // kakou信息缓存Map
	private String rightDir;
	private String wrongDir;

	public VehicleDataService(VehicleConfig config) {
		this.config = config;
		this.cache = KaKouInfoCache.getInstance(config.getKakouPath());
		init();
	}

	private void init() {
		this.rightDir = this.config.getDst() + "json/";
		this.wrongDir = this.config.getDst() + "error/";
	}

	public void execute(String dateStr) {
		for (int i = 0; i < 24; i++) { // 抓取一天的数据
			String str = dateStr.replace("-", "");
			String jsonDir = this.rightDir + str + "/" + JsoupUtil.fillWith0(i) + "/";
			String errorDir = this.wrongDir + str + "/" + JsoupUtil.fillWith0(i) + "/";
			new File(jsonDir).mkdirs();
			new File(errorDir).mkdirs();
			catchWithTimeStr(VehicleDataUtil.timeStr(dateStr, i), jsonDir, errorDir);
		}
	}

	/**
	 * 根据时间查询出相应的结果
	 * 
	 * @param hourStr
	 */
	private void catchWithTimeStr(String hourStr, String jsonDir, String errorDir) {
		Response res = login();

		int pageNo = config.getPageNo(); // 页码

		PageInfo page = new PageInfo(-1, -1, pageNo, config.getPageSize());

		String sessionKey = config.getSessionKey();
		String cookie = JsoupUtil.cookie(res, sessionKey); // 获取 cookie

		String url = VehicleDataUtil.hourUrl(
				VehicleDataUtil.pageUrl(config.getCatchUrl(), pageNo + "", config.getPageSize() + ""), hourStr,
				hourStr);
		LOG.info("start query vehicle info, hourStr:{}, pageNo:{}, cookie:{}", hourStr, pageNo, cookie);
		Document doc = JsoupUtil.connect(url, sessionKey, cookie, false);

		int times = 1;
		while (doc == null && times <= LIMIT_TIMES) { // 连接失败时重新连接指定次数
			Threads.sleep(60 * 1000);
			doc = JsoupUtil.connect(url, sessionKey, cookie, times == LIMIT_TIMES);
			times++;
		}

		if (doc == null) {
			LOG.error(String.format("connect with cookie error, url:%s, sessionKey=%s, sessionValue=%s", url,
					sessionKey, cookie));
			return;
		}

		boolean flag = VehicleDataUtil.catchVehicleData(doc, page, cache.getMap(),
				VehicleDataUtil.jsonFileName(jsonDir, config), VehicleDataUtil.jsonFileName(errorDir, config),
				config.getAreaCode());

		if (!flag) {
			LOG.error("query fail, hourStr:{}, pageNo:{}", hourStr, pageNo);
			return;
		}

		Threads.sleep(1000);

		// 按照分页抓取所有的
		for (int i = page.getCurrentPage(); i <= page.getPageTotal(); i++) {
			url = VehicleDataUtil.hourUrl(
					VehicleDataUtil.pageUrl(config.getCatchUrl(), pageNo + "", config.getPageSize() + ""), hourStr,
					hourStr);
			doc = JsoupUtil.connect(url, config.getSessionKey(), cookie, false);

			times = 1;
			while (doc == null && times <= LIMIT_TIMES) { // 连接失败时重新连接指定次数
				Threads.sleep(60 * 1000);
				doc = JsoupUtil.connect(url, sessionKey, cookie, times == LIMIT_TIMES);
				times++;
			}

			if (doc == null) {
				LOG.error(String.format("connect with cookie error, url:%s, sessionKey=%s, sessionValue=%s", url,
						sessionKey, cookie));
				return;
			}

			flag = VehicleDataUtil.catchVehicleData(doc, page, cache.getMap(),
					VehicleDataUtil.jsonFileName(jsonDir, config), VehicleDataUtil.jsonFileName(errorDir, config),
					config.getAreaCode());
			if (!flag) {
				LOG.error("query fail, pageNo:{}, url:{}", pageNo, url);
			}
			Threads.sleep(1000);
		}
	}

	private Response login() {
		Response res = JsoupUtil.login(config.getLoginUrl(), config.getUsernameKey(), config.getUsernameValue(),
				config.getPasswordKey(), config.getPasswordValue());

		int counts = 1;
		while (res == null && counts < 20) { // 登入失败则重复登入
			Threads.sleep(counts * 60 * 1000);
			res = JsoupUtil.login(config.getLoginUrl(), config.getUsernameKey(), config.getUsernameValue(),
					config.getPasswordKey(), config.getPasswordValue());
			counts++;
		}

		if (res == null) {
			LOG.error("login error, url:{}, counts:{}", VehicleDataUtil.LOGIN_URL, counts);
			LOG.error("login fail.", new IllegalArgumentException("login error, pleace check the login settings"));
			System.exit(1);
		}

		return res;
	}
}
