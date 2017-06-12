package com.surfilter.mass.tools;
/**
 * Created by liujikuan on 2016/10/17.
 */

import com.surfilter.mass.tools.dao.HbaseDao;
import com.surfilter.mass.tools.dao.RedisDao;
import net.whitbeck.rdbparser.Entry;
import net.whitbeck.rdbparser.KeyValuePair;
import net.whitbeck.rdbparser.RdbParser;
import net.whitbeck.rdbparser.ValueType;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.beans.beancontext.BeanContextSupport;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * @author liujikuan
 * @see : https://github.com/jwhitbeck/java-rdb-parser
 */
public class RdbFilePrinter {
    private static Logger LOG = LoggerFactory.getLogger(RdbFilePrinter.class);

    /**
     * 模仿hbase-cli的行为，对m_开头的key进行操作
     *
     * @param file      dump.rdb文件路径
     * @param startDate
     * @param endDate
     */
    public static void macHandler(File file, long startDate, long endDate) {
        try (RdbParser parser = new RdbParser(file)) {
            Entry e;
            while ((e = parser.readNext()) != null) {
                switch (e.getType()) {
                    case KEY_VALUE_PAIR:
                        KeyValuePair kvp = (KeyValuePair) e;
                        if (ValueType.HASHMAP_AS_ZIPLIST == kvp.getValueType()) {
                            String key = new String(kvp.getKey(), "ASCII");
                            if (key.startsWith("m_") && key.split("_").length > 1) {
                                insertMAC2HBase(startDate, endDate, kvp, key.split("_")[1]);
                                //statsMACinRedis(kvp, key.split("_")[1]);
                            }
                        }
                        break;
                }
            }
            HbaseDao.table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void certificationHandler(File file, long startDate, long endDate) {
        try (RdbParser parser = new RdbParser(file)) {
            Entry e;
            while ((e = parser.readNext()) != null) {
                switch (e.getType()) {
                    case KEY_VALUE_PAIR:
                        KeyValuePair kvp = (KeyValuePair) e;
                        if (ValueType.HASHMAP_AS_ZIPLIST == kvp.getValueType()) {
                            String key = new String(kvp.getKey(), "ASCII");
                            if (key.startsWith("certification_") || key.startsWith("m_")) {
                                deleteCertification(startDate, endDate, key, kvp.getValues());
                            }
                        }
                        break;
                }
            }
            System.out.println("the total deleted num is "+recordnum);
            System.out.println("the total certification num is "+totalNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int recordnum = 0;
    static int totalNum = 0;
    private static void deleteCertification(long startDate, long endDate, String key, List<byte[]> values) throws IOException {
        int count = 0;
        int record = 0;
        String hashKey = null;
        List<String> hashKeyList = new ArrayList<String>();
        for (byte[] val : values) {
            if (count % 2 == 0) {
                hashKey = new String(val, "ASCII");
            } else {
                //判断数据是否过期
                String value = new String(val, "ASCII");
                String[] vals = value.split("\\|");
                if (vals.length >= 3) {
                    long time = Long.parseLong(vals[2]);
                    if (time > startDate && time < endDate) {
                        hashKeyList.add(hashKey);
                        LOG.debug(key + "\t" + hashKey + "\t" + time);
                        record++;
                    }
                    if (hashKeyList.size() == 5000) {
                        //去redis中删除数据
                        RedisDao.delKeys(key, hashKeyList.toArray(new String[hashKeyList.size()]));
                        hashKeyList.clear();
                    }
                }
            }
            count++;
        }
        System.out.println("key " + key + " has removed " + record + " hashKeys");
        recordnum = recordnum + record;
        totalNum = totalNum + count/2;
        //去redis中删除数据
        RedisDao.delKeys(key, hashKeyList.toArray(new String[hashKeyList.size()]));
    }
/*    static int invalidateNum = 0;
    static int macNum = 0;
    private static void statsMACinRedis(KeyValuePair kvp, String pre_mac) {
        int count = 0;
        for (byte[] val : kvp.getValues()) {
            count++;
        }
        count = count / 2;
        String ch = pre_mac.charAt(1) + "";
        if (!"0".equals(ch) && !"4".equals(ch) && !"8".equals(ch) && !"C".equals(ch)) {
            invalidateNum = invalidateNum + count;
            System.out.println("m_" + pre_mac);
        }
        macNum = macNum + count;
    }*/

    //把mac信息录入到hbase的身份表的同时增加first_start_time1和first_terminal_num1两个字段并分别赋值为first_start_time、first_terminal_num两个字段的值
    private static void insertMAC2HBase(long startDate, long endDate, KeyValuePair kvp, String pre_mac) {
        int num = 0;
        String mac = null;

        List<Put> puts = new ArrayList<Put>();
        List<String> rowKeys = new ArrayList<String>();
        List<String> hashKeyList = new ArrayList<String>();
        try {
            for (byte[] val : kvp.getValues()) {
                String value = new String(val, "ASCII");
                if (num % 2 == 0) {
                    mac = pre_mac + value;
                } else {
                    String[] strs = value.split("\\|");
                    if (strs.length > 3) {
                        long lst = Long.parseLong(strs[2]);
                        if (lst > startDate && lst < endDate) {
                            buildPuts(mac, strs, puts);
                            rowKeyBuilder(mac, rowKeys);
                            hashKeyList.add(mac.substring(5));
                            LOG.debug("  mac ： " + mac);
                            LOG.debug("Values： " + value);
                        }
                    }
                }
                num++;
                if (num % 5000 == 0) {
                    HbaseDao.commitPuts(puts);
                    appendRecordWithSth(rowKeys);
                    RedisDao.delKeys("m_" + pre_mac, hashKeyList.toArray(new String[hashKeyList.size()]));
                    puts.clear();
                    rowKeys.clear();
                    hashKeyList.clear();
                }
            }
            HbaseDao.commitPuts(puts);
            appendRecordWithSth(rowKeys);
            RedisDao.delKeys("m_" + pre_mac, hashKeyList.toArray(new String[hashKeyList.size()]));
            rowKeys.clear();
            puts.clear();
            hashKeyList.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void appendRecordWithSth(List<String> rowKeys) {
        //从hbase取数据
        //map的key是rowkey，value是rowkey对应的first_start_time和first_terminal_num
        Map<String, String[]> map = HbaseDao.get(rowKeys);
        //往hbase插数据
        HbaseDao.mutateData(map);
    }

    private static void rowKeyBuilder(String mac, List<String> rowKeys) {
        String temp = mac.replaceAll("(.{2})", "$1-");
        String macNew = temp.substring(0, temp.length() - 1);
        rowKeys.add(macNew);
    }

    /**
     * @param rowKey
     * @param vals
     * @param puts
     * @throws IOException
     */
    private static void buildPuts(String rowKey, String[] vals, List<Put> puts) throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));// 设置rowkey
        put.add(Bytes.toBytes("cf"), Bytes.toBytes("times"), Bytes.toBytes(vals[0]));
        put.add(Bytes.toBytes("cf"), Bytes.toBytes("lsn"), Bytes.toBytes(vals[1]));
        put.add(Bytes.toBytes("cf"), Bytes.toBytes("lst"), Bytes.toBytes(vals[2]));
        if (vals.length > 3) {
            put.add(Bytes.toBytes("cf"), Bytes.toBytes("s"), Bytes.toBytes(vals[3]));
            ;
        }
        puts.add(put);
    }

    public static void main(String[] args) {
        if (args == null || args.length < 3) {
            System.out.println("参数个数有误，程序退出");
            System.exit(0);
        }
        File file = new File(args[0]);
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy/MM/dd");
        long startDate = format.parseDateTime(args[1]).getMillis() / 1000;
        long endDate = format.parseDateTime(args[2]).getMillis() / 1000;
        long startTime = System.currentTimeMillis();
        if (args.length == 4 && "true".equals(args[3]))
            macHandler(file, startDate, endDate);
        else
            certificationHandler(file, startDate, endDate);
        long endTime = System.currentTimeMillis();
        System.out.println("the lapsed time is " + (endTime - startTime) / 1000 + "s");
    }
}
