package com.surfilter.mass.tools.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.BaseInfoConfig;
import com.surfilter.mass.tools.conf.JdbcConfig;
import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.db.MysqlDAO;
import com.surfilter.mass.tools.db.impl.MysqlDAOImpl;
import com.surfilter.mass.tools.entity.ZdPerson;
import com.surfilter.mass.tools.entity.ZdPersonInfo;
import com.surfilter.mass.tools.entity.ZtryData;
import com.surfilter.mass.tools.util.Closeables;
import com.surfilter.mass.tools.util.Threads;
import com.surfilter.mass.tools.util.ZtryCatchUtil;

/**
 * Ztry detail 根据url入库到mysql数据库
 * 
 * @author zealot
 *
 */
public class ZtryDetai2DBApp {

	static final Logger LOG = LoggerFactory.getLogger(ZtryDetai2DBApp.class);

	private static final int BTACH_SIZE = 50;
	private static final int COUNTS_SLEEP = 500;
	private static final int MINUTES = 2;
	private static final int MILLS = MINUTES * 60 * 1000;

	public static void main(String[] args) throws IOException {
		MassConfiguration conf = new MassConfiguration();
		BaseInfoConfig info = new BaseInfoConfig(conf);
		MysqlDAO dao = new MysqlDAOImpl(new JdbcConfig(conf));

		if (args.length == 1) {
			File file = new File(args[0]);

			File finishDir = null;

			if (file.exists() && file.isFile()) {
				finishDir = new File(file.getParentFile(), "finish");
				if (!finishDir.exists()) {
					try {
						FileUtils.forceMkdir(finishDir);
					} catch (IOException e) {
					}
				}

				LOG.info("start deal file name:{}", file.getName());
				fetchDetail(info, dao, file, BTACH_SIZE);
				LOG.info("finish deal file name:{}", file.getName());

				FileUtils.moveFileToDirectory(file, finishDir, true);
			} else if (file.isDirectory()) {
				finishDir = new File(file, "finish");
				if (!finishDir.exists()) {
					try {
						FileUtils.forceMkdir(finishDir);
					} catch (IOException e) {
					}
				}

				File[] files = file.listFiles();
				for (File f : files) {
					if (f.isFile()) {
						LOG.info("start deal file name:{}", f.getName());
						fetchDetail(info, dao, f, BTACH_SIZE);
						LOG.info("finish deal file name:{}", f.getName());

						FileUtils.moveFileToDirectory(f, finishDir, true);
					}
				}
			} else {
				LOG.error("src file error:{}", args[0]);
			}
		} else {
			LOG.error("using: ZtryDetai2DBApp ztryDetailUrlPathOrDir");
		}
	}

	public static void fetchDetail(BaseInfoConfig info, MysqlDAO dao, File path, int size) {
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
					appendDetails2DB(dao, info, urls);
					urls.clear();

					LOG.debug("current line:{}", line);
					if (index % COUNTS_SLEEP == 0) {
						LOG.debug("sleep {} minutes", MINUTES);
						Threads.sleep(MILLS);
					} else {
						Threads.sleep(1000);
					}
				}
			}

			if (urls.size() > 0) {
				appendDetails2DB(dao, info, urls);
				urls.clear();
			}

			LOG.debug("finish fetch detail, path:{}, total counts:{}", path, index);
		} catch (Exception e) {
			LOG.error(String.format("read file error, path:%s", path), e);
		} finally {
			Closeables.close(reader);
		}
	}

	private static void appendDetails2DB(MysqlDAO dao, BaseInfoConfig info, List<String> urls) {
		int size = urls.size();
		List<ZdPerson> ps = new ArrayList<>(size);

		for (String url : urls) {
			ZtryData data = ZtryCatchUtil.fetchDetail2ZtryData(url);

			if (data != null && data.getCertificate() != null && !"null".equalsIgnoreCase(data.getCertificate())) {
				ps.add(new ZdPerson(data, info));
			}
		}

		if (ps.isEmpty()) {
			return;
		}

		List<Long> ids = dao.saveZdPersons(ps);

		if (ids == null || ids.isEmpty()) {
			return;
		}

		size = ps.size();
		List<ZdPersonInfo> infos = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			infos.add(new ZdPersonInfo(ps.get(i), ids.get(i)));
		}

		dao.saveZdPersonInfos(infos);

		LOG.debug("fetch detail size:{}", size);
		ps.clear();
		ps = null;

		infos.clear();
		infos = null;
	}
}
