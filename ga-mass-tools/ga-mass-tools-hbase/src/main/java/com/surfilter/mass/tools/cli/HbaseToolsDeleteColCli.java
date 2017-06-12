package com.surfilter.mass.tools.cli;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.cli.AdvCli;
import com.surfilter.commons.utils.cli.CliRunner;
import com.surfilter.mass.tools.services.HbaseToolsDeleteColService;
import com.surfilter.mass.tools.util.FileUtil;

/**
 * hbase 删除列的工具
 */
public class HbaseToolsDeleteColCli implements CliRunner {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsDeleteColCli.class);

	private static String HBASE_TABLE_NAME = "t";
	private static String FILE_NAME = "f";
	private static String MODE = "m";
	private static String COL = "col";// 指定列名称
	private static String SP = "sp";

	public Options initOptions() {
		Options options = new Options();

		options.addOption(HBASE_TABLE_NAME, true, "-t hbase table name ");
		options.addOption(FILE_NAME, true, "-f delete file name");
		options.addOption(MODE, true, "-m mode(r or c)");
		options.addOption(COL, true, "-col column value");
		options.addOption(SP, true, "-sp file spliter");

		return options;
	}

	public void start(CommandLine cl) {
		String tableName = cl.getOptionValue(HBASE_TABLE_NAME);
		String fileName = cl.getOptionValue(FILE_NAME);
		String mode = cl.getOptionValue(MODE);
		String column = cl.getOptionValue(COL);
		String spliter = FileUtil.getSp(cl.getOptionValue(SP));

		LOG.debug("delete column, tableName:{}, spliter:{}, column:{}", tableName, spliter, column);
		File file = new File(fileName);
		if ((!file.exists()) && (file.isFile())) {
			LOG.error("Can't find file:{}", fileName);
			return;
		}
		File finishDir = new File(file.getParentFile(), "finish");
		if (!finishDir.exists()) {
			try {
				FileUtils.forceMkdir(finishDir);
			} catch (IOException e) {
				LOG.error("mkdir child dir error.", e);
			}
		}
		HbaseToolsDeleteColService colService = new HbaseToolsDeleteColService();
		colService.deleteCol(tableName, mode, file, finishDir, column, spliter);
	}

	public boolean validateOptions(CommandLine cl) {
		return cl.hasOption(HBASE_TABLE_NAME) && cl.hasOption(FILE_NAME) && cl.hasOption(MODE) && cl.hasOption(COL)
				&& cl.hasOption(SP);
	}

	public static void main(String[] args) {
		AdvCli.initRunner(args, "delColCli", new HbaseToolsDeleteColCli());
	}
}
