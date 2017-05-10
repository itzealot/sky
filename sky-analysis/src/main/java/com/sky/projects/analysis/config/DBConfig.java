package com.sky.projects.analysis.config;

public abstract interface DBConfig {

	public static final String TABLE_HEAT_CHAT = "devicenumhistoryinfo";
	public static final String TABLE_EQUIPMENT_INFO = "equipmentinfo";
	public static final String TABLE_FOCUS_USER = "tbl_focus_person";
	public static final String TABLE_USER_TRACK = "tbl_person_location";
	public static final String TABLE_USER_DISTRIBUTE = "tbl_person_distribute";
	public static final String TABLE_GROUP_ACTION = "tbl_group_action";
	public static final String TABLE_GROUP_MODEL = "tbl_group_model";
	public static final String TABLE_GROUP_PERSON = "tbl_group_person";
	public static final String TABLE_SERVICE_CODES = "serviceinfo";
	public static final String TABLE_REGION_INFO = "carecontextinfo";
	public static final String TABLE_PREWARN_MESSAGE = "tbl_area_warn";
	public static final String TABLE_PASS_SERVICE = "tbl_person_pass_service";

	public static final int EXECUTE_BATCH_NUMBER = 3000;
}
