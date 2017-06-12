package com.surfilter.mass.tools.conf;

/**
 * Sf Consts
 * 
 * @author zealot
 *
 */
public final class SfSysConsts {

	public static final int SF_DEFAULT_PROTOCOL_TYPE_LENGTH = 7;

	public static final int DEFAULT_BUFFER_SIZE = 5000;
	public static final int DEFAULT_LAST_TIME = 946684800;
	public static final String DEFAULT_SPLITER = "\t";
	public static final String SF_DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// 身份关系原始文件存储目录
	public static final String SF_SRC_PROPERTY = "transfer.sf.src";

	// 身份关系日志存储目录
	public static final String SF_DST_PROPERTY = "transfer.sf.dst";

	// 身份关系厂商编码
	public static final String SF_COMPANY_PROPERTY = "transfer.sf.company.id";

	// 生成文件所需的区域编码
	public static final String SF_AREA_CODE_PROPERTY = "transfer.sf.area.code";

	// sysSource对应的编号
	public static final String SF_SYS_TYPE_PROPERTY = "transfer.sf.sys.type";

	// 线程池大小
	public static final String SF_POOL_SIZE_PROPERTY = "transfer.sf.pool.size";

	// 休息时间
	public static final String SF_SLEEP_PROPERTY = "transfer.sf.sleep";

	// 主线程休息
	public static final String SF_SLEEP_COUNTS_PROPERTY = "transfer.sf.sleep.counts";

	// 解析文件的分隔符
	public static final String SF_SPLITER_PROPERTY = "transfer.sf.spliter";

	// 虚拟账户类型(id type)
	public static final String SF_ID_TYPE_PROPERTY = "transfer.sf.id.type";

	// 认证类型(auth type)
	public static final String SF_AUTH_TYPE_PROPERTY = "transfer.sf.auth.type";

	// 身份类型(cert type)
	public static final String SF_CERT_TYPE_PROPERTY = "transfer.sf.cert.type";

	// LAST_TIME FORMAT PROPERTY
	public static final String SF_DATE_FORMAT_PROPERTY = "transfer.sf.format";

	public static final String SF_ACCOUNT_IS_EMAIL_PROPERTY = "transfer.sf.account.is.email";

	public static final String SF_TIME_LOCAL_PROPERTY = "transfer.sf.time.local";

	public static final String SF_NAME_FILER_PROPERTY = "transfer.sf.name.filter";

	/**
	 * data indexs: 0,1,2,3,4,5,6,7,8
	 * MAC,PHONE,IMSI,IMEI,AUTH_CODE,CERTIFICATE_CODE,ACCOUNT,LAST_TIME,
	 * LAST_PLACE
	 * 
	 * 源文件列索引列表:如共三列，第一列为 PHONE，第二列为 CERTIFICATE_CODE， 第三列为 ACCOUNT；则该值为：1,5,6
	 */
	public static final String SF_INDEXS_PROPERTY = "transfer.sf.indexs";

	private SfSysConsts() {
	}
}
