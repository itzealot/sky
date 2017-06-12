package com.surfilter.mass.tools.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseToolsGenRowkeysService {
	private static Logger LOG = LoggerFactory.getLogger(HbaseToolsGenRowkeysService.class);
	private static final String OK_ = "ok_";

	public void genRowkeyFile(File file, String delimiter) {
		LOG.debug("Start to gen source file: {}", file.getName());
		String destFileName = OK_ + file.getName();
		File destFile = new File(file.getParentFile(), destFileName);

		if (!destFile.exists()) {
			try {
				destFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			bw = getWriter(destFile);
			br = new BufferedReader(new FileReader(file));
			String line = "";

			while ((line = br.readLine()) != null) {
				if (StringUtils.isNotEmpty(line)) {
					String[] array = StringUtils.split(line, delimiter);
					if ((array != null) && (array.length == 4)) {
						String[] reverseArray = { array[2], array[3], array[0], array[1] };
						bw.write(StringUtils.join(array, "|") + "\r\n");
						bw.write(StringUtils.join(reverseArray, "|") + "\r\n");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(bw);
		}
	}

	private BufferedWriter getWriter(File f) throws IOException {
		return new BufferedWriter(new FileWriter(f));
	}
}
