package com.surfilter.mass.tools.cli;

import com.surfilter.commons.utils.cli.AdvCli;
import com.surfilter.commons.utils.cli.CliRunner;
import com.surfilter.mass.tools.services.Table2TableService;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Table2TableCli implements CliRunner {
	private static Logger LOG = LoggerFactory.getLogger(Table2TableCli.class);
	private static final String SOURCE_TABLE_NAME = "st";
	private static final String DEST_TABLE_NAME = "dt";
	private static final String START_ROW = "srow";

	public Options initOptions() {
		Options options = new Options();
		options.addOption(SOURCE_TABLE_NAME, true, "-st: source table name");
		options.addOption(DEST_TABLE_NAME, true, "-dt: dest table name");
		options.addOption(START_ROW, true, "-srow: start row to scan");
		return options;
	}

	public void start(CommandLine commandCli) {
		String sTableName = commandCli.getOptionValue(SOURCE_TABLE_NAME);
		String dTableName = commandCli.getOptionValue(DEST_TABLE_NAME);
		String startRow = commandCli.getOptionValue(START_ROW);
		Table2TableService service = new Table2TableService();
		LOG.info("******************* The table({}) transport to {} start with row:{}.*******************", sTableName, dTableName, startRow);
		if(sTableName.equals(dTableName)){
			LOG.error("source_table:{} equals dest_table:{}, exit.", sTableName , dTableName);
			System.exit(0);
		}
		try {
			service.table2table(sTableName, dTableName, startRow);
		} catch (Exception e) {
			LOG.info("******************* The table({}) transport end.*******************", sTableName, e);
		}
		LOG.info("******************* The table({}) transport end.*******************", sTableName);
	}

	public boolean validateOptions(CommandLine commandCli) {
		return (commandCli.hasOption(SOURCE_TABLE_NAME)) && (commandCli.hasOption(DEST_TABLE_NAME));
	}

	public static void main(String[] args) {
		AdvCli.initRunner(args, "Table2TableCli", new Table2TableCli());
	}
}
