package com.surfilter.mass.tools.util;

import java.io.File;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.LoginMsg;
import com.surfilter.mass.tools.entity.PageInfo;
import com.surfilter.mass.tools.entity.TableIndex.TABLE_T_ITGS_TGSINFO;

/**
 * jsoup util
 * 
 * @author zealot
 *
 */
public final class JsoupUtil {

	private static final Logger LOG = LoggerFactory.getLogger(JsoupUtil.class);

	public static final String SPLITER = "\t";

	public static final String CATCH_URL = "http://10.231.10.10:8888/EHL_ITGS/itgs/jsp/index/itgscommon.getPageUtil.action?pageNum=0&currPage=%s&exeSql=select+*++from+%s+&pageSize=%s&totalNum=0";

	/** 卡口信息表 */
	public static final String T_TGS_SET_WATCH = "T_TGS_SET_WATCH";

	/** 卡口地址 */
	public static final String T_ITGS_TGSINFO = "t_itgs_tgsinfo";

	public static final int DEFAULT_PAGE_SIZE = 100;

	public static String pageUrl(String tableName, int pageNo, int pageSize) {
		return format(CATCH_URL, pageNo, tableName, pageSize);
	}

	public static String cookie(Response res, String key) {
		return res == null ? null : res.cookie(key);
	}

	public static String trim(String val) {
		return val == null ? null : val.replace("\n", "");
	}

	public static String trim(Node n) {
		return n == null ? null : n.toString().replace("\n", "");
	}

	/**
	 * catch T_ITGS_TGSINFO(卡口地址)
	 * 
	 * @param pageNo
	 *            当前页
	 * @param pageSize
	 *            页大小
	 * @return
	 */
	public static List<String> catchByTableName_T_ITGS_TGSINFO(int pageNo, int pageSize) {
		Elements rows = rows(T_ITGS_TGSINFO, pageNo, pageSize);
		if (rows == null) {
			return new ArrayList<>(2);
		}
		List<String> results = new ArrayList<>(pageSize);

		for (Element row : rows) {
			List<Node> nodes = row.childNodes();
			if (nodes != null) {
				String result = parse_T_ITGS_TGSINFO(nodes);
				if (result != null)
					results.add(result);
			}
		}

		return results;
	}

	/**
	 * 抓取卡口信息并写入文件
	 * 
	 * @param file
	 */
	public static void catchKaKouInfoAndWrite(File file) {
		int size = DEFAULT_PAGE_SIZE;
		PageInfo page = pageInfo(T_ITGS_TGSINFO, 1, size);
		if (page == null) {
			return;
		}

		// 按照页抓取数据
		for (int i = 1; i <= page.getPageTotal(); i++) {
			List<String> results = catchByTableName_T_ITGS_TGSINFO(i, size);
			FileUtil.append(file, results);
			LOG.info("write into file :{}, size:{}", file.getName(), results.size());
			results.clear();
			results = null;
		}
	}

	/**
	 * 根据表名称获取分页信息
	 * 
	 * @param tableName
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static PageInfo pageInfo(String tableName, int pageNo, int pageSize) {
		String url = JsoupUtil.pageUrl(tableName, pageNo, pageSize);

		try {
			Document doc = Jsoup.connect(url).get();
			if (doc != null) {
				Elements userdatas = doc.getElementsByTag("userdata");
				if (userdatas == null || userdatas.size() != 3) {
					return null;
				}

				String sql = userdatas.get(0).html();
				String[] pages = userdatas.get(1).html().split(";");
				int currentPage = NumberUtil.parseInt(userdatas.get(2).html());
				int counts = NumberUtil.parseInt(pages[1]);
				int pageTotal = NumberUtil.parseInt(pages[0]);
				LOG.info("query page info, sql:{}, pageTotal:{}, counts:{}", sql, pageTotal, counts);
				return new PageInfo(pageTotal, counts, currentPage, pageSize);
			}
		} catch (Exception e) {
			LOG.error("error parse document by tag row", e);
		}
		return null;
	}

	public static String parse_T_ITGS_TGSINFO(List<Node> nodes) {
		return parse(nodes, TABLE_T_ITGS_TGSINFO.INDEXS);
	}

	public static String parse(List<Node> nodes, List<Integer> indexs) {
		if (nodes == null || nodes.isEmpty()) {
			return null;
		}

		StringBuffer buffer = new StringBuffer(512);
		int i = 0, len = indexs.size() - 1;
		for (; i < len; i++) {
			buffer.append(trim(nodes.get(indexs.get(i))));
			buffer.append(SPLITER);
		}
		return buffer.append(trim(nodes.get(indexs.get(i)))).toString();
	}

	public static Elements rows(String tableName, int pageNo, int pageSize) {
		String url = JsoupUtil.pageUrl(tableName, pageNo, pageSize);

		try {
			Document doc = Jsoup.connect(url).get();
			if (doc != null) {
				Elements rows = doc.getElementsByTag("row");
				return rows == null ? null : rows;
			}
		} catch (Exception e) {
			LOG.error("error parse document by tag row", e);
		}
		return null;
	}

	public static Map<String, String> cookies(Response res) {
		return res == null ? null : res.cookies();
	}

	public static Document connect(String url, String sessionKey, String sessionValue, boolean print) {
		try {
			Connection connect = Jsoup.connect(url);
			return connect.cookie(sessionKey, sessionValue).timeout(60000).ignoreContentType(true).get();
		} catch (Exception e) {
			if (print) {
				LOG.error(format("connect url fail, sessionKey:{}, sessionValue:{}, url:%s", sessionKey, sessionValue,
						url), e);
			}
			return null;
		}
	}

	public static Document connect(String url, String sessionKey, String sessionValue) {
		return connect(url, sessionKey, sessionValue, true);
	}

	public static Document connect(String url, Map<String, String> cookies) {
		try {
			return Jsoup.connect(url).cookies(cookies).timeout(10000).get();
		} catch (Exception e) {
			LOG.error(format("connect with cookie error, url:%s", url), e);
			return null;
		}
	}

	public static boolean loginSuccess(Document doc, String elementId, String tag) {
		return LoginMsg.LOGIN_SUCCESS.equals(loginStatus(doc, elementId, tag));
	}

	public static LoginMsg loginStatus(Document doc, String elementId, String tag) {
		if (doc == null) {
			return LoginMsg.LOGIN_FAILED;
		}

		Element e = doc.getElementById(elementId);

		if (e != null) {
			Elements es = e.getElementsByTag(tag);
			if (es != null && !es.isEmpty()) {
				String html = es.html().replaceAll("[\n]", "");
				if (html.isEmpty()) {
					return LoginMsg.LOGIN_SUCCESS;
				}

				return new LoginMsg(html);
			}

			return LoginMsg.LOGIN_SUCCESS;
		}

		return LoginMsg.LOGIN_SUCCESS;
	}

	public static Response login(String url, String usernameKey, String usernameValue, String passwordKey,
			String passwordValue, Method method) {
		try {
			return Jsoup.connect(url).data(usernameKey, usernameValue, passwordKey, passwordValue).method(method)
					.execute();
		} catch (Exception e) {
			LOG.error(format("login error, url:%s, usernameKey:%s, usernameValue:%s, passwordKey:%s, passwordValue:%s",
					url, usernameKey, usernameValue, passwordKey, passwordValue), e);
			return null;
		}
	}

	public static Response login(String url, Method method, boolean print, String... keyvals) {
		try {
			return Jsoup.connect(url).data(keyvals).method(method).execute();
		} catch (Exception e) {
			if (print) {
				LOG.error(format("login error, url:%s, keyvals:%s", url, Arrays.asList(keyvals)), e);
			}
			return null;
		}
	}

	public static Response loginWithArray(String url, Method method, boolean print, String[] keyvals) {
		try {
			return Jsoup.connect(url).data(keyvals).method(method).execute();
		} catch (Exception e) {
			if (print) {
				LOG.error(format("login error, url:%s, keyvals:%s", url, Arrays.asList(keyvals)), e);
			}
			return null;
		}
	}

	public static Response login(String url, boolean print, String... keyvals) {
		return login(url, Method.POST, print, keyvals);
	}

	public static Response login(String url, String usernameKey, String usernameValue, String passwordKey,
			String passwordValue) {
		return login(url, usernameKey, usernameValue, passwordKey, passwordValue, Method.POST);
	}

	public static void auth(String protocol) {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			SSLContext context = SSLContext.getInstance(protocol);
			context.init(null, new X509TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		} catch (Exception e) {
			LOG.error(format("auth error, protocol:%s", protocol), e);
		}
	}

	public static void authWithTLS() {
		auth("TLS");
	}

	public static Map<String, String> httpHeaders(Response res) {
		return res == null ? null : res.headers();
	}

	public static Response connect(URL url, String protocol, int timeout) {
		try {
			auth(protocol);
			Connection conn = HttpConnection.connect(url);
			conn.timeout(timeout);
			conn.header("Accept-Encoding", "gzip,deflate,sdch");
			conn.header("Connection", "close");
			return conn.response();
		} catch (Exception e) {
			LOG.error("get http headers error, url:{}, {}", url, e);
			return null;
		}
	}

	public static Response connectWithTLS(URL url, int timeout) {
		return connect(url, "TLS", timeout);
	}

	public static String fillWith0(int val) {
		return val < 10 ? "0" + val : "" + val;
	}

	public static String join(String... strings) {
		StringBuffer buffer = new StringBuffer(128);

		for (int i = 0, len = strings.length; i < len; i++) {
			buffer.append(strings[i]);
		}

		return buffer.toString();
	}

	private JsoupUtil() {
	}
}
