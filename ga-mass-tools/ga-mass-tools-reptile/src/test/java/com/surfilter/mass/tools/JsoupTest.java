package com.surfilter.mass.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.surfilter.mass.tools.util.Dates;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.JsoupUtil;

import junit.framework.TestCase;

public class JsoupTest extends TestCase {

	String url = "https://data.surfilter.com:12180/gacenter/login";
	String usernameKey = "username";
	String usernameValue = "zengtao";
	String passwordKey = "password";
	String passwordValue = "enQxMjM0NTY=";// zt123456
	String sessionKey = "JSESSIONID";

	public void testLogin() {
		Response res = JsoupUtil.login("http://172.31.6.66:2000/j_security_check", "j_username", usernameValue,
				"j_password", passwordValue);
		try {
			if (res == null) {
				System.out.println("login error.");
				return;
			}

			String cookie = JsoupUtil.cookie(res, sessionKey);
			System.out.println(cookie);
			Document d = JsoupUtil.connect("http://172.31.6.66:2000/pages/task/list/myTask.jsf", sessionKey, cookie,
					true);

			if (d != null) {
				System.out.println(d.html());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testDate() {
		Calendar c = Calendar.getInstance();
		System.out.println(c.get(Calendar.HOUR_OF_DAY));
	}

	public void testStr2DateWithPlusHour() {
		System.out.println(Dates.str2DateWithPlusHour("2016-05-06"));
		System.out.println(Dates.str2DateWithPlusHour("2016-05-06+16"));
	}

	public void testURL() throws MalformedURLException {
		URL url = new URL("https", "data.surfilter.com", 12180, "/gacenter/login");

		JsoupUtil.authWithTLS();
		try {
			Response res = Jsoup.connect(url.toString())
					.data("username", "zengtao", "password", "zt921015", "captcha", "").method(Method.POST).execute();
			// System.out.println(res.body());

			Document doc = JsoupUtil.connect("https://data.surfilter.com:12180/gacenter/servicedata/index?page=11",
					sessionKey, JsoupUtil.cookie(res, sessionKey), true);

			if (doc != null) {
				System.out.println(doc.html());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> catchUrlBy(String tableName, int pageNo, int pageSize) {
		String url = JsoupUtil.pageUrl(tableName, pageNo, pageSize);
		System.out.println(url);
		List<String> infos = new ArrayList<>(pageSize);

		try {
			Document doc = Jsoup.connect(url).get();

			if (doc != null) {
				Elements rows = doc.getElementsByTag("row");
				if (rows != null && !rows.isEmpty())
					for (Element row : rows) {
						String result = row.html();
						String id = null;
						if (result.length() >= 10) {
							id = result.substring(0, 10);
						}
						int index = result.indexOf("null");
						String name = null;
						String newString = null;
						if (index > 10) {
							name = result.substring(10, index);
							newString = result.substring(index + 4);
						}

						String parentId = null;
						if (newString != null) {
							index = newString.indexOf("null");
							parentId = newString.substring(index + 4, index + 14);
						}
						if (id != null && name != null && parentId != null)
							infos.add(JsoupUtil.join(id, "\t", name, "\t", parentId));
					}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return infos;
	}

	public void testCatchUrlBy() {
		List<String> infos = catchUrlBy(JsoupUtil.T_TGS_SET_WATCH, 4, 100);
		FileUtil.append(new File("D:/results/卡口信息.txt"), infos);
	}

	public void testReadKaKouInfo() {
		FileUtil.readKaKouInfo("C:\\Users\\Administrator\\Desktop\\fxj_licheng\\kakou_info.txt");
	}

	public void testCatchByTableName_T_ITGS_TGSINFO() {
		List<String> infos = JsoupUtil.catchByTableName_T_ITGS_TGSINFO(1, 100);
		FileUtil.append(new File("D:/results/卡口地址.txt"), infos);
	}

	/**
	 * 爬取所有的卡口信息，并写入文件
	 */
	public void testCatchAllKaKouInfos() {
		JsoupUtil.catchKaKouInfoAndWrite(new File("D:/results/卡口地址.txt"));
	}

	public void testParse() {
		Document d = Jsoup.parse("<row><col>c1</col><col>c2</col><col>c3</col></row>");
		Elements es = d.getElementsByTag("row");

		for (Element e : es) {
			List<Node> nodes = e.childNodes();
			for (Node n : nodes) {
				System.out.println(JsoupUtil.trim(n));
			}
		}
	}

	public void testCatchVehicleData() {
		String url = "http://10.231.10.10:8888/EHL_ITGS/login.action";
		Response res = JsoupUtil.login(url, "pname", "007209", "pwd", "tsrj85086111");
		if (res == null) {
			System.out.println("login error.");
			return;
		}

		String cookie = JsoupUtil.cookie(res, sessionKey);
		System.out.println(cookie);

		// all
		String url0 = "http://10.231.10.10:8888/EHL_ITGS/itgs/jsp/jcfk/txclcx.queryPassCar.action?param=";
		String url1 = url0
				+ "%2C%2C%2C3701979105%3B3701126033%3B3701126035%3B3701126036%3B3701126037%3B3701126038%3B3701126039%3B3701126041%3B3701126043%3B3701126045%3B3701126046%3B3701126048%3B3701126003%3B3701126007%3B3701126008%3B3701126010%3B3701126011%3B3701126013%3B3701126014%3B3701126015%3B3701126016%3B3701126017%3B3701126018%3B3701126020%3B3701126022%3B3701126023%3B3701126024%3B3701126025%3B3701126027%3B3701126030%3B3701126050%3B3701126001%3B3701126002%3B3701126026%3B3701126029%3B3701126034%3B3701126040%3B3701126042%3B3701126044%3B3701126047%3B3701126051%3B3701126109%3B3701126028%3B3701126645%3B3701126646%3B3701126647%3B3701126012%3B3701126019%3B3701126006%3B3701126101%3B3701126102%3B3701126103%3B3701126104%3B3701126105%3B3701126106%3B3701126107%3B3701126108%3B3701126032%3B3701126049%3B3701126115%3B3701126116%3B3701126117%3B3701126118%3B3701126119%3B3701126120%3B3701126121%3B3701126122%3B3701126123%3B3701126124%3B3701126125%3B3701126126%3B3701126128%3B3701126129%3B3701126062%3B3701126064%3B3701126127%3B3701126072%3B3701126074%3B3701126075%3B3701126068%3B3701126060%3B3701126067%3B3701126069%3B3701126071%3B3701126076%3B3701126080%3B3701126061%3B3701126134%3B3701126137%3B3701126636%3B3701126637%3B3701126065%3B3701126070%3B3701126073%3B3701126609%3B3701126608%3B3701126133%3B3701126136%3B3701126610%3B3701126611%3B3701126612%3B3701126613%3B3701126139%3B3701126620%3B3701126077%3B3701126704%3B3701126205%3B3701126206%3B3701126135%3B3701126063%3B3701126066%3B3701126079%3B3701126078%3B3701126614%3B3701126615%3B3701126623%3B3701126619%3B3701126618%3B3701126625%3B3701126617%3B3701126616%3B3701126639%3B3701126638%3B3701126508%3B3701126635%3B3701126634%3B3701126633%3B3701126632%3B3701126626%3B3701126627%3B3701126628%3B3701126640%3B3701126641%3B3701126642%3B3701126643%3B3701126644%3B3701126629%3B3701126630%3B3701126631%3B3701126083%3B3701126084%3B3701126086%3B3701055041%3B3701126087%3B3701126088%3B3701055040%3B3701126518%3B3701126516%3B3701126095%3B3701126501%3B3701126502%3B3701126503%3B3701126504%3B3701126506%3B3701126507%3B3701126510%3B3701126512%3B3701126085%3B3701126082%3B3701126517%3B3701126702%3B3701126701%2C2017-03-01+08%3A16%2C2017-03-01+09%3A16%2C%3B%3B%3B%3B%2C%3B%3B%2CcarSpeed%3D%40%261%26200%26-1";

		// String url0 =
		// "http://10.231.10.10:8888/EHL_ITGS/itgs/jsp/jcfk/txclcx.queryPassCar.action?param="
		// String url1 = url0 +
		// "%2C%2C%2C3701979105%2C2017-03-01%2B08%3A18%2C2017-03-01%2B09%3A18%2C%3B%3B%3B%3B%2C%3B%3B%2CcarSpeed%3D%40%261%2615%26-1";

		// String url1 = url0 +
		// "%2C%2C%2C2017-03-01%2B08%3A16%2C2017-03-01%2B09%3A16%2C%3B%3B%3B%3B%2C%3B%3B%2CcarSpeed%3D%40%261%2615%26-1";

		Document d = JsoupUtil.connect(url1, sessionKey, cookie, true);

		if (d != null) {
			System.out.println(d.html());
		}
	}

	public void testParseHtml() {
		String val = "<row id='70'><col>3701033040</col><col>舜玉路玉函路东100米</col><col>中科-VPN</col><col>null</col><col>无</col><col>null</col><col>null</col><col>370103000000</col><col>市中区</col><col>1</col><col>2</col><col>117.00490513005893</col><col>36.62623612510229</col><col>3</col><col>无</col><col>舜玉路玉函路东100米</col><col>市中区分局</col><col>null</col><col>卡口</col><col>1</col><col>无</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>vpn</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>1</col><col>2016-11-11 14:06:02</col><col>0</col><col>null</col><col>0</col><col>null</col><col>0</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>null</col><col>71</col></row>";
		val.toCharArray();
	}

	String HEAD = "http://ztry.xz.ga/ztrydj/";

	public void testCatch() {
		try {
			// catchUrl(HEAD
			// +
			// "DJOutputList.jsp?type=9&show_type=default&endid=2306890000000245666&firstid=1101050002001080236&intCount=49&flag=behind&page=2&intperpagecount=18&from_sub=");
			catchUrl(
					"http://ztry.xz.ga/ztrydj/DJOutputList.jsp?type=9&tongyin=&bmzj_num=2&tstz_num=3&tbbj_num=2&ajlb_num=1&sqlwhere=+and+a.xb_dm='2'&from_sub=&lrr=&sy_dwdm=&show_type=simple&intperpagecount=99&TJJB=on&DBJB=00");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void catchUrl(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		if (doc != null) {
			System.out.println(doc.html());
		}
	}
}
