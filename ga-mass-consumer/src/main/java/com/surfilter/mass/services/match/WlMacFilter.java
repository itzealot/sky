//package com.surfilter.mass.services.match;
//
//import java.util.Map;
//import java.util.Set;
//
///**
// * WL 数据过滤
// * 
// * @author zealot
// *
// */
//public class WlMacFilter {
//
//	private static volatile WlMacFilter instance = null;
//
//	private WlMacFilter() {
//	}
//
//	public static WlMacFilter getInsance() {
//		if (instance == null) {
//			synchronized (WlMacFilter.class) {
//				if (instance == null)
//					instance = new WlMacFilter();
//			}
//		}
//		return instance;
//	}
//
//	public void setMacCompanyKeys(Set<String> macCompanyKeys) {
//		this.macCompanyKeys = macCompanyKeys;
//	}
//
//	public void setMacFilterMap(Map<String, String> macFilterMap) {
//		this.macFilterMap = macFilterMap;
//	}
//
//}
