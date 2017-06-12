package com.surfilter.mass.tools.cli;

import com.surfilter.commons.utils.cli.AdvCli;
import com.surfilter.commons.utils.cli.CliRunner;
import com.surfilter.mass.tools.services.HbaseToolsGenRowkeysService;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseToolsGenRowkeysCli implements CliRunner {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsGenRowkeysCli.class);
	private static final String SOURCE_FIEE = "sf";
	private static final String DELIMITER = "d";
	private static final String DEFAULT_DELIMITER = "\t";

	public Options initOptions() {
		Options options = new Options();
		options.addOption("sf", true, "-sf=xxx,source file");
		options.addOption("d", true, "-d='\t',delimiter");
		return options;
	}

	public void start(CommandLine cmd) {
		String sourceFile = cmd.getOptionValue(SOURCE_FIEE);
		LOG.debug("Start to transfer rowkeys file:{}", sourceFile);
		String delimiter = cmd.getOptionValue(DELIMITER);
		if (StringUtils.isNotEmpty(sourceFile)) {
			File file = new File(sourceFile);
			if (file.exists()) {
				HbaseToolsGenRowkeysService service = new HbaseToolsGenRowkeysService();
				if (StringUtils.isEmpty(delimiter)) {
					delimiter = DEFAULT_DELIMITER;
				}
				service.genRowkeyFile(file, delimiter);
			}
		}
	}

	public boolean validateOptions(CommandLine cmd) {
		return cmd.hasOption(SOURCE_FIEE);
	}

	public static void main(String[] args) {
		AdvCli.initRunner(args, "hbaseToolsGenRowkeysCli", new HbaseToolsGenRowkeysCli());
	}
}
