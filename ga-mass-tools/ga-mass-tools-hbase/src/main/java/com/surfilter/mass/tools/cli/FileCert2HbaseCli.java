package com.surfilter.mass.tools.cli;

import com.surfilter.commons.utils.cli.AdvCli;
import com.surfilter.commons.utils.cli.CliRunner;
import com.surfilter.mass.tools.services.Cert2HbaseService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCert2HbaseCli implements CliRunner {
	private static Logger LOG = LoggerFactory.getLogger(FileCert2HbaseCli.class);
	private static final String CERT_FILE_DIR = "cert_file_dir";
	private static final String TABLE_NAME = "t";
	private static final String COMPANY_ID = "cid";

	public Options initOptions() {
		Options options = new Options();
		options.addOption(CERT_FILE_DIR, true, "cert file dir can't be null");
		options.addOption(TABLE_NAME, true, "-t: table name");
		options.addOption("cid", true, "-cid: company_id");
		return options;
	}

	public void start(CommandLine commandCli) {
		LOG.debug("Start to parse cert file info");
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
				}
			}
			File[] files = file.listFiles();
			Arrays.sort(files);

			Cert2HbaseService service = new Cert2HbaseService(companyId);
			service.importCert2Hbase(files, finishDir, tableName);
		} else {
			LOG.error("dir:{} is not valid directory,pls check!", dir);
		}
	}

	public boolean validateOptions(CommandLine commandCli) {
		return (commandCli.hasOption(CERT_FILE_DIR)) && (commandCli.hasOption(TABLE_NAME))
				&& (commandCli.hasOption(COMPANY_ID));
	}

	public static void main(String[] args) {
		AdvCli.initRunner(args, "cert2HbaseCli", new FileCert2HbaseCli());
	}
}
