package com.surfilter.mass;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.dao.db.JdbcConfig;
import com.surfilter.mass.entity.MatchInfo;
import com.surfilter.mass.services.match.algthm.AhoCorasick;

/**
 * 报警程序上下文
 * 
 * @author zealot
 *
 */
public class ImcaptureContext {

	private MassConfiguration conf;
	private BlockingQueue<String> blockQueue;
	private AhoCorasick<MatchInfo> ac;
	private JdbcConfig jdbcConfig;
	private Set<String> macCompanyKeys; // redis 查询出的 mac 厂商前缀

	public ImcaptureContext(MassConfiguration conf) {
		this.conf = conf;
		this.blockQueue = new ArrayBlockingQueue<>(20000);
		jdbcConfig = JdbcConfig.getInstance(conf);
	}

	public void setMacCompanyKeys(Set<String> macCompanyKeys) {
		this.macCompanyKeys = macCompanyKeys;
	}

	public Set<String> getMacCompanyKeys() {
		return macCompanyKeys;
	}

	public String getString(String key) {
		return conf.get(key);
	}

	public String gets(String key) {
		return conf.getStrings(key);
	}

	public Integer getInt(String key) {
		return conf.getInt(key);
	}

	public Integer getInt(String key, int defaultValue) {
		Integer i = conf.getInt(key);
		if (i == null || i == 0) {
			return defaultValue;
		}
		return i;
	}

	public MassConfiguration getConf() {
		return conf;
	}

	public BlockingQueue<String> getBlockQueue() {
		return blockQueue;
	}

	public AhoCorasick<MatchInfo> getAc() {
		return ac;
	}

	public JdbcConfig getJdbcConfig() {
		return jdbcConfig;
	}

	public void setJdbcConfig(JdbcConfig jdbcConfig) {
		this.jdbcConfig = jdbcConfig;
	}

}
