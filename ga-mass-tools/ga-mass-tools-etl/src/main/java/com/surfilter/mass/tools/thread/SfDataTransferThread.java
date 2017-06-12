package com.surfilter.mass.tools.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SfDataIndex;
import com.surfilter.mass.tools.conf.SfSysConsts;
import com.surfilter.mass.tools.conf.UserType;
import com.surfilter.mass.tools.entity.SfData;
import com.surfilter.mass.tools.util.DateUtil;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.Filter;
import com.surfilter.mass.tools.util.Id15To18;
import com.surfilter.mass.tools.util.Threads;

public class SfDataTransferThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(SfDataTransferThread.class);

	private static AtomicInteger allCounts = new AtomicInteger(0);
	private List<String> lines = new ArrayList<>(SfSysConsts.DEFAULT_BUFFER_SIZE);
	private List<SfData> datas = new ArrayList<SfData>(SfSysConsts.DEFAULT_BUFFER_SIZE);

	private volatile boolean running = true;
	private BlockingQueue<String> queue;

	private String dst; // json 存储目录
	private String companyId; // companyId
	private String areaCode; // 区域编码
	private String spliter; // 指定的分隔符，默认值为
	private int[] indexs; // 列索引
	private int len; // 解析后列长度

	private String idType; // 虚拟账户协议类型
	private String authType; // 认证类型
	private String certType; // 身份类型
	private String dateFormat; // dateFormat String
	private String sysType;// sysSource's sysType
	private String accountIsEmail; // 账号是否为邮箱,进行邮箱账号处理
	private String timeLocal; // time local, default is US
	private String nameFilter; // name filter flag

	public SfDataTransferThread(BlockingQueue<String> queue, MassConfiguration conf) {
		String[] indexArrays = conf.get(SfSysConsts.SF_INDEXS_PROPERTY).split(";");
		this.len = indexArrays.length;
		this.indexs = new int[this.len];
		for (int i = 0; i < len; i++) {
			this.indexs[i] = Integer.parseInt(indexArrays[i]);
		}

		validateAndSet(conf);

		this.queue = queue;

		try {
			new File(dst).mkdirs();
		} catch (Exception e) {
		}
	}

	private void validateAndSet(MassConfiguration conf) {
		this.dst = conf.get(SfSysConsts.SF_DST_PROPERTY);
		this.companyId = conf.get(SfSysConsts.SF_COMPANY_PROPERTY);
		this.areaCode = conf.get(SfSysConsts.SF_AREA_CODE_PROPERTY);
		this.spliter = conf.get(SfSysConsts.SF_SPLITER_PROPERTY, SfSysConsts.DEFAULT_SPLITER);
		this.idType = conf.get(SfSysConsts.SF_ID_TYPE_PROPERTY);
		this.authType = conf.get(SfSysConsts.SF_AUTH_TYPE_PROPERTY);
		this.certType = conf.get(SfSysConsts.SF_CERT_TYPE_PROPERTY);
		this.sysType = conf.get(SfSysConsts.SF_SYS_TYPE_PROPERTY);
		this.accountIsEmail = conf.get(SfSysConsts.SF_ACCOUNT_IS_EMAIL_PROPERTY);
		this.dateFormat = conf.get(SfSysConsts.SF_DATE_FORMAT_PROPERTY, SfSysConsts.DEFAULT_SPLITER);
		this.timeLocal = conf.get(SfSysConsts.SF_TIME_LOCAL_PROPERTY, "false");
		this.nameFilter = conf.get(SfSysConsts.SF_NAME_FILER_PROPERTY, "true");

		if (Filter.isBlank(this.dst)) {
			throw new RuntimeException(SfSysConsts.SF_DST_PROPERTY + " is illegal.");
		}

		if (Filter.isBlank(this.companyId)) {
			throw new RuntimeException(SfSysConsts.SF_COMPANY_PROPERTY + " is illegal.");
		}

		if (Filter.isBlank(this.areaCode)) {
			throw new RuntimeException(SfSysConsts.SF_AREA_CODE_PROPERTY + " is illegal.");
		}

		if (contains(SfDataIndex.ACCOUNT_INDEX) && (Filter.isBlank(idType) || idType.length() != 7)) {
			throw new RuntimeException(SfSysConsts.SF_ID_TYPE_PROPERTY + " is illegal.");
		}

		if (contains(SfDataIndex.AUTH_CODE_INDEX) && (Filter.isBlank(authType) || authType.length() != 7)) {
			throw new RuntimeException(SfSysConsts.SF_AUTH_TYPE_PROPERTY + " is illegal.");
		}

		if (contains(SfDataIndex.CERTIFICATE_CODE_INDEX) && (Filter.isBlank(certType) || certType.length() != 7)) {
			throw new RuntimeException(SfSysConsts.SF_CERT_TYPE_PROPERTY + " is illegal.");
		}

		if (Filter.isBlank(sysType) || sysType.length() != 3) {
			throw new RuntimeException(SfSysConsts.SF_SYS_TYPE_PROPERTY + " is illegal.");
		}
	}

	@Override
	public void run() {
		while (running) {
			if (!queue.isEmpty()) {
				queue.drainTo(lines, SfSysConsts.DEFAULT_BUFFER_SIZE);
				doParse();
				this.lines.clear();
			} else {
				Threads.sleep(200);
			}
		}

		LOG.info("finish parse data........................");
	}

	private void doParse() {
		if (lines.isEmpty()) {
			return;
		}

		for (String line : lines) {
			Iterable<String> it = Splitter.on(spliter).trimResults().split(line);
			String[] spliters = new String[len];
			int index = 0;

			for (String filed : it) {
				if (index >= len) {
					index++;
					break;
				}
				spliters[index++] = filed;
			}

			if (index != len) {// 解析长度不为字段映射长度
				LOG.error("parse line error:" + line);
				continue;
			}

			String mAC = "";
			String pHONE = "";
			String iMSI = "";
			String iMEI = "";

			String aUTH_TYPE = ""; // 认证类型
			String aUTH_CODE = "";

			String cERTIFICATE_TYPE = ""; // 身份类型
			String cERTIFICATE_CODE = "";

			String iD_TYPE = ""; // 虚拟账户类型
			String aCCOUNT = ""; // 虚拟账户

			int lAST_TIME = SfSysConsts.DEFAULT_LAST_TIME;
			String lAST_PLACE = "";

			for (int i = 0; i < len; i++) {
				spliters[i] = trim(spliters[i]);

				switch (indexs[i]) {
				case SfDataIndex.MAC_INDEX:// is MAC
					mAC = Filter.macFilter(spliters[i]);
					break;

				case SfDataIndex.MOBILE_INDEX:// is PHONE
					pHONE = Filter.mobileFilter(spliters[i]);
					break;

				case SfDataIndex.IMSI_INDEX:// is IMSI
					iMSI = Filter.imeiOrImsiFilter(spliters[i]);
					break;

				case SfDataIndex.IMEI_INDEX:// is IMEI
					iMEI = Filter.imeiOrImsiFilter(spliters[i]);
					break;

				case SfDataIndex.AUTH_CODE_INDEX:// is AUTH_CODE
					aUTH_CODE = spliters[i];

					if ("1021902".equals(this.authType) && "true".equals(this.nameFilter)) {
						aUTH_CODE = Filter.trimName(aUTH_CODE);
					}

					if (!Filter.isBlank(aUTH_CODE)) {
						aUTH_TYPE = this.authType;
					} else {
						aUTH_CODE = "";
					}

					break;

				case SfDataIndex.CERTIFICATE_CODE_INDEX:// is CERTIFICATE_CODE
					cERTIFICATE_CODE = spliters[i];

					if (UserType.CERTIFICATE.getValue().equals(this.certType)) { // 身份证
						cERTIFICATE_CODE = Id15To18.id15Or18Filter(cERTIFICATE_CODE);
					}

					if (!Filter.isBlank(cERTIFICATE_CODE)) {
						cERTIFICATE_TYPE = this.certType;
					} else {
						cERTIFICATE_CODE = "";
					}
					break;

				case SfDataIndex.ACCOUNT_INDEX:// is ACCOUNT
					aCCOUNT = spliters[i];
					if ("true".equals(accountIsEmail)) {
						iD_TYPE = Filter.emailProtocol(aCCOUNT);
						if (Filter.isBlank(iD_TYPE)) {
							aCCOUNT = "";
						}
					} else {
						if (!Filter.isBlank(aCCOUNT)) {
							iD_TYPE = this.idType;
						} else {
							aCCOUNT = "";
						}
					}
					break;

				case SfDataIndex.LAST_TIME_INDEX:// is LAST_TIME
					try {
						lAST_TIME = Integer.parseInt(spliters[i]);
					} catch (Exception e) {
						lAST_TIME = DateUtil.unixTime(spliters[i], this.dateFormat,
								"false".equals(timeLocal) ? Locale.US : Locale.CHINA, SfSysConsts.DEFAULT_LAST_TIME);
					}
					break;

				case SfDataIndex.LAST_PLACE_INDEX:// is LAST_PLACE
					lAST_PLACE = spliters[i];
					break;
				}
			}

			datas.add(new SfData(mAC, pHONE, iMSI, iMEI, aUTH_TYPE, aUTH_CODE, cERTIFICATE_TYPE, cERTIFICATE_CODE,
					iD_TYPE, aCCOUNT, lAST_TIME, lAST_PLACE));
		}

		String path = dst + "/" + DateUtil.dateToStr(new Date(), "yyyyMMddHHmmss") + FileUtil.random() + "_" + sysType
				+ "_" + this.areaCode + "_" + this.companyId + "_015.log";
		FileUtil.writeWithJson(path, datas, allCounts);
	}

	public void stop() {
		this.running = false;
	}

	private boolean contains(int value) {
		for (int i : indexs) {
			if (i == value) {
				return true;
			}
		}
		return false;
	}

	private String trim(String value) {
		return Filter.trimNULL(value).replaceAll("[,|;|\\=|\\?|\\*|\\+|\\(|\\)|（|）|\\'|\\t]*", "").trim();
	}
}
