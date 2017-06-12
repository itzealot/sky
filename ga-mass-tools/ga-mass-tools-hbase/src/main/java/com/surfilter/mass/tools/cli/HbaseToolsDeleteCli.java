package com.surfilter.mass.tools.cli;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.cli.AdvCli;
import com.surfilter.commons.utils.cli.CliRunner;
import com.surfilter.mass.tools.services.HbaseToolsDeleteService;
import com.surfilter.mass.tools.util.FileUtil;

public class HbaseToolsDeleteCli implements CliRunner {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsDeleteCli.class);

	private static final String HBASE_TABLE_NAME = "t"; // table name
	private static final String ROWKEY = "rk";// rowKey
	private static final String ROWKEY_FILE = "f";
	private static final String MODE = "m"; // mode
	private static final String SP = "sp";// 分隔符

	public Options initOptions() {
		Options options = new Options();

		options.addOption(HBASE_TABLE_NAME, true, "-t: hbase table name is must");
		options.addOption(ROWKEY, true, "-rk: table rowkey");
		options.addOption(MODE, true, "-m: mode(r[relaion] or c[certification])");
		options.addOption(ROWKEY_FILE, true, "-f: rowkey file");
		options.addOption(SP, true, "-sp: file spliter");

		return options;
	}

	public void start(CommandLine cd) {
		String tableName = cd.getOptionValue(HBASE_TABLE_NAME);
		String rowkeys = cd.getOptionValue(ROWKEY);
		String rowkeyOfFile = cd.getOptionValue(ROWKEY_FILE);
		String mode = cd.getOptionValue(MODE);
		String spliter = FileUtil.getSp(cd.getOptionValue(SP));

		LOG.debug("Prepare to delete records of table:{}, mode:{}, spliter:{}", tableName, mode, spliter);
		HbaseToolsDeleteService service = new HbaseToolsDeleteService();

		if (cd.hasOption(ROWKEY)) {// 根据rowKey删除，多个使用逗号分隔
			LOG.debug("Rows:{}", rowkeys);
			if (StringUtils.isNotBlank(rowkeys)) {
				service.deleteRows(tableName, rowkeys, mode);
			}
		} else if (cd.hasOption(ROWKEY_FILE)) {// 根据文件删除
			LOG.debug("File:{}", rowkeyOfFile);
			if (StringUtils.isNotBlank(rowkeyOfFile)) {
				File file = new File(rowkeyOfFile);

				if (!file.exists()) {
					LOG.error("Can't find file:{}", rowkeyOfFile);
					return;
				}

				service.deleteRowsInFile(tableName, file, spliter, mode);
			}
		}
	}

	public boolean validateOptions(CommandLine cd) {
		return cd.hasOption(HBASE_TABLE_NAME) && cd.hasOption(HBASE_TABLE_NAME) && cd.hasOption(SP)
				&& (cd.hasOption(ROWKEY) || cd.hasOption(ROWKEY_FILE));
	}

	public static void main(String[] args) {
		AdvCli.initRunner(args, "hbaseToolsDeleteCli", new HbaseToolsDeleteCli());
	}

}
