package com.surfilter.mass.tools;

import java.io.File;
import java.io.IOException;

import com.surfilter.mass.tools.entity.ZtryPageInfo;
import com.surfilter.mass.tools.util.JsoupUtil;
import com.surfilter.mass.tools.util.NumberUtil;
import com.surfilter.mass.tools.util.ZtryCatchUtil;

import junit.framework.TestCase;

public class ZtryCatchTest extends TestCase {

	static final String URL_FETCH_PAGE_TEST2 = "http://ztry.xz.ga/ztrydj/DJOutputList.jsp?type=9&show_type=simple&endid=9023036222001110337&firstid=8428000000000059658&intCount=21761&flag=first&page=1&intperpagecount=99&from_sub=";
	static final String URL_FETCH_PAGE_TEST = "http://ztry.xz.ga/ztrydj/DJOutputList.jsp?type=9&show_type=simple&endid=9023036222001110337&firstid=8428000000000059658&intCount=21761&flag=behind&page=129&intperpagecount=99&from_sub=";
	static final String DETAIL_URL_TEST = "http://ztry.xz.ga/ztrydj/DJOutputDetail.jsp?type=9&id=1101020000000100982";
	static final String DETAIL_URL_TEST2 = "http://ztry.xz.ga/ztrycx/CXOutputDetail.jsp?type=9&id=1304020000000674741";

	static final String URL = "http://ztry.xz.ga/ztrydj/DJOutputList.jsp?type=9&tongyin=&bmzj_num=2&tstz_num=3&tbbj_num=2&ajlb_num=1&sqlwhere=+and+a.xb_dm%3D%272%27&from_sub=&lrr=&sy_dwdm=&show_type=simple&intperpagecount=99&TJJB=on&DBJB=00";
	static final String URL2 = "http://ztry.xz.ga/ztrydj/DJOutputList.jsp?type=9&show_type=simple&endid=9023036222001110337&firstid=8428000000000039575&intCount=21767&flag=behind&page=129&intperpagecount=99&from_sub=";
	static final String URL3 = "http://ztry.xz.ga/ztrydj/DJOutputList.jsp?type=9&show_type=simple&endid=1101050000000130408&firstid=1101020000000112225&intCount=21776&flag=behind&page=3&intperpagecount=18&from_sub=";

	public void testFetchDetailUrl() {
		ZtryPageInfo info = new ZtryPageInfo();
		info.setPageSize(99);
		info.setCookie(ZtryCatchUtil.COOKIE);
		System.out.println(ZtryCatchUtil.fetchDetailUrls(URL2, info, false));
		System.out.println(info.getUrls());
		System.out.println(info.getIds());
		System.out.println(info.getPageUrl());
	}

	public void testFetchDetailsByUrl() {
		System.out.println(ZtryCatchUtil.fetchDetail(DETAIL_URL_TEST));
	}

	public void testFetchRevokeDetailByUrl() {
		System.out.println(ZtryCatchUtil.fetchDetail(DETAIL_URL_TEST2));
	}

	public void testZTRYCookie() throws IOException {
		JsoupUtil.connect(URL, ZtryCatchUtil.COOKIE);
		JsoupUtil.connect(URL, ZtryCatchUtil.COOKIE);
	}

	public void testFetchPageNo() {
		System.out.println(ZtryCatchUtil.fetchPageNo("&page=9&intperpagecount=99&from_sub="));
		System.out.println(ZtryCatchUtil.fetchPageNo("&page=19&intperpagecount=99&from_sub="));
		System.out.println(ZtryCatchUtil.fetchPageNo("&page=19&&intperpagecount=99&from_sub="));
		System.out.println(ZtryCatchUtil.fetchPageNo(URL_FETCH_PAGE_TEST));
		System.out.println(ZtryCatchUtil.fetchPageNo(URL_FETCH_PAGE_TEST2));
	}

	public void testFetchZtryIdByUrl() {
		System.out.println(ZtryCatchUtil.fetchZtryIdByUrl(DETAIL_URL_TEST));
	}

	public void testIsPriYear() {
		for (int i = 2010; i <= 2017; i++) {
			System.out.println("year:" + i + ":" + NumberUtil.isPriYear(i));
		}
	}

	public void testFetchDstFile() {
		System.out.println(ZtryCatchUtil.fetchDstFile(new File("/apps/dst.txt/results/url.txt")));
	}

	/** 抓取女的url */
	public static final String GIRL_URL = "http://ztry.xz.ga/ztrydj/DJOutputList.jsp?type=9&tongyin=&bmzj_num=2&tstz_num=3&tbbj_num=2&ajlb_num=1&sqlwhere=+and+a.xb_dm%3D%272%27&from_sub=&lrr=&sy_dwdm=&show_type=simple&intperpagecount=99&TJJB=on&DBJB=00";
	/** 抓取男的URL */
	public static final String BOY_URL = "http://ztry.xz.ga/ztrydj/DJOutputList.jsp?type=9&tongyin=&bmzj_num=2&tstz_num=3&tbbj_num=2&ajlb_num=1&sqlwhere=+and+a.xb_dm%3D%271%27&from_sub=&lrr=&sy_dwdm=&show_type=simple&intperpagecount=99&TJJB=on&DBJB=00";

	public static final String URL_HEAD = "http://ztry.xz.ga/ztrydj/";
	public static final String REVOKE_URL_HEAD = "http://ztry.xz.ga/ztrycx/";

	/** 在逃人员按时间抓取 URL */
	public static final String DATE_URL = "http://ztry.xz.ga/ztrydj/DJOutputList.jsp?type=9&tongyin=&bmzj_num=2&tstz_num=3&tbbj_num=2&ajlb_num=1&sqlwhere=+and%28+a.rbksj_dj+between+to_date%28%27startDate%27%2C%27YYYY-MM-DD%27%29+and+to_date%28%27endDate235959%27%2C%27YYYY-MM-DDhh24miss%27%29%29%0D%0A&from_sub=&lrr=&sy_dwdm=&show_type=simple&intperpagecount=99&Submit=%C8%B7%26nbsp%3B%26nbsp%3B%26nbsp%3B%B6%A8&rybh=&sfzh=&xm=&zj_dm=&zjhm=&xb_dm=bxd&csrq_xx=&csrq_sx=&hjd_mc=&hjd_qh=&xzd_mc=&xzd_qh=&jg_mc=&jg_qh=&mz_dm=&ky_dm=&sg_1=&sg_2=&tstz_mc=&tstz_dm=&bw_dm=&fw_dm=&sl_dm=&bj_dm=&ajbh=&ajlb_mc=&ajlb_dm=&la_dwmc=&la_dwdm=&la_dwxt_dm=&lasj_xx=&lasj_sx=&dj_rq_xx=&dj_rq_sx=&TJJB=on&DBJB=00&ztlx_dm=&tjlbh=&tpfx_mc=&tpfx_qh=&tprq_xx=&tprq_sx=&rbksj_dj_xx=startDate&rbksj_dj_sx=endDate";

	public void testCatchBoys() {
		ZtryPageInfo info = new ZtryPageInfo();
		info.setPageSize(99);
		info.setCookie(ZtryCatchUtil.COOKIE);
		ZtryCatchUtil.fetchUrlAndDetail(BOY_URL, new File("D:/results/ztry/boy_urls.txt"),
				new File("D:/results/ztry/boy_details.txt"), 0, info, false);
	}

	public void testCatchGirls() {
		ZtryPageInfo info = new ZtryPageInfo();
		info.setPageSize(99);
		info.setCookie(ZtryCatchUtil.COOKIE);
		ZtryCatchUtil.fetchUrlAndDetail(GIRL_URL, new File("D:/results/ztry/girl_urls.txt"),
				new File("D:/results/ztry/girl_details.txt"), 0, info, false);
	}
}
