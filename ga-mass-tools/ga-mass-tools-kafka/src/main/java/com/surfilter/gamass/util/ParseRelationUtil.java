package com.surfilter.gamass.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.hadoop.hbase.util.MD5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.gamass.conf.Constant;
import com.surfilter.gamass.conf.JRedisPoolConfig;
import com.surfilter.gamass.entity.Certification;
import com.surfilter.gamass.entity.CertificationTrack;
import com.surfilter.gamass.entity.Relation;
import com.surfilter.mass.tools.util.CertificationFilter;
import com.surfilter.mass.tools.util.Closeables;
import com.surfilter.mass.tools.util.Filter;
import com.surfilter.mass.tools.util.Threads;

public class ParseRelationUtil {
	private static final Logger LOG = LoggerFactory.getLogger(ParseRelationUtil.class);
	private static final String CP = "|";

	public static String addRelationHashPrefix(String rowkey, int lenth) {
		String[] rk = rowkey.split("\\" + CP);
		String from = rk[0] + CP + rk[1];
		return MD5Hash.getMD5AsHex(from.getBytes()).substring(0, lenth) + CP + rowkey;
	}

	public static String addHashPrefix(String rowkey, int lenth) {
		return MD5Hash.getMD5AsHex(rowkey.getBytes()).substring(0, lenth) + CP + rowkey;
	}

	public static void read(BlockingQueue<String> queue, File file, long sleep, int counts) throws Exception {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			long index = 0;

			while ((line = reader.readLine()) != null) {
				String res = parse(line);
				if (res != null)
					queue.put(res);
				index++;

				if (queue.size() >= counts) {
					LOG.debug("file:{}, current line:{}, queue size:{}", file.getName(), index, queue.size());
					Threads.sleep(sleep);
				}
			}

			LOG.debug("file:{}, total counts:{}", file.getName(), index);
		} catch (Exception e) {
			LOG.error("read file:{} into BlockingQueue error.{}", file.getName(), e);
			throw new Exception("read file into BlockingQueue error.");
		} finally {
			Closeables.close(reader);
		}
	}

	public static void read2Relation(BlockingQueue<Relation> queue, File file, long sleep, int counts) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			long index = 0;

			while ((line = reader.readLine()) != null) {
				Relation res = parse2Relation(line);
				if (res != null)
					queue.put(res);
				index++;

				if (queue.size() >= counts) {
					LOG.debug("file:{}, current line:{}, queue size:{}", file.getName(), index, queue.size());
					Threads.sleep(sleep);
				}
			}

			LOG.debug("file:{}, total counts:{}", file.getName(), index);
		} catch (Exception e) {
			LOG.error("read file:{} into BlockingQueue error.{}", file.getName(), e);
		} finally {
			Closeables.close(reader);
		}
	}

	public static Relation parse2Relation(String line) {
		String[] arrays = line.split("\t");

		if (arrays.length != 10 && arrays.length != 11) {
			LOG.error("error length line:" + line);
			return null;
		}

		String idFrom = arrays[0];
		String fromType = arrays[1];
		String idTo = arrays[2];
		String toType = arrays[3];
		String firstStartTime = arrays[4];
		String firstTerminalNum = arrays[5];
		String source = trim2MULL(arrays[6]);
		String createTime = Dates.nowUnixtimeStr();
		String sysSource = trim2MULL(arrays[8]);

		String createTimeP = null;
		String companyId = null;

		if (arrays.length == 11) { // has compan_id
			companyId = trimCompanyId(arrays[9]);
			createTimeP = arrays[10];
		} else {
			companyId = Constant.MULL;
			createTimeP = arrays[9];
		}

		Relation relation = new Relation(idFrom, fromType, idTo, toType, firstStartTime, firstTerminalNum, source,
				createTime, sysSource, companyId, createTimeP);

		return validate(line, relation) ? relation : null;
	}

	public static String parse(String line) {
		String[] arrays = line.split("\t");

		if (arrays.length != 10 && arrays.length != 11) {
			LOG.error("error length line:" + line);
			return null;
		}

		String idFrom = arrays[0];
		String fromType = arrays[1];
		String idTo = arrays[2];
		String toType = arrays[3];
		String firstStartTime = arrays[4];
		String firstTerminalNum = arrays[5];
		String source = trim2MULL(arrays[6]);
		String createTime = arrays[7];
		String sysSource = trim2MULL(arrays[8]);

		String createTimeP = null;
		String companyId = null;

		if (arrays.length == 11) { // has compan_id
			companyId = trimCompanyId(arrays[9]);
			createTimeP = arrays[10];
		} else {
			companyId = Constant.MULL;
			createTimeP = arrays[9];
		}

		Relation relation = new Relation(idFrom, fromType, idTo, toType, firstStartTime, firstTerminalNum, source,
				createTime, sysSource, companyId, createTimeP);

		return validate(line, relation) ? relation.join() : null;
	}

	public static boolean validate(String line, Relation relation) {
		// validate idFrom with fromType
		if (CertificationFilter.evaluate(relation.getIdFrom(), relation.getFromType()) == null) {
			return false;
		}

		// validate idTo with toType
		if (CertificationFilter.evaluate(relation.getIdTo(), relation.getToType()) == null) {
			return false;
		}

		// validate firstTerminalNum
		if (relation.getFirstTerminalNum().length() != 14) {
			LOG.error("error firstTerminalNum relation:{}", line);
			return false;
		}

		// validate createTimeP
		if (relation.getCreateTimeP().length() != 8) {
			LOG.error("error create_time_p relation:{}", line);
			return false;
		}

		try {
			// 过滤非法的身份数据
			String fromType = relation.getFromType();
			if(fromType.length() != 7) {
				return false;
			}
			Integer.parseInt(fromType);
			
			String toType = relation.getToType();
			if(toType.length() != 7) {
				return false;
			}
			Integer.parseInt(toType);
			
			// validate firstStartTime or createTime
			if (relation.getFirstStartTime().length() != 10 || relation.getCreateTime().length() != 10) {
				return false;
			}
			Long.parseLong(relation.getFirstStartTime());
			Long.parseLong(relation.getCreateTime());
			Integer.parseInt(relation.getFirstTerminalNum().substring(0, 6));
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static <T> void writeLists(final File file, final List<T> lines) {
		BufferedWriter writer = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file, true);
			writer = new BufferedWriter(new OutputStreamWriter(fos, Charset.forName("UTF-8")));

			for (T line : lines) {
				writer.write(String.valueOf(line));
				writer.write("\n");
			}
		} catch (IOException e) {
			LOG.error("write lines into file and create ok file error.", e);
		} finally {
			Closeables.close(writer, fos);
		}
	}

	public static String trimCompanyId(String str) {
		return (Filter.isBlank(str) || str.length() != 9) ? Constant.MULL : str;
	}

	public static String trim2MULL(String str) {
		return Filter.isBlank(str) ? Constant.MULL : str;
	}

	public static String virtualOrReal(String idType) {
		return idType.startsWith("102") ? "0" : "1";
	}

	public static String provinceCode(String serviceCode) {
		return serviceCode.substring(0, 2) + "0000";
	}

	public static String cityCode(String serviceCode) {
		return serviceCode.substring(0, 4) + "00";
	}

	public static String areaCode(String serviceCode) {
		return serviceCode.substring(0, 6);
	}

	/**
	 * pattern:idFrom|fromType|idTo|toType
	 * 
	 * @param r
	 * @return
	 */
	public static String joinRelationRowKey(Relation r, boolean versionIsFxj) {
		String result = new StringBuffer(128).append(r.getIdFrom()).append(CP).append(r.getFromType()).append(CP)
				.append(r.getIdTo()).append(CP).append(r.getToType()).toString();
		if (versionIsFxj) { // 分县局版本
			return addRelationHashPrefix(result, 8);
		}
		return result;
	}

	/**
	 * pattern:idTo|toType|idFrom|fromType
	 * 
	 * @param r
	 * @return
	 */
	public static String joinRelationReverseRowKey(Relation r, boolean versionIsFxj) {
		String rowkey = new StringBuffer(128).append(r.getIdTo()).append(CP).append(r.getToType()).append(CP)
				.append(r.getIdFrom()).append(CP).append(r.getFromType()).toString();

		if (versionIsFxj) {
			return addRelationHashPrefix(rowkey, 8);
		}

		return rowkey;
	}

	/**
	 * pattern:firstStartTime|firstTerminalNum|source|createTime|sysSource
	 * 
	 * @param r
	 * @return
	 */
	public static String joinRelationValues(Relation r) {
		return new StringBuffer(128).append(r.getFirstStartTime()).append(CP).append(r.getFirstTerminalNum()).append(CP)
				.append(r.getSource()).append(CP).append(r.getCreateTime()).append(CP).append(r.getSysSource())
				.toString();
	}

	/**
	 * pattern:id|idType
	 * 
	 * @param c
	 * @return
	 */
	public static String joinCertificationRowKey(Certification c, boolean versionIsFxj) {
		String rowkey = new StringBuffer(64).append(c.getId()).append(CP).append(c.getIdType()).toString();
		if (versionIsFxj) {// 分县局版本
			return addHashPrefix(rowkey, 8);
		}
		return rowkey;
	}

	/**
	 * pattern:firstStartTime|firstTerminalNum|source|createTime|sysSource
	 * 
	 * @param r
	 * @return
	 */
	public static String joinCertificationValues(Certification c) {
		return new StringBuffer(128).append(c.getFirstStartTime()).append(CP).append(c.getFirstTerminalNum()).append(CP)
				.append(c.getSource()).append(CP).append(c.getCreateTime()).append(CP).append(c.getSysSource())
				.toString();
	}

	public static String macHashKey(String mac) {
		return mac.substring(5);
	}

	public static String macRedisKey(String mac) {
		return JRedisPoolConfig.CERTIFICATION_MAC_PREFIX_KEY + mac.substring(0, 5);
	}

	public static void parseAndWriteRelation(List<Relation> msgs, String relationDir, String hbaseParams,
			String relationTableName, boolean versionIsFxj) {
		ParseRelationUtil.writeLists(new File(relationDir + "/relation.txt"), msgs);
		// TODO update into hbase
		insertRelation2Hbase(msgs, hbaseParams, relationTableName, versionIsFxj);
	}

	public static void insertRelation2Hbase(List<Relation> msgs, String hbaseParams, String relationTableName,
			boolean versionIsFxj) {
		List<String> rowKeys = new ArrayList<>(msgs.size());
		List<String> values = new ArrayList<>(msgs.size());

		for (Relation r : msgs) {
			rowKeys.add(ParseRelationUtil.joinRelationRowKey(r, versionIsFxj));
			values.add(ParseRelationUtil.joinRelationValues(r));
		}

		HbaseUtil.migrateRelation2Hbase(hbaseParams, rowKeys, values, relationTableName, versionIsFxj);

		rowKeys.clear();
		rowKeys = null;

		values.clear();
		values = null;
	}

	public static void parseAndWriteCertification(List<Relation> msgs, String certDir, String hbaseParams,
			String cetificationTableName, String serversInfo, boolean versionIsFxj) {
		List<Certification> certs = new ArrayList<>(msgs.size() * 2);

		for (Relation r : msgs) {
			Certification[] cs = parse2Certification(r);
			if (cs != null) {
				for (Certification c : cs)
					certs.add(c);
			}
		}

		ParseRelationUtil.writeLists(new File(certDir + "/certification.txt"), certs);
		insertCertification2HbaseAndRedis(certs, hbaseParams, cetificationTableName, serversInfo, versionIsFxj);

		certs.clear();
		certs = null;
	}

	public static void insertCertification2HbaseAndRedis(List<Certification> certs, String hbaseParams,
			String cetificationTableName, String serversInfo, boolean versionIsFxj) {
		List<String> rowKeys = new ArrayList<>(certs.size());
		List<String> keys = new ArrayList<>(certs.size());
		List<String> values = new ArrayList<>(certs.size());

		for (Certification c : certs) {
			rowKeys.add(ParseRelationUtil.joinCertificationRowKey(c, versionIsFxj));
			keys.add(ParseRelationUtil.joinCertificationRowKey(c, false));
			values.add(ParseRelationUtil.joinCertificationValues(c));
		}

		RedisUtil.updateCertificationInRedis(keys, values, serversInfo, versionIsFxj);
		keys.clear();
		keys = null;

		HbaseUtil.migrateCertification2Hbase(hbaseParams, rowKeys, values, cetificationTableName);
		rowKeys.clear();
		rowKeys = null;

		values.clear();
		values = null;
	}

	public static Certification[] parse2Certification(Relation r) {
		Certification[] results = new Certification[2];

		results[0] = new Certification(r.getIdFrom(), r.getFromType(), r.getFirstStartTime(), r.getFirstTerminalNum(),
				r.getSource(), r.getCreateTime(), r.getSysSource(), r.getCompanyId(), r.getCreateTimeP());

		results[1] = new Certification(r.getIdTo(), r.getToType(), r.getFirstStartTime(), r.getFirstTerminalNum(),
				r.getSource(), r.getCreateTime(), r.getSysSource(), r.getCompanyId(), r.getCreateTimeP());

		return results;
	}

	public static void parseAndWriteCertificationTrack(List<Relation> msgs, String certDir) {
		List<CertificationTrack> certTracks = new ArrayList<>(msgs.size() * 2);

		for (Relation r : msgs) {
			CertificationTrack[] cs = parse2CertificationTrack(r);
			if (cs != null) {
				for (CertificationTrack c : cs)
					certTracks.add(c);
			}
		}

		ParseRelationUtil.writeLists(new File(certDir + "/certTrack.txt"), certTracks);

		certTracks.clear();
		certTracks = null;
	}

	public static CertificationTrack[] parse2CertificationTrack(Relation r) {
		CertificationTrack[] certTracks = new CertificationTrack[2];
		String serviceCode = r.getFirstTerminalNum();

		certTracks[0] = new CertificationTrack(r.getIdFrom(), r.getFromType(),
				ParseRelationUtil.virtualOrReal(r.getFromType()), "MULL", ParseRelationUtil.provinceCode(serviceCode),
				ParseRelationUtil.cityCode(serviceCode), ParseRelationUtil.areaCode(serviceCode), serviceCode,
				r.getFirstStartTime(), "1", r.getSource(), r.getCompanyId(), "MULL", r.getCreateTime(),
				r.getSysSource(), r.getCreateTimeP());

		certTracks[1] = new CertificationTrack(r.getIdTo(), r.getToType(),
				ParseRelationUtil.virtualOrReal(r.getToType()), "MULL", ParseRelationUtil.provinceCode(serviceCode),
				ParseRelationUtil.cityCode(serviceCode), ParseRelationUtil.areaCode(serviceCode), serviceCode,
				r.getFirstStartTime(), "1", r.getSource(), r.getCompanyId(), "MULL", r.getCreateTime(),
				r.getSysSource(), r.getCreateTimeP());

		return certTracks;
	}
}
