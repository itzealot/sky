package com.surfilter.mass.services.match;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.ImcaptureConsts;
import com.surfilter.mass.dao.KeyPerDao;
import com.surfilter.mass.dao.db.JdbcConfig;
import com.surfilter.mass.dao.db.KeyPerDaoImpl;
import com.surfilter.mass.entity.ServiceInfo;

/**
 * 场所与场所类型信息缓存
 * 
 * @author zealot
 *
 */
public class ServiceInfoHelper {

	private static Logger LOG = LoggerFactory.getLogger(ServiceInfoHelper.class);
	private static volatile ServiceInfoHelper instance = null;
	private static Object LOCK = new Object();

	private static volatile boolean isInit = true;// 是否初始化
	private Timer timer;
	private static Map<String, ServiceInfo> map = null;

	private Map<String, String> macFilterMap;
	private KeyPerDao keyPerDao;

	/**
	 * 初始化场所信息的同时，初始化mac filter 标记
	 * 
	 * @param keyPerDao
	 * @return
	 */
	public Map<String, ServiceInfo> getServiceInfo() {
		if (isInit) {// 不需要初始化
			synchronized (LOCK) {
				if (isInit) {
					if (map != null) {
						map.clear();
						map = null;
					}

					map = keyPerDao.querySerCodeType();// 查询数据库的场所、场所类型信息并保存到Map中
					macFilterMap = keyPerDao.getMacFilterConf();// 查询数据库围栏mac过滤配置信息到map中

					LOG.info("reload service_info and mac filter flags, serviceCode size:{}, macFilterMap:{}",
							map.size(), macFilterMap);
					isInit = false; // 修改标记
				}
			}
		}

		return map;
	}

	public Map<String, String> getMacFilterMap() {
		return macFilterMap;
	}

	public static ServiceInfoHelper getInstance(MassConfiguration conf) {
		if (instance == null) {
			synchronized (LOCK) {
				if (instance == null) {
					instance = new ServiceInfoHelper(conf);
				}
			}
		}

		return instance;
	}

	private ServiceInfoHelper(MassConfiguration conf) {
		Integer delay = getInt(conf, ImcaptureConsts.SERVICE_INFO_RELOAD_MINUTES, 60);
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					LOG.debug("service info load timer running...");
					isInit = true;
				}
			}, delay * 60 * 1000, delay * 60 * 1000);
		}

		this.keyPerDao = new KeyPerDaoImpl(JdbcConfig.getInstance(conf));
	}

	public Integer getInt(MassConfiguration conf, String key, int defaultValue) {
		Integer i = conf.getInt(key);
		return i == null || i == 0 ? defaultValue : i;
	}

}
