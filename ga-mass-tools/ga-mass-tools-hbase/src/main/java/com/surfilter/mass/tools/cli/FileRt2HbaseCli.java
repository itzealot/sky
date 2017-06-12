package com.surfilter.mass.tools.cli;

import com.surfilter.commons.utils.cli.AdvCli;
import com.surfilter.commons.utils.cli.CliRunner;
import com.surfilter.mass.tools.services.Rt2HbaseService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRt2HbaseCli implements CliRunner {
	private static Logger LOG = LoggerFactory.getLogger(FileRt2HbaseCli.class);
	private static final String CERT_FILE_DIR = "rt_file_dir";
	private static final String TABLE_NAME = "t";
	private static final String COMPANY_ID = "cid";

	public Options initOptions() {
		Options options = new Options();
		options.addOption("rt_file_dir", true, "relation file dir can't be null");
		options.addOption("t", true, "-t: table name");
		options.addOption("cid", true, "-cid: company_id");
		return options;
	}

	public void start(CommandLine commandCli) {
		LOG.debug("Start to parse relation file info");
		String dir = commandCli.getOptionValue(CERT_FILE_DIR);
		String tableName = commandCli.getOptionValue(TABLE_NAME);
		String companyId = commandCli.getOptionValue(COMPANY_ID);
		File file = new File(dir);
		if ((file.exists()) && (file.isDirectory())) {
			File finishDir = new File(dir, "finish");
			if (!finishDir.exists()) {
				try {
					FileUtils.forceMkdir(finishDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			File[] files = file.listFiles();
			Arrays.sort(files);

			Rt2HbaseService service = new Rt2HbaseService(companyId);
			service.importRelation2Hbase(files, finishDir, tableName);
		} else {
			LOG.error("dir:{} is not valid directory,pls check!", dir);
		}
	}

	public boolean validateOptions(CommandLine commandCli) {
		return (commandCli.hasOption(CERT_FILE_DIR)) && (commandCli.hasOption(TABLE_NAME))
				&& (commandCli.hasOption(COMPANY_ID));
	}

	public static void main(String[] args) {
		AdvCli.initRunner(args, "rt2HbaseCli", new FileRt2HbaseCli());
	}
}
