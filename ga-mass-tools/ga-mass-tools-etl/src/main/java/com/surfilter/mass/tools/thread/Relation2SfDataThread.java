package com.surfilter.mass.tools.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SfDataIndex;
import com.surfilter.mass.tools.conf.SfSysConsts;
import com.surfilter.mass.tools.entity.SfData;
import com.surfilter.mass.tools.util.DateUtil;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.Filter;
import com.surfilter.mass.tools.util.Threads;

public class Relation2SfDataThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(Relation2SfDataThread.class);

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
	String dateFormat; // dateFormat String
	private String sysType;// sysSource's sysType
	String accountIsEmail; // 账号是否为邮箱,进行邮箱账号处理
	String timeLocal; // time local, default is US
	String nameFilter; // name filter flag

	public Relation2SfDataThread(BlockingQueue<String> queue, MassConfiguration conf) {
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
			String[] spliters = new String[12];
			int index = 0;

			for (String filed : it) {
				spliters[index++] = filed;
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

			try {
				lAST_TIME = Integer.parseInt(spliters[4]);
				lAST_PLACE = spliters[5];

				switch (spliters[1]) {
				case "1020002":
					mAC = spliters[0];
					break;
				case "1021901":
					iMEI = spliters[2];
					break;
				case "1020003":
					iMSI = spliters[2];
					break;
				case "1020004":
					pHONE = spliters[2];
					break;
				case "1021111":
					cERTIFICATE_CODE = spliters[2];
					cERTIFICATE_TYPE = "1021111";
					break;
				default:
					iD_TYPE = spliters[3];
					aCCOUNT = spliters[2];
					break;
				}
				switch (spliters[3]) {
				case "1020002":
					mAC = spliters[0];
					break;
				case "1021901":
					iMEI = spliters[2];
					break;
				case "1020003":
					iMSI = spliters[2];
					break;
				case "1020004":
					pHONE = spliters[2];
					break;
				case "1021111":
					cERTIFICATE_CODE = spliters[2];
					cERTIFICATE_TYPE = "1021111";
					break;
				default:
					iD_TYPE = spliters[3];
					aCCOUNT = spliters[2];
					break;
				}
			} catch (Exception e) {
				LOG.error("line:{} parse error!!!", line);
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

	String trim(String value) {
		return Filter.trimNULL(value).replaceAll("[,|;|\\=|\\?|\\*|\\+|\\(|\\)|（|）|\\'|\\t]*", "").trim();
	}
}
