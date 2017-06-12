package com.surfilter.mass.tools.entity;

import java.util.List;
import java.util.Map;

/**
 * 在逃人员查询
 * 
 * @author zealot
 *
 */
public class ZtryPageInfo {

	private List<String> urls;
	private List<String> ids;
	private String pageUrl;
	private int pageNo;
	private int pageSize;
	private Map<String, String> cookie;

	public ZtryPageInfo() {
	}

	public ZtryPageInfo(List<String> urls, String pageUrl, List<String> ids, int pageNo, int pageSize,
			Map<String, String> cookie) {
		super();
		this.urls = urls;
		this.pageUrl = pageUrl;
		this.ids = ids;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.cookie = cookie;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Map<String, String> getCookie() {
		return cookie;
	}

	public void setCookie(Map<String, String> cookie) {
		this.cookie = cookie;
	}

	@Override
	public String toString() {
		return "ZTRYPageInfo [urls=" + urls + ", ids=" + ids + ", pageUrl=" + pageUrl + ", pageNo=" + pageNo
				+ ", pageSize=" + pageSize + "]";
	}

}
