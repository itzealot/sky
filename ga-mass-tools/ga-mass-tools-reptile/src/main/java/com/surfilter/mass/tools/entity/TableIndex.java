package com.surfilter.mass.tools.entity;

import java.util.Arrays;
import java.util.List;

public final class TableIndex {

	/** 卡口信息字段描述 */
	public static class TABLE_T_ITGS_TGSINFO {
		public static final int ID = 0; // 卡口ID
		public static final int NAME = 1; // 卡口名称
		public static final int VPN_NAME = 2; // VPN 名称
		public static final int IMAGE = 3; // 图像路径
		public static final int REMARK = 4; // 备注信息(详细安装信息)
		public static final int PARENT_ID = 7; // 父卡口ID
		public static final int OWN_AREA = 8; // 所属区
		public static final int LONGTITUDE = 11; // 经度
		public static final int LATITUDE = 12; // 纬度
		public static final int ADDRESS = 15; // 地址
		public static final int OWN_OFFICE = 16; // 所属分局
		public static final int TIME = 43; // 时间

		public static final List<Integer> INDEXS = Arrays.asList(ID, NAME, VPN_NAME, IMAGE, REMARK, PARENT_ID, OWN_AREA,
				LONGTITUDE, LATITUDE, ADDRESS, OWN_OFFICE, TIME);

		private TABLE_T_ITGS_TGSINFO() {
		}
	}

	private TableIndex() {
	}
}
