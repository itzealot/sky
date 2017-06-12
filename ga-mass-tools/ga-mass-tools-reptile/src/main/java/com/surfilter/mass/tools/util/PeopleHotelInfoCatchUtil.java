package com.surfilter.mass.tools.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.entity.PeopleHotelInfo;
import static com.surfilter.mass.tools.util.FileUtil.trim2Null;

/**
 * 旅社信息 url
 * 
 * @author zealot
 *
 */
public final class PeopleHotelInfoCatchUtil {

	static final Logger LOG = LoggerFactory.getLogger(PeopleHotelInfoCatchUtil.class);

	/** hotel page info url */
	public static final String HOTEL_PAGE = "http://10.49.6.171/hotel/SameRoom.asp?sql=select%20*%20from%20(select%20%2F*%2B%20FIRST_ROWS(pageSize)%20*%2F%20A.*%2C%20ROWNUM%20RN%20from%20(select%20*%20from%20v_ch_all_web%20where%20INSERT_TIME%3E%3DstartTime%20and%20INSERT_TIME%3C%3DendTime)%20A%20where%20ROWNUM%20%3C%3D%20recordEnd)%20where%20RN%20%3E%3D%20recordStart";
	public static final String HOTEL_PAGE_ONE = "http://10.49.6.171/hotel/SameRoom.asp?sql=select%20/*+%20FIRST_ROWS%20*/%20*%20from%20(select%20A.*,%20ROWNUM%20RN%20from%20(select%20*%20from%20v_ch_all_web%20where%20INSERT_TIME%3E=startTime%20and%20INSERT_TIME%3C=endTime)%20A%20where%20ROWNUM%20%3C=%20recordEnd)%20where%20RN%20%3E=%20recordStart";

	/** 元素选择器 */
	public static final String NAME_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(1) > td:nth-child(2)"; //
	public static final String HOTELID_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(1) > td:nth-child(5)"; //
	public static final String SEX_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(2) > td:nth-child(2)"; //
	public static final String NATION_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(2) > td:nth-child(4)"; //
	public static final String BDATE_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(3) > td:nth-child(2)"; //
	public static final String ID_NAME_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(3) > td:nth-child(4)"; //
	public static final String ID_CODE_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(4) > td:nth-child(2)"; //
	public static final String XZQH_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(4) > td:nth-child(4)"; //
	public static final String ADDRESS_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(5) > td:nth-child(2)"; //
	public static final String HOTEL_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(5) > td:nth-child(4)"; //
	public static final String NO_ROOM_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(6) > td:nth-child(2)"; //
	public static final String IN_TIME_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(6) > td:nth-child(4)"; //
	public static final String OUT_TIME_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(7) > td:nth-child(2)"; //
	public static final String BUR_CODE_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(7) > td:nth-child(4)"; //
	public static final String STA_CODE_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(8) > td:nth-child(2)"; //
	public static final String PHOTO_SELECTOR = "body > table:nth-child({ID}) > tbody > tr:nth-child(1) > td:nth-child(3) > img"; //

	/** 图片地址 */
	public static final String HOTEL_HEAD = "http://10.49.6.171/hotel/";
	public static final int PAGE_SIZE = 100;

	/**
	 * 根据 rowId 获取当前页的所有选择器
	 * 
	 * @param rowId
	 *            [2-(pageSize + 1)]
	 * @return
	 */
	public static List<String> fetchSelectors(String rowId) {
		List<String> selectors = new ArrayList<>(16);

		selectors.add(NAME_SELECTOR.replace("{ID}", rowId));
		selectors.add(HOTELID_SELECTOR.replace("{ID}", rowId));
		selectors.add(SEX_SELECTOR.replace("{ID}", rowId));
		selectors.add(NATION_SELECTOR.replace("{ID}", rowId));
		selectors.add(BDATE_SELECTOR.replace("{ID}", rowId));
		selectors.add(ID_NAME_SELECTOR.replace("{ID}", rowId));
		selectors.add(ID_CODE_SELECTOR.replace("{ID}", rowId));
		selectors.add(XZQH_SELECTOR.replace("{ID}", rowId));
		selectors.add(ADDRESS_SELECTOR.replace("{ID}", rowId));
		selectors.add(HOTEL_SELECTOR.replace("{ID}", rowId));
		selectors.add(NO_ROOM_SELECTOR.replace("{ID}", rowId));
		selectors.add(IN_TIME_SELECTOR.replace("{ID}", rowId));
		selectors.add(OUT_TIME_SELECTOR.replace("{ID}", rowId));
		selectors.add(BUR_CODE_SELECTOR.replace("{ID}", rowId));
		selectors.add(STA_CODE_SELECTOR.replace("{ID}", rowId));
		selectors.add(PHOTO_SELECTOR.replace("{ID}", rowId));

		return selectors;
	}

	/**
	 * 抓取旅馆住宿信息
	 * 
	 * @param recordStart
	 * @param pageSize
	 * @param pageCounts
	 *            查找多少页内的数据
	 */
	public static void fetchPeopleHotelInfos(int recordStart, int pageSize, int pageCounts, File file) {
		int sum = 0;
		while (sum < pageCounts) {
			String url = fetchPageUrl(recordStart, pageSize);
			List<String> results = fetchPagePeopleHotelInfos(url, pageSize);
			FileUtil.append(file, results);
			sum++;
			if (results.size() < pageSize) {
				LOG.debug("finish fetch all people hotel infos");
				break;
			}

			results.clear();
			results = null;
		}
	}

	/**
	 * 抓取旅馆住宿信息
	 * 
	 * @param recordStart
	 * @param pageSize
	 * @param pageCounts
	 *            查找多少页内的数据
	 * @param startTime
	 *            yyyyMMddHHmm
	 * @param endTime
	 *            yyyyMMddHHmm
	 */
	public static void fetchPeopleHotelInfos(int recordStart, int pageSize, int pageCounts, String startTime,
			String endTime, File file) {
		int sum = 0;
		int start = recordStart;

		while (sum < pageCounts) {
			String url = fetchPageUrl(start, pageSize, startTime, endTime);
			List<String> results = fetchPagePeopleHotelInfos(url, pageSize);
			int size = 0;

			if (results != null) {
				size = results.size();
				FileUtil.append(file, results);
				results.clear();
			}
			results = null;

			sum++;
			start += pageSize;

			if (size != 0 && size < pageSize) {
				LOG.debug("finish fetch all people hotel infos");
				break;
			}

			Threads.sleep(1000);
		}
	}

	public static String fetchPageUrl(int recordStart, int pageSize) {
		return HOTEL_PAGE_ONE.replace("recordStart", recordStart + "").replace("recordEnd",
				(recordStart + pageSize) + "");
	}

	public static String fetchPageUrl2(int recordStart, int pageSize, String startTime, String endTime) {
		return HOTEL_PAGE_ONE.replace("recordStart", recordStart + "")
				.replace("recordEnd", (recordStart + pageSize - 1) + "").replace("startTime", startTime)
				.replace("endTime", endTime);
	}

	public static String fetchPageUrl(int recordStart, int pageSize, String startTime, String endTime) {
		return HOTEL_PAGE.replace("recordStart", recordStart + "")
				.replace("recordEnd", (recordStart + pageSize - 1) + "").replace("startTime", startTime)
				.replace("endTime", endTime).replace("pageSize", pageSize + "");
	}

	/**
	 * 根据 url 抓取某一页的旅馆住宿信息
	 * 
	 * @param url
	 * @param pageSize
	 * @return
	 */
	public static List<String> fetchPagePeopleHotelInfos(String url, int pageSize) {
		try {
			Document doc = Jsoup.parse(new URL(url).openStream(), "GBK", url);
			List<String> results = new ArrayList<>(pageSize);
			int len = pageSize + 1;
			StringBuffer buffer = new StringBuffer(256);

			for (int i = 2; i <= len; i++) {
				List<String> selectors = fetchSelectors(i + "");

				String name = trim2Null(doc.select(selectors.get(0)).html());
				String hotelId = trim2Null(doc.select(selectors.get(1)).html());
				String sex = trim2Null(doc.select(selectors.get(2)).html());
				String nation = trim2Null(doc.select(selectors.get(3)).html());
				String bDate = trim2Null(doc.select(selectors.get(4)).html());
				String idName = trim2Null(doc.select(selectors.get(5)).html());
				String idCode = trim2Null(doc.select(selectors.get(6)).html());
				String xzqh = trim2Null(doc.select(selectors.get(7)).html());
				String adress = trim2Null(doc.select(selectors.get(8)).html());
				String hotel = trim2Null(doc.select(selectors.get(9)).html());
				String noRoom = trim2Null(doc.select(selectors.get(10)).html());
				String inTime = trim2Null(doc.select(selectors.get(11)).html());
				String outTime = trim2Null(doc.select(selectors.get(12)).html());
				String burCode = trim2Null(doc.select(selectors.get(13)).html());
				String staCode = trim2Null(doc.select(selectors.get(14)).html());
				Elements select = doc.select(selectors.get(15));
				String photo = "null";
				if (select.size() > 0) {
					photo = HOTEL_HEAD + select.get(0).attr("src");
				}
				PeopleHotelInfo info = new PeopleHotelInfo(name, hotelId, sex, nation, bDate, idName, idCode, xzqh,
						adress, hotel, noRoom, inTime, outTime, burCode, staCode, photo);
				if (idCode != null) {
					results.add(info.map2Str(buffer));
				}
			}
			LOG.debug("finish fetch people hotel infos, size:{}, url:{}", results.size(), url);
			return results;
		} catch (Exception e) {
			LOG.error("fetch people hotel infos error, url:{}, {}", url, e);
			e.printStackTrace();
			return null;
		}
	}

	private PeopleHotelInfoCatchUtil() {
	}
}
