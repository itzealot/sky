package com.surfilter.mass.tools.entity;

/**
 * 分页信息
 * 
 * @author zealot
 *
 */
public class PageInfo {

	private int pageTotal;
	private int counts;
	private int currentPage;
	private int pageSize;

	public PageInfo(int pageTotal, int counts, int currentPage, int pageSize) {
		super();
		this.pageTotal = pageTotal;
		this.counts = counts;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	public int getCounts() {
		return counts;
	}

	public void setCounts(int counts) {
		this.counts = counts;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "PageInfo [pageTotal=" + pageTotal + ", counts=" + counts + ", currentPage=" + currentPage
				+ ", pageSize=" + pageSize + "]";
	}

}
