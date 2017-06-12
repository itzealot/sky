package com.surfilter.mass.tools.cli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.util.Closeables;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.Threads;
import com.surfilter.mass.tools.util.ZtryCatchUtil;

/**
 * Ztry detail 爬虫程序
 * 
 * @author zealot
 *
 */
public class ZtryDetailCatchApp {

	static final Logger LOG = LoggerFactory.getLogger(ZtryDetailCatchApp.class);

	private static final int BTACH_SIZE = 100;
	private static final int COUNTS_SLEEP = 5000;
	private static final int MINUTES = 2;
	private static final int MILLS = MINUTES * 60 * 1000;

	public static void main(String[] args) {
		if (args.length == 1) {
			fetchDetail(args[0], BTACH_SIZE, false);
		} else if (args.length == 2) {
			boolean val = "revoke".equals(args[0]) || "[revoke]".equals(args[0]);
			fetchDetail(args[1], BTACH_SIZE, val);
		} else {
			LOG.error("using: ZtryDetailCatchApp [revoke] ztryDetailUrlPath");
		}
	}

	public static void fetchDetail(String path, int size, boolean isRevoke) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(path));
			String line = null;
			long index = 0;
			List<String> urls = new ArrayList<>(size);

			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				urls.add(line);
				index++;

				if (urls.size() >= size) {
					appendDetails(path, size, urls, isRevoke);
					urls.clear();

					LOG.debug("current line:{}", index);
					if (index % COUNTS_SLEEP == 0) {
						LOG.debug("sleep {} minutes", MINUTES);
						Threads.sleep(MILLS);
					} else {
						Threads.sleep(1000);
					}
				}
			}

			if (urls.size() > 0) {
				appendDetails(path, size, urls, isRevoke);
				urls.clear();
			}

			LOG.debug("finish fetch detail, path:{}, total counts:{}", path, index);
		} catch (Exception e) {
			LOG.error("read file error, path:{}, {}", path, e);
			e.printStackTrace();
		} finally {
			Closeables.close(reader);
		}
	}

	private static void appendDetails(String path, int size, List<String> urls, boolean isRevoke) {
		List<String> results = new ArrayList<>(size);
		for (String url : urls) {
			String detail = null;

			if (isRevoke) {
				detail = ZtryCatchUtil.fetchRevokeDetail(url);
			} else {
				detail = ZtryCatchUtil.fetchDetail(url);
			}

			if (detail != null) {
				results.add(detail);
			}
		}
		FileUtil.append(FileUtil.fileNameAppendSuffix(path, "_detail"), results);
		LOG.debug("fetch detail size:{}", size);
		results.clear();
		results = null;
	}
}
