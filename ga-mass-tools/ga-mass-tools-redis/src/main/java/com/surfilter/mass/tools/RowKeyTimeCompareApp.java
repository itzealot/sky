package com.surfilter.mass.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.conf.Constant;
import com.surfilter.mass.tools.conf.JRedisPoolConfig;
import com.surfilter.mass.tools.conf.MassConfiguration;
import com.surfilter.mass.tools.dao.HBaseDaoAlias;
import com.surfilter.mass.tools.dao.RedisDaoAlias;
import com.surfilter.mass.tools.entity.Certification;
import com.surfilter.mass.tools.entity.Relation;
import com.surfilter.mass.tools.util.Closeables;
import com.surfilter.mass.tools.util.ParseRelationUtil;
import com.surfilter.mass.tools.util.RedisUtil;

/**
 * Redis与Hbase rowKey 访问时间性能比较工具
 * 
 * @author zealot
 *
 */
public class RowKeyTimeCompareApp {
	private static final Logger LOG = LoggerFactory.getLogger(RowKeyTimeCompareApp.class);

	public static void main(String[] args) throws Exception {
		MassConfiguration conf = new MassConfiguration();

		String src = conf.get(Constant.READ_SRC_FILE);
		int counts = conf.getInt(Constant.READ_SLEEP_COUNTS, 5000);
		String serversInfo = conf.get(Constant.WRITE_REDIS_IP) + "|" + conf.get(Constant.WRITE_REDIS_PORT);

		LOG.debug("src file:" + src);

		String[] hbaseParamsArray = conf.get(Constant.WRITE_HBASE_PARAMS).replaceAll(";", ",").split("\\|");
		HBaseDaoAlias.getInstance(hbaseParamsArray);

		BufferedReader reader = null;
		List<Relation> msgs = new ArrayList<>(Constant.RELATION_BATCH_SIZE);

		long hbaseTotalTime = 0;
		long redisTotalTime = 0;

		RedisDaoAlias redisDao = RedisDaoAlias.getInstance(serversInfo);
		HBaseDaoAlias hbaseDao = HBaseDaoAlias.getInstance(hbaseParamsArray);

		try {
			reader = new BufferedReader(new FileReader(src));
			String line = null;
			long index = 0;

			while ((line = reader.readLine()) != null) {
				Relation res = ParseRelationUtil.parse2Relation(line);
				if (res != null) {
					msgs.add(res);
				}
				index++;

				if (msgs.size() >= counts) {
					List<String> rowKeys = new ArrayList<>(msgs.size());
					List<String> hashKeys = new ArrayList<>(msgs.size());

					List<Get> gets = new ArrayList<>(msgs.size());

					for (Relation r : msgs) {
						Certification[] cs = ParseRelationUtil.parse2Certification(r);
						String rowKey = ParseRelationUtil.joinCertificationRowKey(cs[0]);

						String redisKey = RedisUtil.fetchRedisKey(cs[0].getIdType());

						if (redisKey == null) { // is mac
							String mac = cs[0].getId().replaceAll("-", "");
							rowKeys.add("m_" + mac.substring(0, 5));
							hashKeys.add(mac.substring(5));
						} else if (JRedisPoolConfig.CERTIFICATION_OTHER_KEY.equals(redisKey)) {
							rowKeys.add(redisKey);
							hashKeys.add(rowKey);
						} else {
							rowKeys.add(redisKey);
							hashKeys.add(cs[0].getId());
						}

						gets.add(new Get(Bytes.toBytes(rowKey)).addColumn(Bytes.toBytes("cf"),
								Bytes.toBytes("discover_times")));
					}

					long hbaseStart = System.currentTimeMillis();
					Connection conn = null;
					try {
						conn = hbaseDao.getConn();
						// hbaseDao.exists(conn, "certification", gets);
						hbaseDao.gets(conn, "certification", gets);
						long hbaseSpend = System.currentTimeMillis() - hbaseStart;
						LOG.debug("hbase exists, batch size:{}, spends:{}ms", gets.size(), hbaseSpend);
						hbaseTotalTime += hbaseSpend;
						gets.clear();
						gets = null;
					} finally {
						hbaseDao.releaseConn(conn);
					}

					long redisStart = System.currentTimeMillis();
					redisDao.hexists(rowKeys, hashKeys);
					long redisSpend = System.currentTimeMillis() - redisStart;
					LOG.debug("redis exists, batch size:{}, spends:{}ms", rowKeys.size(), redisSpend);
					redisTotalTime += redisSpend;

					rowKeys.clear();
					hashKeys.clear();
					rowKeys = null;
					hashKeys = null;

					msgs.clear(); // 快速释放内存
				}
			}

			LOG.debug("file:{}, total counts:{}, redis total spends:{}ms, hbase total spends:{}ms", src, index,
					redisTotalTime, hbaseTotalTime);
		} catch (Exception e) {
			LOG.error("read file:{} error.{}", src, e);
		} finally {
			Closeables.close(reader);
		}
	}
}
