package com.surfilter.mass.tools;

import java.io.File;
import java.net.URL;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.surfilter.mass.tools.util.PeopleHotelInfoCatchUtil;

import junit.framework.TestCase;

public class TestPeopleHotelCatch extends TestCase {

	/** hotel page info url */
	public static final String HOTEL_PAGE_ONE = "http://10.49.6.171/hotel/SameRoom.asp?sql=select%20/*+%20FIRST_ROWS%20*/%20*%20from%20(select%20A.*,%20ROWNUM%20RN%20from%20(select%20*%20from%20v_ch_all_web)%20A%20where%20ROWNUM%20%3C=%2020)%20where%20RN%20%3E=%2011";

	public void testConnect() {
		try {
			Document doc = Jsoup.parse(new URL(HOTEL_PAGE_ONE).openStream(), "GBK", HOTEL_PAGE_ONE);
			System.out.println(doc.html());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testParse() {
		try {
			Response res = Jsoup.connect(HOTEL_PAGE_ONE).execute();
			String body = res.body();
			System.out.println(new String(body.getBytes("GBK"), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testFetchPagePeopleHotelInfos() {
		PeopleHotelInfoCatchUtil.fetchPagePeopleHotelInfos(HOTEL_PAGE_ONE, 10);
	}

	public void testFetchPeopleHotelInfos() {
		PeopleHotelInfoCatchUtil.fetchPeopleHotelInfos(1, 100, 2, new File("D:/results/test/test.txt"));
	}

	public void testFetchPeopleHotelInfosWithTime() {
		PeopleHotelInfoCatchUtil.fetchPeopleHotelInfos(1, 10, 10, "201701010000", "201701020000",
				new File("D:/results/test/test.txt"));
	}
}
