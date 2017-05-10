package com.sky.projects.analysis.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sky.projects.analysis.config.Config;

@SuppressWarnings("serial")
public class Services implements Serializable {
	private static final Log LOG = LogFactory.getLog(Services.class);
	private List<String> m_servics = null;

	public Services() {
		try {
			Connection jdbcConn = JDBCUtil.createConnection(JDBCUtil.DBType.MYSQL, Config.DB_IP, Config.DB_PORT,
					Config.DB, Config.DB_USER, Config.DB_PWD);
			this.m_servics = JDBCUtil.getSerivceCodes(jdbcConn, "serviceinfo");
			LOG.info("all serviceCOde:" + this.m_servics);
			jdbcConn.close();
		} catch (SQLException e) {
			LOG.error(e);
			e.printStackTrace();
		}
	}

	public List<String> getM_servics() {
		return this.m_servics;
	}

	public void setM_servics(List<String> m_servics) {
		this.m_servics = m_servics;
	}
}