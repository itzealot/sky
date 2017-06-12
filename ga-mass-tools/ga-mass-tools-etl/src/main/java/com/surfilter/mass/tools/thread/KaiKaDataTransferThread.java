package com.surfilter.mass.tools.thread;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SysConsts;
import com.surfilter.mass.tools.entity.KaiKaData;
import com.surfilter.mass.tools.util.DateUtil;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.Filter;
import com.surfilter.mass.tools.util.Id15To18;

public class KaiKaDataTransferThread extends AbstractDataTransferThread<KaiKaData> {
	private static final Logger LOG = LoggerFactory.getLogger(KaiKaDataTransferThread.class);

	private String dst; // json 存储目录
	private String spliter;

	public KaiKaDataTransferThread(BlockingQueue<String> queue, MassConfiguration conf) {
		super(queue);
		this.spliter = conf.get(SysConsts.DATA_SPLITER_PROPERTY, "\t");
		this.dst = conf.get(SysConsts.DATA_DIR_PROPERTY);
		new File(this.dst).mkdirs();
	}

	@Override
	public KaiKaData newInstance(String[] arrays) {
		String sERVICE_CODE = arrays[0];
		String uSER_NAME = arrays[1];
		String uSER_PASS = arrays[2];
		String cARD_TYPE = "1020005";
		String cARD_NUM = arrays[3];
		if (Filter.isBlank(cARD_NUM)) {
			LOG.error("error CARD_NUM:{}", Arrays.asList(arrays));
			return null;
		}

		String cARD_COMPANY = arrays[4];
		String uSER_NUM = arrays[5];
		String cERTIFICATE_TYPE = "1021111";
		String cERTIFICATE_CODE = Id15To18.id15Or18Filter(arrays[6]);
		if (Filter.isBlank(cERTIFICATE_CODE)) {
			LOG.error("error cERTIFICATE_CODE:{}", Arrays.asList(arrays));
			return null;
		}

		int sEX = 0;
		try {
			sEX = Integer.parseInt(arrays[7]);
		} catch (Exception e) {
		}

		String wORK_COMPANY = arrays[8];
		String cOUNTRY = arrays[9];

		long oPEN_CARD_TIME = 0;
		try {
			oPEN_CARD_TIME = Long.parseLong(arrays[10]);
		} catch (Exception e) {
			oPEN_CARD_TIME = DateUtil.unixTime(arrays[10], "yyyy-MM-dd HH:mm:ss", Locale.US, 0);
		}

		long vALIDATE_TIME = 0;
		try {
			vALIDATE_TIME = Long.parseLong(arrays[11]);
		} catch (Exception e) {
			vALIDATE_TIME = DateUtil.unixTime(arrays[11], "yyyy-MM-dd", Locale.US, 0);
		}

		String pHONE = arrays[12];

		if (oPEN_CARD_TIME < 1L) {
			LOG.error("error OPEN_CARD_TIME:{}", Arrays.asList(arrays));
			return null;
		}

		return new KaiKaData(sERVICE_CODE, uSER_NAME, uSER_PASS, cARD_TYPE, cARD_NUM, cARD_COMPANY, uSER_NUM,
				cERTIFICATE_TYPE, cERTIFICATE_CODE, sEX, wORK_COMPANY, cOUNTRY, oPEN_CARD_TIME, vALIDATE_TIME, pHONE);
	}

	@Override
	public String getPath() {
		return this.dst + "/" + DateUtil.dateToStr(new Date(), "yyyyMMddHHmmss") + FileUtil.random()
				+ "_120_440300_723005104_019.log";
	}

	@Override
	public String getSpliter() {
		return spliter;
	}

	@Override
	public int getFiledSize() {
		return SysConsts.DATA_KK_FILED_SIZE;
	}

}
