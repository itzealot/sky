package com.surfilter.mass.tools.util;

import static com.surfilter.mass.tools.util.FileUtil.trim2Empty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.CarInfoJson.CachedBayonet;
import com.surfilter.mass.tools.conf.CarInfoJson.Entities;
import com.surfilter.mass.tools.conf.VehicleConfig;
import com.surfilter.mass.tools.entity.KaKouInfo;
import com.surfilter.mass.tools.entity.PageInfo;
import com.surfilter.mass.tools.entity.VehicleData;
import com.surfilter.mass.tools.entity.VideoVehicleTrack;

/**
 * VehicleUtil
 * 
 * @author zealot
 *
 */
public final class VehicleDataUtil {
	static final Logger LOG = LoggerFactory.getLogger(VehicleDataUtil.class);

	public static final String LOGIN_URL = "http://10.231.10.10:8888/EHL_ITGS/login.action";
	public static final String USERNAME_KEY = "pname";
	public static final String USERNAME = "007209";
	public static final String PASSWORD_KEY = "pwd";
	public static final String PASSWORD = "tsrj85086111";

	/** 视频车辆数据爬取url及参数 */
	public static final String VEHICLE_URL_HEADER = "http://10.231.10.10:8888/EHL_ITGS/itgs/jsp/jcfk/txclcx.queryPassCar.action?param=";
	/** 历城视频车辆数据url */
	public static final String LICHENG_VEHICLE_URL = VEHICLE_URL_HEADER
			+ "%2C%2C%2C3701979105%3B3701126033%3B3701126035%3B3701126036%3B3701126037%3B3701126038%3B3701126039%3B3701126041%3B3701126043%3B3701126045%3B3701126046%3B3701126048%3B3701126003%3B3701126007%3B3701126008%3B3701126010%3B3701126011%3B3701126013%3B3701126014%3B3701126015%3B3701126016%3B3701126017%3B3701126018%3B3701126020%3B3701126022%3B3701126023%3B3701126024%3B3701126025%3B3701126027%3B3701126030%3B3701126050%3B3701126001%3B3701126002%3B3701126026%3B3701126029%3B3701126034%3B3701126040%3B3701126042%3B3701126044%3B3701126047%3B3701126051%3B3701126109%3B3701126028%3B3701126645%3B3701126646%3B3701126647%3B3701126012%3B3701126019%3B3701126006%3B3701126101%3B3701126102%3B3701126103%3B3701126104%3B3701126105%3B3701126106%3B3701126107%3B3701126108%3B3701126032%3B3701126049%3B3701126115%3B3701126116%3B3701126117%3B3701126118%3B3701126119%3B3701126120%3B3701126121%3B3701126122%3B3701126123%3B3701126124%3B3701126125%3B3701126126%3B3701126128%3B3701126129%3B3701126062%3B3701126064%3B3701126127%3B3701126072%3B3701126074%3B3701126075%3B3701126068%3B3701126060%3B3701126067%3B3701126069%3B3701126071%3B3701126076%3B3701126080%3B3701126061%3B3701126134%3B3701126137%3B3701126636%3B3701126637%3B3701126065%3B3701126070%3B3701126073%3B3701126609%3B3701126608%3B3701126133%3B3701126136%3B3701126610%3B3701126611%3B3701126612%3B3701126613%3B3701126139%3B3701126620%3B3701126077%3B3701126704%3B3701126205%3B3701126206%3B3701126135%3B3701126063%3B3701126066%3B3701126079%3B3701126078%3B3701126614%3B3701126615%3B3701126623%3B3701126619%3B3701126618%3B3701126625%3B3701126617%3B3701126616%3B3701126639%3B3701126638%3B3701126508%3B3701126635%3B3701126634%3B3701126633%3B3701126632%3B3701126626%3B3701126627%3B3701126628%3B3701126640%3B3701126641%3B3701126642%3B3701126643%3B3701126644%3B3701126629%3B3701126630%3B3701126631%3B3701126083%3B3701126084%3B3701126086%3B3701055041%3B3701126087%3B3701126088%3B3701055040%3B3701126518%3B3701126516%3B3701126095%3B3701126501%3B3701126502%3B3701126503%3B3701126504%3B3701126506%3B3701126507%3B3701126510%3B3701126512%3B3701126085%3B3701126082%3B3701126517%3B3701126702%3B3701126701%2CstartHour%3A00%2CendHour%3A59%2C%3B%3B%3B%3B%2C%3B%3B%2CcarSpeed%3D%40%26pageNo%26pageSize%26-1";

	/** default pageSize */
	public static final int PAGE_SIZE = 1000;

	/**
	 * 视频车辆数据
	 * 
	 * @param doc
	 * @param page
	 * @param map
	 * @param jsonPath
	 * @param errorJsonPath
	 */
	public static boolean catchVehicleData(Document doc, PageInfo page, Map<String, KaKouInfo> map, String jsonPath,
			String errorJsonPath, String areaCode) {
		if (doc == null) {
			return false;
		}
		LOG.info("html:{}", doc.html());
		Elements userdatas = doc.getElementsByTag("userdata"); // 获取数据
		if (userdatas == null || userdatas.size() < 2) {
			LOG.error("query userdatas fail.........................");
			return false;
		}

		String[] pages = userdatas.get(0).html().split(";");// 分页数据
		if (pages.length < 2) {
			LOG.error("query pages fail, pages:{}.........................", Arrays.asList(pages));
			return false;
		}

		int pageSize = page.getPageSize();
		List<VideoVehicleTrack> datas = new ArrayList<>(pageSize); // 有牌照数据
		List<VideoVehicleTrack> errorDatas = new ArrayList<>(pageSize); // 无牌数据

		int pageTotal = NumberUtil.parseInt(pages[0]); // 页数
		int counts = NumberUtil.parseInt(pages[1]); // 总数

		if (page.getCounts() == -1) { // 第一次爬取，打印爬取信息
			LOG.info("query page info, pageTotal:{}, counts:{}", pageTotal, counts);
		}

		page.setCounts(counts);
		page.setCurrentPage(page.getCurrentPage() + 1);

		Elements rows = doc.getElementsByTag("row");

		for (Element row : rows) {
			List<Node> nodes = row.childNodes();
			if (nodes != null) {
				VehicleData data = parseVehicleData(nodes);
				if (data != null) {
					VideoVehicleTrack track = map(data, map, areaCode);
					if (track != null) {
						if (track.getPLATE_NUM().length() < 6) { // 错误车牌
							errorDatas.add(track);
						} else {
							datas.add(track);
						}
					}
				}
			}
		}

		FileUtil.writeWithJson(jsonPath, datas); // write json
		datas = null;
		FileUtil.writeWithJson(errorJsonPath, errorDatas); // write json
		errorDatas = null;
		return true;
	}

	private static VideoVehicleTrack map(VehicleData data, Map<String, KaKouInfo> map, String areaCode) {
		VideoVehicleTrack track = new VideoVehicleTrack();
		String kakouName = data.getAddress();
		KaKouInfo info = map.get(kakouName);
		if (info == null) { // 从缓存中未查询到卡口名称
			LOG.error("error query from kakou map, kakouName:{}", kakouName);
			return null;
		}
		int time = NumberUtil.parseInt(Dates.unixTime(data.getPassTime(), "yyyy-MM-dd HH:mm") + "");

		if (time < 1) {
			LOG.error("error time VehicleData from web, {}", data);
			return null;
		}

		track.setPASS_TIME(time);

		track.setDEVICE_NUM(info.getId());
		track.setDEVICE_NAME(kakouName);
		track.setXPOINT(info.getLongtitude());
		track.setYPOINT(info.getLatitude());
		track.setAREA_CODE(areaCode);
		track.setADDR(info.getAddress());

		track.setDIRECTION_NAME(data.getDirection());
		track.setPLATE_NUM(data.getCarNumber());
		track.setBPLATE_NUM(data.getCarNumber());
		track.setBRAND(data.getType());
		track.setCOLOR(data.getColor());
		track.setIMAGE(data.getPhoto());

		return track;
	}

	public static VideoVehicleTrack map(Entities data, Map<String, KaKouInfo> map, String areaCode) {
		VideoVehicleTrack track = new VideoVehicleTrack();
		String kakouName = data.getLocation();

		int time = NumberUtil.parseInt(String.valueOf(Dates.unixTime(data.getTravelTime(), "yyyy-MM-dd HH:mm")));

		if (time < 1) {
			LOG.error("error time VehicleData from web, {}", data);
			return null;
		}

		CachedBayonet cachedBayonet = data.getCachedBayonet();

		if (cachedBayonet == null) {
			LOG.error("cachedBayonet is null from json.");
		}

		float x = NumberUtil.parseFloat(cachedBayonet.getX());
		float y = NumberUtil.parseFloat(cachedBayonet.getY());

		// 设置经纬度
		track.setXPOINT(cachedBayonet.getX());
		track.setYPOINT(cachedBayonet.getY());

		if (x < 1F || y < 1F) { // 经纬度不合法，从卡口中获取
			KaKouInfo info = map.get(kakouName);

			if (info == null) { // 从缓存中未查询到卡口名称
				return null;
			}

			float xx = NumberUtil.parseFloat(info.getLongtitude());
			float yy = NumberUtil.parseFloat(info.getLatitude());

			if (xx < 1F || yy < 1F) {
				return null;
			}

			track.setXPOINT(info.getLongtitude());
			track.setYPOINT(info.getLatitude());
		}

		track.setPASS_TIME(time);

		track.setDEVICE_NUM(data.getKkid());
		track.setDEVICE_NAME(kakouName);
		track.setAREA_CODE(areaCode);
		track.setADDR(kakouName);

		track.setDIRECTION_NAME(data.getTravelOrientationStr());
		track.setPLATE_NUM(data.getPlateNumber());
		track.setBPLATE_NUM(data.getPlateNumber());
		track.setBRAND(data.getPlateTypeStr());
		track.setCOLOR(data.getVehicleColorStr());
		track.setIMAGE(data.getImgUrl());

		return track;
	}

	public static VehicleData parseVehicleData(List<Node> nodes) {
		if (nodes == null || nodes.isEmpty()) {
			return null;
		}

		String passTime = trim2Empty(nodes.get(0).toString());
		String address = trim2Empty(nodes.get(1).toString());
		String carNumber = trim2Empty(nodes.get(2).toString());
		String type = trim2Empty(nodes.get(4).toString());
		String color = trim2Empty(nodes.get(6).toString());
		String direction = trim2Empty(nodes.get(7).toString());
		String speed = trim2Empty(nodes.get(8).toString());
		String photo = trim2Empty(nodes.get(9).toString());

		return new VehicleData(passTime, address, carNumber, type, color, direction, speed, photo);
	}

	/**
	 * 拼接相应的时间字符串
	 * 
	 * @param dateStr
	 * @param hour
	 * @return [like 2017-03-01+01]
	 */
	public static String timeStr(String dateStr, int hour) {
		return dateStr + "+" + JsoupUtil.fillWith0(hour);
	}

	public static String pageUrl(String url, String pageNo, String pageSize) {
		return url.replace("pageNo", pageNo).replace("pageSize", pageSize);
	}

	/**
	 * 随机产生视频车辆数据json的文件名称
	 * 
	 * @param config
	 * @return
	 */
	public static String jsonFileName(String dir, VehicleConfig config) {
		return dir + Dates.date2Str(new Date(), "yyyyMMddHHmmss") + FileUtil.random() + "_" + config.getSysType() + "_"
				+ config.getAreaCode() + "_" + config.getCompanyId() + "_016.log";
	}

	/**
	 * 时间替换 url
	 * 
	 * @param url
	 * @param startHour
	 *            like 2017-03-01+08
	 * @param endHour
	 *            like 2017-03-01+09
	 * @return
	 */
	public static String hourUrl(String url, String startHour, String endHour) {
		return url.replace("startHour", startHour).replace("endHour", endHour);
	}

	public static String liChengVehicleUrl(String startHour, String pageNo, String pageSize) {
		return pageUrl(hourUrl(LICHENG_VEHICLE_URL, startHour, startHour), pageNo, pageSize);
	}

	public static Response login() {
		return JsoupUtil.login(LOGIN_URL, USERNAME_KEY, USERNAME, PASSWORD_KEY, PASSWORD);
	}

	private VehicleDataUtil() {
	}
}
