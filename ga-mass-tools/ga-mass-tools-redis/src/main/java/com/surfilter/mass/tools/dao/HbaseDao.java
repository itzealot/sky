package com.surfilter.mass.tools.dao;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by Administrator on 2016/12/2.
 */
public class HbaseDao {
    private static Configuration config = HBaseConfiguration.create();
    private static HConnection connection;
    public static HTableInterface table = null;
    private static Logger LOG = LoggerFactory.getLogger(HbaseDao.class);
    public static final byte[] COLUMN_FAMILY = "cf".getBytes();
    public static final byte[] COLUMN_FIRST_START_TIME = "first_start_time".getBytes();

    static {
        Properties prop = new Properties();
        String propertyPath = System.getProperty("user.dir") + "/conf/config.properties";
        try {
            prop.load(new FileInputStream(propertyPath));
            String zkConn = prop.getProperty("hbase.zookeeper.quorum", "localhost");
            String zkPort = prop.getProperty("hbase.zookeeper.property.clientPort", "2181");
            String tableName = prop.getProperty("hbase.table.name", "mac");

            config.set("hbase.zookeeper.quorum", zkConn);
            config.set("hbase.zookeeper.property.clientPort", zkPort);
//          connection = ConnectionFactory.createConnection(config);
            connection = HConnectionManager.createConnection(config);
            table = connection.getTable(TableName.valueOf(tableName));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void commitPuts(List<Put> puts){
        try {
            table.put(puts);
            table.flushCommits();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Map<String, String[]> get(List<String> rowKeys){
        List<Get> list = new ArrayList<Get>();
        for(String rowKey : rowKeys){
            Put put = new Put(rowKey.getBytes());
            /*put.addColumn(COLUMN_FAMILY,"first_start_time1".getBytes());
            table.checkAndPut(rowKey.getBytes(),COLUMN_FAMILY,COLUMN_FIRST_START_TIME,null,put);*/
        }
        try {
            table.get(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void mutateData(Map<String,String[]> map) {
        List<Put> list = new ArrayList<Put>();
        for(String rowKey : map.keySet()){
            String[] valueEntry =  map.get(rowKey);
            Put put = new Put(rowKey.getBytes());
            put.addColumn("cf".getBytes(),"first_start_time1".getBytes(),valueEntry[0].getBytes());
            put.addColumn("cf".getBytes(),"first_terminal_num1".getBytes(),valueEntry[1].getBytes());
            list.add(put);
        }
        Object[] object = new Object[]{};
        try {
            table.batch(list,object);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("the succeed records' num is "+object.length);
        }
    }

}
