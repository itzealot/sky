package com.surfilter.mass.tools.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import iie.mdss.client.Executer;
import iie.mdss.client.IQueryClient;

@SuppressWarnings("serial")
public class IQueryUtil implements Serializable {
	private static final Logger LOG = Logger.getLogger(IQueryUtil.class);

	private static IQueryClient client = null;
	private static Executer exe = null;
	private String tableName;

	static {
		try {
			client = new IQueryClient();
			exe = client.getExecuter();
		} catch (Exception e) {
			LOG.error("create IQueryClient error.", e);
			throw new RuntimeException("create IQueryClient error.");
		}
	}

	public IQueryUtil(String tableName) {
		this.tableName = tableName;
	}

	public void batch(List<List<String>> dataLLists) {
		try {
			for (List<String> dataList : dataLLists) {
				exe.insertByValues(tableName, new ArrayList<String>(dataList));
			}
			LOG.info("iQuery insert data to " + tableName + " ok! size: " + dataLLists.size());
		} catch (Exception e) {
			LOG.error("iQuery Insert data to " + tableName + " failed!", e);
		}
	}

	public void close() {
		exe = null;
		client = null;
	}
}
