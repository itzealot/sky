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
import com.surfilter.mass.tools.services.HbaseToolsModifyValueService;
import com.surfilter.mass.tools.util.FileUtil;

/**
 * hbase 更新范围内某列的值
 * 
 * @author zealot
 *
 */
public class HbaseToolsModifyValueCli implements CliRunner {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsModifyValueCli.class);

	private static String HBASE_TABLE_NAME = "t";// table name
	private static String FILE_NAME = "f";// file
	private static String MODE = "m";// mode
	private static String COL = "col"; // the col
	private static String SP = "sp"; // the spliter
	private static String VALUE = "value";// the value
	private static String VALUE_TYPE = "type"; // long,int,string
	private static String RANGE = "range"; // range value

	public Options initOptions() {
		Options options = new Options();

		options.addOption(HBASE_TABLE_NAME, true, "-t : hbase table name ");
		options.addOption(FILE_NAME, true, "-f : delete file name");
		options.addOption(MODE, true, "-m : mode(r or c)");
		options.addOption(COL, true, "-col : column value");
		options.addOption(SP, true, "-sp : file spliter");
		options.addOption(VALUE, true, "-value : put value");
		options.addOption(VALUE_TYPE, true, "-type : value type[long,int,string]");
		options.addOption(RANGE, true, "-range : range value like start-end");

		return options;
	}

	public void start(CommandLine cl) {
		String tableName = cl.getOptionValue(HBASE_TABLE_NAME);
		String fileName = cl.getOptionValue(FILE_NAME);
		String mode = cl.getOptionValue(MODE);
		String column = cl.getOptionValue(COL);
		String spliter = FileUtil.getSp(cl.getOptionValue(SP));
		String value = cl.getOptionValue(VALUE);
		String valueType = cl.getOptionValue(VALUE_TYPE);
		String range = cl.getOptionValue(RANGE);

		if (!"c".equals(mode) && !"r".equals(mode)) {
			throw new IllegalArgumentException("mode must be r or c");
		}

		LOG.debug("modify value range, tableName:{}, spliter:{}, column:{}, range:{}, value:{}, valueType:{}",
				tableName, spliter, column, range, value, valueType);
		
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
		HbaseToolsModifyValueService colService = new HbaseToolsModifyValueService();
		colService.putRowsInFile(tableName, file, spliter, mode, column, value, valueType, range);
	}

	public boolean validateOptions(CommandLine cl) {
		return cl.hasOption(HBASE_TABLE_NAME) && cl.hasOption(FILE_NAME) && cl.hasOption(MODE) && cl.hasOption(COL)
				&& cl.hasOption(SP) && cl.hasOption(VALUE) && cl.hasOption(VALUE_TYPE) && cl.hasOption(RANGE);
	}

	public static void main(String[] args) {
		AdvCli.initRunner(args, "modifyValueRange", new HbaseToolsModifyValueCli());
	}
}