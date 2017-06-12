package com.surfilter.mass.tools.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.CarInfoConfig;
import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.Threads;

/**
 * 抓取所有的车辆信息,tableName=T_ITGS_VEHICLE
 * 
 * @author zealot
 *
 */
public class AllCarInfoCatchApp {

	static final Logger LOG = LoggerFactory.getLogger(AllCarInfoCatchApp.class);

	public static void main(String[] args) {
		CarInfoConfig config = new CarInfoConfig(new MassConfiguration());

		File file = new File(config.getDst());
		int counts = 0;
		int errorCounts = 0;
		int pageSize = config.getPageSize();

		for (int i = config.getPageNo(); i <= config.getMaxPageNo(); i++) { // 分页查询
			String url = String.format(config.getCatchUrl(), i + "", pageSize);
			LOG.debug("current page: {}", i);

			try {
				Document doc = Jsoup.connect(url).get();
				if (doc != null) {
					Elements rows = doc.getElementsByTag("row");
					List<String> results = new ArrayList<>(pageSize);
					StringBuffer buffer = new StringBuffer(256);

					for (Element row : rows) {
						List<Node> nodes = row.childNodes();

						if (nodes != null && !nodes.isEmpty()) {
							String result = parse(buffer, nodes);
							if (result != null) {
								results.add(result);
								counts++;
							} else {
								errorCounts++;
							}
						} else {
							errorCounts++;
						}
					}

					FileUtil.append(file, results);
					results.clear();
					results = null;

					if (errorCounts != 0 && errorCounts % 100 == 0) {
						LOG.debug("error counts:{}", errorCounts);
					}

					if (counts % 2000 == 0) {
						LOG.debug("right counts:{}", counts);
					}
				}
			} catch (Exception e) {
				LOG.error("fetch vehicle info error:, url:{}, {}", url, e);
				e.printStackTrace();
			}

			Threads.sleep(1000);
		}

		LOG.debug("all error counts:{}", errorCounts);
		LOG.debug("all right counts:{}", counts);
	}

	private static String parse(StringBuffer buffer, List<Node> nodes) {
		int i = 0, len = nodes.size() - 1;
		for (; i < len; i++) {
			buffer.append(trim(nodes.get(i).toString()));
			buffer.append("\t");
		}
		String result = buffer.append(trim(nodes.get(i).toString())).toString();
		buffer.setLength(0);
		return result;
	}

	private static String trim(String str) {
		if (str == null) {
			return "null";
		}
		String newStr = str.trim().replace("\t", "");
		return newStr.isEmpty() ? "null" : newStr;
	}
}
