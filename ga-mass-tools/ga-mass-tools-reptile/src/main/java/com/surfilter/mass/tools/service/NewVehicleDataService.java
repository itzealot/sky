package com.surfilter.mass.tools.service;

import static java.lang.String.format;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.surfilter.mass.tools.conf.CarInfoJson;
import com.surfilter.mass.tools.conf.CarInfoJson.Entities;
import com.surfilter.mass.tools.conf.VehicleConfig;
import com.surfilter.mass.tools.entity.VideoVehicleTrack;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.JsoupUtil;
import com.surfilter.mass.tools.util.NumberUtil;
import com.surfilter.mass.tools.util.Threads;
import com.surfilter.mass.tools.util.VehicleDataUtil;

/**
 * 抓取视频车辆数据的方法 for 新系统
 * 
 * @author zealot
 *
 */
public class NewVehicleDataService {

	static final Logger LOG = LoggerFactory.getLogger(NewVehicleDataService.class);
	private static final Random RANDOM = new Random();

	static final int LIMIT_TIMES = 5;
	static final char SPLITER = '/';
	static final String MINUTE_START_SUFIXX = "%3A00";
	static final String NUM_MINUTE_START_SUFIXX = "%3AstartMinuteNum";
	static final String MINUTE_END_SUFIXX = "%3A59";
	static final String NUM_MINUTE_END_SUFIXX = "%3AendMinuteNum";
	static final char PLUS_CHAR = '+';

	private VehicleConfig config;
	private KaKouInfoCache cache; // kakou信息缓存Map
	private String rightDir;
	private String wrongDir;

	public NewVehicleDataService(VehicleConfig config) {
		this.config = config;
		this.cache = KaKouInfoCache.getInstance(config.getKakouPath());
		init();
	}

	private void init() {
		this.rightDir = this.config.getDst() + "json/";
		this.wrongDir = this.config.getDst() + "error/";
	}

	public void fetchByHour(String dateStr, int hour) {
		StringBuffer buffer = new StringBuffer(128);
		String date = dateStr.replace("-", "");
		String fillHour = JsoupUtil.fillWith0(hour);

		String jsonDir = buffer.append(this.rightDir).append(date).append(SPLITER).append(fillHour).append(SPLITER)
				.toString();
		buffer.setLength(0);
		new File(jsonDir).mkdirs();

		String errorDir = buffer.append(this.wrongDir).append(date).append(SPLITER).append(fillHour).append(SPLITER)
				.toString();
		buffer.setLength(0);
		new File(errorDir).mkdirs();

		String startDate = buffer.append(dateStr).append(PLUS_CHAR).append(fillHour).append(MINUTE_START_SUFIXX)
				.toString();
		buffer.setLength(0);

		String endDate = buffer.append(dateStr).append(PLUS_CHAR).append(fillHour).append(MINUTE_END_SUFIXX).toString();
		buffer.setLength(0);
		buffer = null;

		fetchByUrl(config.getCatchUrl(), startDate, endDate, jsonDir, errorDir);
	}

	public void fetchByUrl(String hourUrl, String startDate, String endDate, String jsonDir, String errorDir) {
		long start = System.currentTimeMillis() / 1000;
		int pageNo = config.getPageNo(); // 页码
		int pageSize = config.getPageSize();

		String cookie = config.getSession(); // 获取 cookie
		if (cookie == null || cookie.trim().isEmpty()) { // 需要重新登入
			cookie = login();
			// 保存相应的 cookie
			config.setSession(cookie);
		}

		String sessionKey = config.getSessionKey();

		int totalNum = 0;
		int sum = 0;
		int errorCounts = 0;
		boolean isPrintTotal = false;

		// 分页抓取数据
		while (true) {
			String url = urlReplace(startDate, endDate, String.valueOf(pageSize), String.valueOf(pageNo));
			Document doc = JsoupUtil.connect(url, sessionKey, config.getSession(), true);

			int times = 1;
			while (doc == null && times <= LIMIT_TIMES) { // 连接失败时重新连接指定次数
				Threads.sleep(times * RANDOM.nextInt(20) * 1000);
				doc = JsoupUtil.connect(url, sessionKey, config.getSession(), times == LIMIT_TIMES);
				times++;
			}

			if (doc == null) {// 使用 cookie连接失败，则需要重置cookie
				// 需要重新登入并且保存相应的 cookie
				config.setSession(login());
				LOG.warn(format("connect with cookie error, url:%s, sessionValue=%s, try login again.", url,
						config.getSession()));
				continue;
			}

			String result = doc.text();

			if (result == null || result.isEmpty() || result.contains("登录")) {
				// 需要重新登入并且保存相应的 cookie
				config.setSession(login());
				LOG.warn(format("get content by cookie error, url:%s, sessionValue=%s, try login again.", url,
						config.getSession()));
				continue;
			}

			CarInfoJson car = null;
			try {
				car = new Gson().fromJson(result, CarInfoJson.class);
				LOG.info("finish query vehicle info, hourStr:{}, pageNo:{}, cookie:{}", url, pageNo, cookie);
			} catch (Exception e) {
				LOG.error(format("convert json fail, html:%s", result), e);
				break;
			}

			if (totalNum == 0) {
				totalNum = NumberUtil.parseValue(car.getTotals(), pageSize) / pageSize + 1;
			}

			// 是否抓取成功
			if (car != null && "SUCCESS".equalsIgnoreCase(car.getStatus()) && car.getEntities() != null) {
				List<Entities> entities = car.getEntities();

				if (!isPrintTotal) {
					LOG.debug("totals:{}, size:{}", car.getTotals(), entities.size());
					isPrintTotal = true;
				}

				List<VideoVehicleTrack> tracks = new ArrayList<>(pageSize);

				int min = Math.min(entities.size(), pageSize);

				for (int i = 0; i < min; i++) {
					VideoVehicleTrack data = VehicleDataUtil.map(entities.get(i), cache.getMap(), config.getAreaCode());

					if (data != null && data.getBPLATE_NUM().length() >= 6) {
						tracks.add(data);
					} else {
						errorCounts++;
					}
				}

				// write json
				if (tracks.size() > 0) {
					sum += tracks.size();
					FileUtil.writeWithJson(VehicleDataUtil.jsonFileName(jsonDir, config), tracks);
				}

				tracks = null;
			}

			pageNo++;

			if (pageNo > totalNum) {
				break;
			}
		}

		long spend = System.currentTimeMillis() / 1000 - start;
		LOG.info("finish write startDate:{}, endDate:{}, totalCounts:{}, errorCounts:{}, spends:{} s", startDate,
				endDate, sum, errorCounts, spend);

	}

	public String urlReplace(String startDate, String endDate, String pageSize, String pageNo) {
		return config.getCatchUrl().replace("startDate", startDate).replace("endDate", endDate)
				.replace("pageSizeNum", pageSize).replace("pageNo", pageNo);
	}

	/**
	 * 执行登入操作，登入成功返回 Session
	 * 
	 * @return
	 */
	private String login() {
		String[] keyvals = keyvals();
		Response res = JsoupUtil.login(config.getLoginUrl(), false, keyvals);

		int counts = 1;
		while (res == null && counts <= 20) { // 登入失败则重复登入
			Threads.sleep(counts * RANDOM.nextInt(20) * 1000);

			res = JsoupUtil.login(config.getLoginUrl(), true, keyvals);

			counts++;
		}

		if (res == null) {
			LOG.error(String.format("login error, url:{}, params:{}", config.getLoginUrl(), Arrays.asList(keyvals)),
					new IllegalArgumentException("login error, pleace check the login settings"));
			System.exit(1);
		}

		return JsoupUtil.cookie(res, config.getSessionKey()); // 获取 cookie
	}

	private String[] keyvals() {
		List<String> keyvals = new ArrayList<>(16);

		keyvals.add(config.getUsernameKey());
		keyvals.add(config.getUsernameValue());
		keyvals.add(config.getPasswordKey());
		keyvals.add(config.getPasswordValue());

		String[] params = config.getParams();
		if (params != null) {
			for (int i = 0, len = params.length; i < len; i++) {
				keyvals.add(params[i]);
			}
		}
		String[] results = new String[keyvals.size()];
		keyvals.toArray(results);

		return results;
	}
}
