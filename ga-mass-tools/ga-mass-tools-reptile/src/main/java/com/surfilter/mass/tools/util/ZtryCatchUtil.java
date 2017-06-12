package com.surfilter.mass.tools.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.entity.ZtryData;
import com.surfilter.mass.tools.entity.ZtryPageInfo;
import com.surfilter.mass.tools.entity.ZtryRevokedData;
import static com.surfilter.mass.tools.util.FileUtil.trim2Null;

/**
 * 在逃人员查询工具类
 * 
 * @author zealot
 *
 */
public final class ZtryCatchUtil {
	static final Logger LOG = LoggerFactory.getLogger(ZtryCatchUtil.class);

	/** 在逃查询人员对应的 selector */
	public static final String TID = "#mainTable > tbody > tr:nth-child(1) > td:nth-child(2)";
	public static final String NAME = "#mainTable > tbody > tr:nth-child(2) > td:nth-child(2)";
	public static final String NICKNAME = "#mainTable > tbody > tr:nth-child(2) > td:nth-child(4)";
	public static final String SEX = "#mainTable > tbody > tr:nth-child(3) > td:nth-child(2)";
	public static final String BIRTH = "#mainTable > tbody > tr:nth-child(3) > td:nth-child(4)";
	public static final String INTO_PROVINCE_DATE = "#base_table > tbody > tr:nth-child(4) > td > table > tbody > tr > td:nth-child(2)";
	public static final String INTO_DEPT_DATE = "#base_table > tbody > tr:nth-child(4) > td > table > tbody > tr > td:nth-child(4)";
	public static final String LAST_MODIFY_TIME = "#base_table > tbody > tr:nth-child(4) > td > table > tbody > tr > td:nth-child(6)";
	public static final String CERTIFICATE = "#mainTable > tbody > tr:nth-child(4) > td:nth-child(2)";
	public static final String NATION = "#mainTable > tbody > tr:nth-child(4) > td:nth-child(4)";
	public static final String OTHER_CERT1 = "#mainTable > tbody > tr:nth-child(5) > td.TableDetail1";
	public static final String OTHER_CERT2 = "#mainTable > tbody > tr:nth-child(6) > td";
	public static final String HEIGHT = "#mainTable > tbody > tr:nth-child(7) > td:nth-child(2)";
	public static final String KOUYIN = "#mainTable > tbody > tr:nth-child(7) > td:nth-child(4)";
	public static final String CAREER = "#mainTable > tbody > tr:nth-child(8) > td.TableDetail1";
	public static final String CENSUS_ADDR = "#mainTable > tbody > tr:nth-child(9) > td.TableDetail1";
	public static final String ADDRESS = "#mainTable > tbody > tr:nth-child(10) > td.TableDetail1";
	public static final String CENSUS = "#mainTable > tbody > tr:nth-child(11) > td.TableDetail1";
	public static final String FEATURES = "#mainTable > tbody > tr:nth-child(12) > td.TableDetail1";
	public static final String SPECIAL = "#mainTable > tbody > tr:nth-child(13) > td.TableDetail1";
	public static final String CASE_NO = "#mainTable > tbody > tr:nth-child(14) > td.TableDetail1";
	public static final String CASE_TYPE = "#mainTable > tbody > tr:nth-child(15) > td.TableDetail1";
	public static final String CASE_REMARK = "#mainTable > tbody > tr:nth-child(16) > td.TableDetail1";
	public static final String AWAY_DATE = "#mainTable > tbody > tr:nth-child(17) > td:nth-child(2)";
	public static final String AWAY_DIRECTION = "#mainTable > tbody > tr:nth-child(17) > td:nth-child(4)";
	public static final String AWAY_TYPE = "#mainTable > tbody > tr:nth-child(17) > td:nth-child(6)";
	public static final String LAW_DATE = "#mainTable > tbody > tr:nth-child(18) > td:nth-child(4)";
	public static final String TJL = "#mainTable > tbody > tr:nth-child(19) > td:nth-child(2)";
	public static final String TJL_TYPE = "#mainTable > tbody > tr:nth-child(19) > td:nth-child(4)";
	public static final String TJL_MONEY = "#mainTable > tbody > tr:nth-child(19) > td:nth-child(6)";
	public static final String OWN_DEPT = "#mainTable > tbody > tr:nth-child(20) > td:nth-child(2)";
	public static final String DEPT_TYPE = "#mainTable > tbody > tr:nth-child(20) > td:nth-child(4)";
	public static final String RECEIVE_DEPT = "#mainTable > tbody > tr:nth-child(21) > td:nth-child(2)";
	public static final String RECEIVE_DATE = "#mainTable > tbody > tr:nth-child(21) > td:nth-child(4)";
	public static final String RECEIVER = "#mainTable > tbody > tr:nth-child(22) > td:nth-child(2)";
	public static final String RECEIVER_PHONE = "#mainTable > tbody > tr:nth-child(22) > td:nth-child(4)";
	public static final String REGISTER = "#mainTable > tbody > tr:nth-child(23) > td:nth-child(2)";
	public static final String REGIST_DATE = "#mainTable > tbody > tr:nth-child(23) > td:nth-child(4)";
	public static final String REGIST_AUTHOR = "#mainTable > tbody > tr:nth-child(23) > td:nth-child(6)";
	public static final String BL_REASON = "#mainTable > tbody > tr:nth-child(24) > td.TableDetail1";

	/** 法律文书 */
	public static final String LAW_BOOK_URL = "http://ztry.xz.ga/attch/viewattch?ryID=ztryId&tabname=ztry_flsx&orderCode=1";

	/** 图片地址 */
	public static final String PHOTO_URL = "http://ztry.xz.ga/attch/viewattch?ryID=ztryId&tabname=ztry_zp&orderCode=1";

	/** 撤销查询人员对应的 selector */
	public static final String CAUGHT_DATE = "#mainTable > tbody > tr:nth-child(25) > td:nth-child(2)"; // 抓获日期
	public static final String CAUGHT_WAY = "#mainTable > tbody > tr:nth-child(25) > td:nth-child(4)"; // 抓获方式
	public static final String CAUGHT_AREA = "#mainTable > tbody > tr:nth-child(26) > td.TableDetail1"; // 抓获地区
	public static final String CAUGHT_OFFICE = "#mainTable > tbody > tr:nth-child(27) > td:nth-child(2)"; // 抓获单位
	public static final String FINGERPRINT_NO = "#mainTable > tbody > tr:nth-child(28) > td:nth-child(2)"; // 指纹编号
	public static final String DNA_NO = "#mainTable > tbody > tr:nth-child(28) > td:nth-child(4)"; // DNA编号
	public static final String REVOKER = "#mainTable > tbody > tr:nth-child(29) > td:nth-child(2)"; // 撤销填表人
	public static final String REVOKE_DATE = "#mainTable > tbody > tr:nth-child(29) > td:nth-child(4)"; // 撤销日期
	public static final String REVOKE_AUTHOR = "#mainTable > tbody > tr:nth-child(29) > td:nth-child(6)"; // 撤销审批人
	public static final String REVOKE_PROVINCE_DATE = "#base_table > tbody > tr:nth-child(4) > td > table > tbody > tr:nth-child(2) > td:nth-child(2)"; // 入省撤销库时间
	public static final String REVOKE_DEPT_DATE = "#base_table > tbody > tr:nth-child(4) > td > table > tbody > tr:nth-child(2) > td:nth-child(4)"; // 入部撤销库时间
	public static final String REVOKE_LAST_MODIFY_DATE = "#base_table > tbody > tr:nth-child(4) > td > table > tbody > tr:nth-child(2) > td:nth-child(6)"; // 撤销最后修改时间
	public static final String REVOKE_BL_REASON = "#mainTable > tbody > tr:nth-child(30) > td.TableDetail1"; // 补录原因

	public static final String SPLITER = "\t";

	public static final String URL_HEAD = "http://ztry.xz.ga/ztrydj/";
	public static final String REVOKE_URL_HEAD = "http://ztry.xz.ga/ztrycx/";

	public static final String SESSION_KEY = "JSESSIONID";
	public static final Map<String, String> COOKIE = new HashMap<>();

	static {
		COOKIE.put(SESSION_KEY, "00002TF930iDS51dcQufHPcfNZL:15kbiorh8");
		COOKIE.put("X-Mapping-hknmkkgk", "0EFB67C8DB062BAB0622D4CA5F2031C3");
	}

	/**
	 * 抓取Ztry的url及详细信息
	 * 
	 * @param url
	 *            分页url
	 * @param urlsFile
	 *            urls file
	 * @param detailsFile
	 *            details file
	 * @param pageNo
	 *            当前页号
	 * @param info
	 * @param isRevoke
	 *            是否为撤销查询,true:是;false:否
	 */
	public static void fetchUrlAndDetail(String url, File urlsFile, File detailsFile, int pageNo, ZtryPageInfo info,
			boolean isRevoke) {
		if (url == null || url.isEmpty()) {// 递归结束条件
			return;
		}

		boolean flag = fetchDetailUrls(url, info, isRevoke);

		if (flag) { // 递归结束条件
			List<String> urls = info.getUrls();

			if (urls != null && !urls.isEmpty()) {
				FileUtil.append(urlsFile, urls);
				LOG.debug("finish write urls, size:{}, url:{}", urls.size(), url);
				List<String> datas = new ArrayList<>(info.getPageSize());

				for (String u : urls) { // 根据返回的url抓取所有的url详情
					String data = null;

					if (isRevoke) { // 是撤销查询
						data = fetchRevokeDetail(url);
					} else {
						data = fetchDetail(u);
					}

					if (data != null) {
						datas.add(data);
					}
				}
				urls.clear();

				if (!datas.isEmpty()) {
					FileUtil.append(detailsFile, datas);
					LOG.debug("finish write details, size:{}, url:{}", datas.size(), url);
					datas.clear();
				}
				datas = null;
			}
			urls = null;
		}

		if (info.getPageUrl() == null || info.getPageNo() < pageNo) { // 递归结束条件
			return;
		}

		fetchUrlAndDetail(info.getPageUrl(), urlsFile, detailsFile, info.getPageNo(), info, isRevoke);
	}

	/**
	 * 抓取在逃人员的url
	 * 
	 * @param url
	 *            分页url
	 * @param urlsFile
	 *            urls file
	 * @param pageNo
	 *            当前页号
	 * @param info
	 * @param isRevoke
	 *            是否为撤销查询,true:是;false:否
	 */
	public static void fetchUrl(String url, File urlsFile, int pageNo, ZtryPageInfo info, boolean isRevoke) {
		if (url == null || url.isEmpty()) {// 递归结束条件
			return;
		}

		boolean flag = fetchDetailUrls(url, info, isRevoke);

		if (flag) { // 递归结束条件
			List<String> urls = info.getUrls();

			if (urls != null && !urls.isEmpty()) {
				FileUtil.append(urlsFile, urls);
				LOG.debug("finish write urls, size:{}, url:{}", urls.size(), url);
				urls.clear();
			}
			urls = null;
		}

		if (info.getPageUrl() == null || info.getPageNo() < pageNo) { // 递归结束条件
			return;
		}

		fetchUrl(info.getPageUrl(), urlsFile, info.getPageNo(), info, isRevoke);
	}

	/**
	 * 根据 url 抓取在逃人员详细信息
	 * 
	 * @param url
	 * @return
	 */
	public static String fetchDetail(String url) {
		try {
			Document doc = Jsoup.connect(url).get();

			if (doc != null) {
				ZtryData data = map(doc, fetchZtryIdByUrl(url));

				if (data != null) {
					StringBuffer buffer = new StringBuffer(256);
					return data.map2Str(buffer);
				}
			}
		} catch (Exception e) {
			LOG.error("fetch detail error, url:{}, {}", url, e);
			Threads.sleep(3 * 60 * 1000);
		}
		return null;
	}

	public static ZtryData fetchDetail2ZtryData(String url) {
		try {
			Document doc = Jsoup.connect(url).get();

			if (doc != null) {
				return map(doc, fetchZtryIdByUrl(url));
			}
		} catch (Exception e) {
			LOG.error("fetch detail to ZtryData error, url:{}, {}", url, e);
			Threads.sleep(3 * 60 * 1000);
		}
		return null;
	}

	/**
	 * 根据 url 抓取撤销查询的人员详细信息
	 * 
	 * @param url
	 * @return
	 */
	public static String fetchRevokeDetail(String url) {
		try {
			Document doc = Jsoup.connect(url).get();

			if (doc != null) {
				ZtryRevokedData data = mapRevoker(doc, fetchZtryIdByUrl(url));
				if (data != null) {
					StringBuffer buffer = new StringBuffer(256);
					return data.map2Str(buffer);
				}
			}
		} catch (Exception e) {
			LOG.error("fetch revoke detail error, url:{}, {}", url, e);

			Threads.sleep(3 * 60 * 1000);
		}
		return null;
	}

	public static String fetchZtryIdByUrl(String url) {
		return url.substring(url.indexOf("id=") + 3);
	}

	public static ZtryData map(Document doc, String ztryId) {
		String ztryTId = trim2Null(doc.select(TID).html());
		String name = trim2Null(doc.select(NAME).html());
		String nickname = trim2Null(doc.select(NICKNAME).html());
		String sex = trim2Null(doc.select(SEX).html());
		String birth = trim2Null(doc.select(BIRTH).html());
		String certificate = trim2Null(doc.select(CERTIFICATE).html());
		String nation = trim2Null(doc.select(NATION).html());
		String otherCert1 = trim2Null(doc.select(OTHER_CERT1).html());
		String otherCert2 = trim2Null(doc.select(OTHER_CERT2).html());
		String height = trim2Null(doc.select(HEIGHT).html());
		String kouyin = trim2Null(doc.select(KOUYIN).html());
		String career = trim2Null(doc.select(CAREER).html());
		String censusAddr = trim2Null(doc.select(CENSUS_ADDR).html());
		String address = trim2Null(doc.select(ADDRESS).html());
		String census = trim2Null(doc.select(CENSUS).html());
		String features = trim2Null(doc.select(FEATURES).html());
		String special = trim2Null(doc.select(SPECIAL).html());
		String caseNo = trim2Null(doc.select(CASE_NO).html());
		String caseType = trim2Null(doc.select(CASE_TYPE).html());
		String caseRemark = trim2Null(doc.select(CASE_REMARK).html());
		String awayDate = trim2Null(doc.select(AWAY_DATE).html());
		String awayDirection = trim2Null(doc.select(AWAY_DIRECTION).html());
		String awayType = trim2Null(doc.select(AWAY_TYPE).html());
		String lawBook = lawBookUrl(ztryId);
		String lawDate = trim2Null(doc.select(LAW_DATE).html());
		String tjl = trim2Null(doc.select(TJL).html());
		String tjlType = trim2Null(doc.select(TJL_TYPE).html());
		String tjlMoney = trim2Null(doc.select(TJL_MONEY).html());
		String ownDept = trim2Null(doc.select(OWN_DEPT).html());
		String deptType = trim2Null(doc.select(DEPT_TYPE).html());
		String receiveDept = trim2Null(doc.select(RECEIVE_DEPT).html());
		String receiveDate = trim2Null(doc.select(RECEIVE_DATE).html());
		String receiver = trim2Null(doc.select(RECEIVER).html());
		String receiverPhone = trim2Null(doc.select(RECEIVER_PHONE).html());
		String register = trim2Null(doc.select(REGISTER).html());
		String registDate = trim2Null(doc.select(REGIST_DATE).html());
		String registAuthor = trim2Null(doc.select(REGIST_AUTHOR).html());
		String intoProvinceDate = trim2Null(doc.select(INTO_PROVINCE_DATE).html());
		String intoDeptDate = trim2Null(doc.select(INTO_DEPT_DATE).html());
		String lastModify = trim2Null(doc.select(LAST_MODIFY_TIME).html());
		String blReason = trim2Null(doc.select(BL_REASON).html());
		String photo = photoUrl(ztryId);

		ZtryData data = new ZtryData(ztryId, ztryTId, name, nickname, sex, birth, certificate, nation, otherCert1,
				otherCert2, height, kouyin, career, censusAddr, address, census, features, special, caseNo, caseType,
				caseRemark, awayDate, awayDirection, awayType, lawBook, lawDate, tjl, tjlType, tjlMoney, ownDept,
				deptType, receiveDept, receiveDate, receiver, receiverPhone, register, registDate, registAuthor,
				blReason, intoProvinceDate, intoDeptDate, lastModify, photo);
		return data;
	}

	public static ZtryRevokedData mapRevoker(Document doc, String ztryId) {
		String ztryTId = trim2Null(doc.select(TID).html());
		String name = trim2Null(doc.select(NAME).html());
		String nickname = trim2Null(doc.select(NICKNAME).html());
		String sex = trim2Null(doc.select(SEX).html());
		String birth = trim2Null(doc.select(BIRTH).html());
		String certificate = trim2Null(doc.select(CERTIFICATE).html());
		String nation = trim2Null(doc.select(NATION).html());
		String otherCert1 = trim2Null(doc.select(OTHER_CERT1).html());
		String otherCert2 = trim2Null(doc.select(OTHER_CERT2).html());
		String height = trim2Null(doc.select(HEIGHT).html());
		String kouyin = trim2Null(doc.select(KOUYIN).html());
		String career = trim2Null(doc.select(CAREER).html());
		String censusAddr = trim2Null(doc.select(CENSUS_ADDR).html());
		String address = trim2Null(doc.select(ADDRESS).html());
		String census = trim2Null(doc.select(CENSUS).html());
		String features = trim2Null(doc.select(FEATURES).html());
		String special = trim2Null(doc.select(SPECIAL).html());
		String caseNo = trim2Null(doc.select(CASE_NO).html());
		String caseType = trim2Null(doc.select(CASE_TYPE).html());
		String caseRemark = trim2Null(doc.select(CASE_REMARK).html());
		String awayDate = trim2Null(doc.select(AWAY_DATE).html());
		String awayDirection = trim2Null(doc.select(AWAY_DIRECTION).html());
		String awayType = trim2Null(doc.select(AWAY_TYPE).html());
		String lawBook = lawBookUrl(ztryId);
		String lawDate = trim2Null(doc.select(LAW_DATE).html());
		String tjl = trim2Null(doc.select(TJL).html());
		String tjlType = trim2Null(doc.select(TJL_TYPE).html());
		String tjlMoney = trim2Null(doc.select(TJL_MONEY).html());
		String ownDept = trim2Null(doc.select(OWN_DEPT).html());
		String deptType = trim2Null(doc.select(DEPT_TYPE).html());
		String receiveDept = trim2Null(doc.select(RECEIVE_DEPT).html());
		String receiveDate = trim2Null(doc.select(RECEIVE_DATE).html());
		String receiver = trim2Null(doc.select(RECEIVER).html());
		String receiverPhone = trim2Null(doc.select(RECEIVER_PHONE).html());
		String register = trim2Null(doc.select(REGISTER).html());
		String registDate = trim2Null(doc.select(REGIST_DATE).html());
		String registAuthor = trim2Null(doc.select(REGIST_AUTHOR).html());
		String intoProvinceDate = trim2Null(doc.select(INTO_PROVINCE_DATE).html());
		String intoDeptDate = trim2Null(doc.select(INTO_DEPT_DATE).html());
		String lastModify = trim2Null(doc.select(LAST_MODIFY_TIME).html());
		String blReason = trim2Null(doc.select(BL_REASON).html());
		String photo = photoUrl(ztryId);
		String caughtDate = trim2Null(doc.select(CAUGHT_DATE).html());
		String caughtWay = trim2Null(doc.select(CAUGHT_WAY).html());
		String caughtArea = trim2Null(doc.select(CAUGHT_AREA).html());
		String caughtOffice = trim2Null(doc.select(CAUGHT_OFFICE).html());
		String fingerprintNo = trim2Null(doc.select(FINGERPRINT_NO).html());
		String dnaNo = trim2Null(doc.select(DNA_NO).html());
		String revoker = trim2Null(doc.select(REVOKER).html());
		String revokeDate = trim2Null(doc.select(REVOKE_DATE).html());
		String revokeAuthor = trim2Null(doc.select(REVOKE_AUTHOR).html());
		String revokeProvinceDate = trim2Null(doc.select(REVOKE_PROVINCE_DATE).html());
		String revokeDeptDate = trim2Null(doc.select(REVOKE_DEPT_DATE).html());
		String revokeLastModifyDate = trim2Null(doc.select(REVOKE_LAST_MODIFY_DATE).html());

		ZtryRevokedData data = new ZtryRevokedData(ztryId, ztryTId, name, nickname, sex, birth, certificate, nation,
				otherCert1, otherCert2, height, kouyin, career, censusAddr, address, census, features, special, caseNo,
				caseType, caseRemark, awayDate, awayDirection, awayType, lawBook, lawDate, tjl, tjlType, tjlMoney,
				ownDept, deptType, receiveDept, receiveDate, receiver, receiverPhone, register, registDate,
				registAuthor, blReason, intoProvinceDate, intoDeptDate, lastModify, photo, caughtDate, caughtWay,
				caughtArea, caughtOffice, fingerprintNo, dnaNo, revoker, revokeDate, revokeAuthor, revokeProvinceDate,
				revokeDeptDate, revokeLastModifyDate);
		return data;
	}

	/**
	 * 根据分页的url 查询所有的详情url
	 * 
	 * @param url
	 *            分页url
	 * @param info
	 *            分页信息
	 * @param cookie
	 *            cookie信息
	 * @param isRevoke
	 * @return
	 */
	public static boolean fetchDetailUrls(String url, ZtryPageInfo info, boolean isRevoke) {
		if (url == null) {
			return false;
		}

		List<String> urls = new ArrayList<>(info.getPageSize());
		List<String> ids = new ArrayList<>(info.getPageSize());
		String pageUrl = null;

		Document doc = null;
		int pageNo = 0;

		try {
			doc = JsoupUtil.connect(url, info.getCookie());

			if (doc == null) {
				LOG.error("fetch detail urls error, url:{}", url);
				return false;
			}
			Elements es = doc.select("a[href]");
			List<String> pages = new ArrayList<>(4);

			if (es != null && !es.isEmpty()) {
				for (Element e : es) {
					String href = e.attr("href");
					if (!ValidateUtil.isBlank(href)) {
						String newUrl = null;

						if (!isRevoke) {
							newUrl = URL_HEAD + href;
						} else {
							newUrl = REVOKE_URL_HEAD + href;
						}
						if (!href.contains("endid")) {
							String id = href.substring(href.indexOf("id=") + 3);
							ids.add(id);
							urls.add(newUrl);
						} else {
							pages.add(newUrl);
						}
					}
				}

				if (pages.size() == 2) {
					String href1 = pages.get(0);
					String href2 = pages.get(1);
					int pageNo1 = fetchPageNo(href1);
					int pageNo2 = fetchPageNo(href2);

					if (pageNo1 >= pageNo2) {
						pageNo = pageNo1;
						pageUrl = href1;
					} else {
						pageNo = pageNo2;
						pageUrl = href2;
					}
				} else if (pages.size() == 1) {
					String pageUrl2 = pages.get(0);
					pageNo = fetchPageNo(pageUrl2);
					pageUrl = pageUrl2;
				} else {
					LOG.error("fetch detail urls error, url:{}", url);
				}
			}
		} catch (Exception e) {
			LOG.error("fetch detail urls error, url:{}, {}", url, e);
			return false;
		}

		info.setUrls(urls);
		info.setPageUrl(pageUrl);
		info.setIds(ids);
		info.setPageNo(pageNo);

		return true;
	}

	public static int fetchPageNo(String pageUrl) {
		int index = pageUrl.indexOf("&page=");
		String newStr = pageUrl.substring(index + 6);
		index = newStr.indexOf('&');
		return NumberUtil.parseInt(newStr.substring(0, index));
	}

	public static String lawBookUrl(String ztryId) {
		return LAW_BOOK_URL.replace("ztryId", ztryId);
	}

	public static String photoUrl(String ztryId) {
		return PHOTO_URL.replace("ztryId", ztryId);
	}

	public static String fetchDstFile(File fileName) {
		return FileUtil.fileNameAppendSuffix(fileName.getAbsolutePath(), "_detail");
	}

	public static Map<String, String> initCookie() {
		Map<String, String> cookie = new HashMap<>();

		cookie.put("JSESSIONID", "0000XMXH1qxXpm64kAfX8SKoj3q:15kbiosn3");
		cookie.put("X-Mapping-hknmkkgk", "A4151C48A17ED5EEE3F5CE1E0EFACEE3");

		return cookie;
	}

	private ZtryCatchUtil() {
	}
}
