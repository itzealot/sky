package com.surfilter.mass.tools.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wellben on 2016/9/26.
 */
public interface Constants {

	String OJDBC_URL = "jdbc:orient:remote:10.10.10.104/gacenter";
//    String OJDBC_URL = "jdbc:orient:remote:192.168.0.112/weng";
//    String OJDBC_URL = "jdbc:orient:remote:10.10.10.77/demo1";
//    String OCONN = "remote:10.10.10.77:2424;10.10.10.78:2424;10.10.10.79:2424/demo1";
	
	
    String OCONN = "remote:192.168.0.112:2424/gacenter";
//    String OCONN = "remote:10.10.10.104:2424;10.10.10.105:2424;10.10.10.106:2424/gacenter";
//    String OCONN = "remote:10.10.10.104:2424/gacenter";
    
    //积分等级:1-10级
    String SCORE_LEVEL_1 = "s1";
    String SCORE_LEVEL_2 = "s2";
    String SCORE_LEVEL_3 = "s3";
    String SCORE_LEVEL_4 = "s4";
    String SCORE_LEVEL_5 = "s5";
    String SCORE_LEVEL_6 = "s6";
    String SCORE_LEVEL_7 = "s7";
    String SCORE_LEVEL_8 = "s8";
    String SCORE_LEVEL_9 = "s9";
    String SCORE_LEVEL_10 = "s10";
    
    
    
    
    //来源小类：
    public static final String SOURCE_KK = "9998";// 网吧开卡日志标识
    public static final String SOURCE_HD = "998";//硬件特征
	public static final String SOURCE_RT = "997";//第三方数据标识,从web通过excel导入	
	public static final String SOURCE_ZF = "1002"; //餐饮
	public static final String SOURCE_CY = "1003"; //餐饮
	public static final String SOURCE_YYS = "1004"; //运营商
	public static final String SOURCE_WZ = "1005"; //网站社区
	public static final String SOURCE_TV = "1006"; //数字电视
	public static final String SOURCE_TEL = "1007"; //网络电话
	public static final String SOURCE_SHOPPING = "1008"; //网络购物
	public static final String SOURCE_TAXI = "1009"; //网络约租车
	public static final String SOURCE_DZ = "1010"; //网络短租
	public static final String SOURCE_MAP = "1011"; //地图应用
	public static final String SOURCE_KD = "1012"; //宽带注册
	public static final String SOURCE_SD = "1013"; //涉毒人员
	public static final String SOURCE_ZDR = "1014"; //重点人员
	public static final String SOURCE_OTHER = "9999"; //其它	
	public static final String SOURCE_G2F = "20";//群和好友日志标识
	public static final String SOURCE_SC = "16";// 视频车辆数据
	public static final String SOURCE_SF = "15";// 身份关系数据
	public static final String SOURCE_SF_2 = "1015";// 身份关系数据
	public static final String SOURCE_ZT = "14";// 手机特征数据
	public static final String SOUCER_AT = "13";// 采集认证轨迹日志标识
	public static final String SOUCER_DT = "12";// 采集设备移动轨迹日志标识		
	public static final String SOUCER_CC = "6";// 普通内容日志标识
	public static final String SOUCER_SJ = "5";// 上下机日志标识(终端上下线日志)
	public static final String SOUCER_XW = "4";// 上网日志即上网行为日志
	public static final String SOUCER_IM = "3";// 即时通讯日志标识
	public static final String SOUCER_FJ = "2";// 非经轨迹日志标识
	public static final String SOUCER_WL = "1";// 围栏日志标识
	
	//厂商
	public static final String COMPANY_RZX = "723005104"; //RZX
	public static final String COMPANY_NMWL = "567051052"; //柠檬网联
	public static final String COMPANY_MYD = "749123029"; //迈外迪
	public static final String COMPANY_YSF = "675061063"; //韵盛发
	public static final String COMPANY_RZXGW = "696027155"; //任子行（GW）
	public static final String COMPANY_YY = "359422736"; //云盈
	public static final String COMPANY_BSD = "573571572"; //博施盾
	public static final String COMPANY_HC = "66240918X"; //寰创
	public static final String COMPANY_HS = "085734125"; //华视	
	public static final String COMPANY_XRK = "765077704"; //向日葵
	
	//协议类型
	public static final String PROTOCOL_TYPE_NAME = "1021902";		//姓名
	public static final String PROTOCOL_TYPE_PHONE = "1020004";		//手机
	public static final String PROTOCOL_TYPE_MAC = "1020002";		//MAC
	public static final String PROTOCOL_TYPE_IMEI = "1021901";		//IMEI
	public static final String PROTOCOL_TYPE_IMSI = "1020003";		//IMSI
	public static final String PROTOCOL_TYPE_QQ = "1030001";		//QQ
	public static final String PROTOCOL_TYPE_WX = "1030036";		//微信
	public static final String PROTOCOL_TYPE_CERT = "1021111";		//身份证
	
	
	
    

    Properties OJDBC_POOL_INFO = new Properties() {{
        put("user", "admin");
        put("password", "admin");

        put("db.usePool", "true"); // USE THE POOL
        put("db.pool.min", "100");   // MINIMUM POOL SIZE
        put("db.pool.max", "1000");  // MAXIMUM POOL SIZE
    }};

    Map<String, String> SPARK_OJDBC_INFO = new HashMap<String, String>() {{
        put("url", OJDBC_URL);
        put("user", "admin");
        put("password", "admin");

        put("spark", "true");
        put("dbtable", "V");
    }};
    
    //来源小类与等级对照表，KEY是来源，VALUE是等级
    Map<String, String> SOURCE_LEVEL_MAP = new HashMap<String, String>() {{
    	//1级        
        put(SOUCER_FJ, SCORE_LEVEL_1);        
        put(SOUCER_XW, SCORE_LEVEL_1);
        put(SOUCER_SJ, SCORE_LEVEL_1);
        put(SOURCE_SD, SCORE_LEVEL_1);
        put(SOURCE_ZDR, SCORE_LEVEL_1);        
        
        //2级
        put(SOUCER_WL, SCORE_LEVEL_2);
        put(SOUCER_DT, SCORE_LEVEL_2);
        put(SOUCER_AT, SCORE_LEVEL_2);
        put(SOURCE_G2F, SCORE_LEVEL_2);
        
        //3级
        put(SOUCER_IM, SCORE_LEVEL_3);
        put(SOUCER_CC, SCORE_LEVEL_3);        
        
        //4级
        put(SOURCE_SF_2, SCORE_LEVEL_4);
        put(SOURCE_SF, SCORE_LEVEL_4);
        put(SOURCE_SC, SCORE_LEVEL_4);        
        
        //5级
        put(SOURCE_KK, SCORE_LEVEL_5);
        put(SOURCE_HD, SCORE_LEVEL_5);
        put(SOURCE_RT, SCORE_LEVEL_5);
        
        //6级
        put(SOURCE_ZF, SCORE_LEVEL_6);
        put(SOURCE_CY, SCORE_LEVEL_6);
        put(SOURCE_YYS, SCORE_LEVEL_6);
        put(SOURCE_KD, SCORE_LEVEL_6);
        
        //7级
        put(SOURCE_WZ, SCORE_LEVEL_7);
        put(SOURCE_TV, SCORE_LEVEL_7);
        put(SOURCE_TEL, SCORE_LEVEL_7);
        
        //8级
        put(SOURCE_SHOPPING, SCORE_LEVEL_8);
        put(SOURCE_TAXI, SCORE_LEVEL_8);
        
        //9级
        put(SOURCE_DZ, SCORE_LEVEL_9);
        put(SOURCE_MAP, SCORE_LEVEL_9);
        
        //10级
        put(SOURCE_OTHER, SCORE_LEVEL_10); 
    }};
    
    
    //厂商与等级对照表，KEY是厂商，VALUE是等级
    Map<String, String> COMPANYID_LEVEL_MAP = new HashMap<String, String>() {{
    	//1级
        put(COMPANY_RZX, SCORE_LEVEL_1);        
        //2级
        put(COMPANY_RZXGW, SCORE_LEVEL_2);        
        //3级
        put(COMPANY_MYD, SCORE_LEVEL_3);
        put(COMPANY_NMWL, SCORE_LEVEL_3);        
    	//4级
        put(COMPANY_YSF, SCORE_LEVEL_4);        
        //5级
        put(COMPANY_BSD, SCORE_LEVEL_5);        
        //6级
        put(COMPANY_YY, SCORE_LEVEL_6); 
    	//7级
        put(COMPANY_HC, SCORE_LEVEL_7);        
        //8级
        put(COMPANY_HS, SCORE_LEVEL_8);        
        //9级
        put(COMPANY_XRK, SCORE_LEVEL_9); 
    	//其它不在范围内的都归为10级
    }};
    
    //等级发现次数与权重对应表，KEY是等级对应发现次数，VALUE是权重
    Map<String, Double> LEVEL_FOUNDCOUNT_WEIGHT_MAP = new HashMap<String, Double>() {{
    	put(SCORE_LEVEL_1, 0.3);
    	put(SCORE_LEVEL_2, 0.25);
    	put(SCORE_LEVEL_3, 0.1);
    	put(SCORE_LEVEL_4, 0.05);
    	put(SCORE_LEVEL_5, 0.05);
    	put(SCORE_LEVEL_6, 0.05);
    	put(SCORE_LEVEL_7, 0.05);
    	put(SCORE_LEVEL_8, 0.05);
    	put(SCORE_LEVEL_9, 0.05);
    	put(SCORE_LEVEL_10, 0.05); 
    }};  
    
    //等级分值对应表，KEY是等级，VALUE是分值
    Map<String, Integer> LEVEL_WEIGHT_MAP = new HashMap<String, Integer>() {{
    	put(SCORE_LEVEL_1, 100);
    	put(SCORE_LEVEL_2, 90);
    	put(SCORE_LEVEL_3, 80);
    	put(SCORE_LEVEL_4, 70);
    	put(SCORE_LEVEL_5, 60);
    	put(SCORE_LEVEL_6, 50);
    	put(SCORE_LEVEL_7, 40);
    	put(SCORE_LEVEL_8, 30);
    	put(SCORE_LEVEL_9, 20);
    	put(SCORE_LEVEL_10, 10); 
    }};     

}
