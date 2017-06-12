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
import com.surfilter.mass.tools.services.HbaseToolsModifyColService;
import com.surfilter.mass.tools.util.FileUtil;

/**
 * hbase 更新列
 * 
 * @author zealot
 *
 */
public class HbaseToolsModifyColCli implements CliRunner {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsModifyColCli.class);

	private static String HBASE_TABLE_NAME = "t";
	private static String FILE_NAME = "f";
	private static String MODE = "m";
	private static String DEL_COL = "dc"; // delete column
	private static String ADD_COL = "ac"; // add column
	private static String SP = "sp";// 分隔符

	public Options initOptions() {
		Options options = new Options();

		options.addOption(HBASE_TABLE_NAME, true, "-t hbase table name ");
		options.addOption(FILE_NAME, true, "-f delete file name");
		options.addOption(MODE, true, "-m mode(r[relation] or c[certification])");
		options.addOption(DEL_COL, true, "-dc delete column value");
		options.addOption(ADD_COL, true, "-ac add column value");
		options.addOption(SP, true, "-sp spliter");

		return options;
	}

	public void start(CommandLine cl) {
		String tableName = cl.getOptionValue(HBASE_TABLE_NAME);
		String fileName = cl.getOptionValue(FILE_NAME);
		String mode = cl.getOptionValue(MODE);
		String deleteColumn = cl.getOptionValue(DEL_COL);
		String addColumn = cl.getOptionValue(ADD_COL);
		String spliter = FileUtil.getSp(cl.getOptionValue(SP));

		LOG.debug("delete column, tableName:{}, mode:{}, deleteColumn:{}, addColumn:{}, spliter:{}", tableName, mode,
				deleteColumn, addColumn, spliter);
		File file = new File(fileName);
		if ((!file.exists()) && (file.isFile())) {
			LOG.warn("Can't find file:{}", fileName);
			return;
		}
		File finishDir = new File(file.getParentFile(), "finish");
		if (!finishDir.exists()) {
			try {
				FileUtils.forceMkdir(finishDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		HbaseToolsModifyColService colService = new HbaseToolsModifyColService();
		colService.modifyCol(tableName, mode, file, finishDir, deleteColumn, addColumn, spliter);
	}

	public boolean validateOptions(CommandLine cl) {
		return cl.hasOption(HBASE_TABLE_NAME) && cl.hasOption(FILE_NAME) && cl.hasOption(MODE) && cl.hasOption(SP)
				&& cl.hasOption(DEL_COL) && cl.hasOption(ADD_COL);
	}

	public static void main(String[] args) {
		AdvCli.initRunner(args, "modifyColCli", new HbaseToolsModifyColCli());
	}
}
