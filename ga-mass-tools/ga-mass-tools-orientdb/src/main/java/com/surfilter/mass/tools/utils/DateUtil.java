package com.surfilter.mass.tools.utils;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author liuchen
 * @author hapuer
 * 
 */
public class DateUtil {

	private static Logger LOG = LoggerFactory.getLogger(DateUtil.class);
	
	private static final DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMddHHmmss");
	private static final DateTimeFormatter format1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTime dateTime = new DateTime();
	private static long baseDateSecond = 0l;
	public static String defaultDate = "19700101080000";

	static {
		// 1970-01-01 08-00-00
		baseDateSecond = new Date(0L).getTime() / 1000;
	}
	public static int getSeconds(){
		DateTime dt = new DateTime();
		DateTime otherTime = dt.withTime(23,59,59,999);
		int diff = otherTime.getSecondOfDay() - dt.getSecondOfDay();
		return diff;
	}
	public static Long getDiffBaseDate(Date date) {
		long diffSeconds = date.getTime() / 1000 - baseDateSecond;
		return diffSeconds;
	}
	public static String  getCurrentDateStr() {
		return new DateTime().toString("yyyy-MM-dd HH:mm:ss");
	}

	// 将SimpleDateFormat修改为JodaTime
	public static String changeDiffSecondToDate(long seconds) {
		return dateTime.withMillis(seconds * 1000).toString(format);
	}

	public static String getUnixTimeStr(long seconds) {
		try {
			return dateTime.withMillis(seconds * 1000).toString(format1);
		} catch (Exception e) {
			LOG.error("转换long类型日期为字符串型，失败！");
		}
		return null;
	}
	
	public static String str2UnixString(String source) {
		return String.valueOf(str2UnixTime(source));
	}

	public static Long str2UnixTime(String source) {
		return DateTime.parse(source, format1).getMillis()/1000;
	}
	
	public static Long getUnixTime(Date date) {
		return date.getTime() / 1000;
	}

	// format unix long time to date string
	public static String formatUnixlt2Str(long unixTimeMillis, String format) {
		return dateTime.withMillis(unixTimeMillis * 1000).toString(DateTimeFormat.forPattern(format));
	}

	/**
	 * 获取unix时间的小时及分钟，并补0
	 * 
	 * @param unixTime
	 * @return
	 */
	public static String timeOf(String unixTime) {
		try {
			DateTime d = dateTime.withMillis(Long.parseLong(unixTime) * 1000);
			int hours = d.get(DateTimeFieldType.hourOfDay());
			int mintes = d.get(DateTimeFieldType.minuteOfHour());
			mintes = (mintes / 5) * 5;
			StringBuilder builder = new StringBuilder();
			if (hours < 10) {
				builder.append("0");
			}
			builder.append(hours);
			
			if(mintes < 10) {
				builder.append("0");
			}
			builder.append(mintes);
			
			return builder.toString();
		} catch(Exception e) {
			return "0000";
		}
	}
	
	/**
	 * 获取unix时间的天，并补0
	 * 
	 * @param unixTime
	 * @return
	 */
	public static String dayOf(String unixTime) {
		try {
			DateTime d = dateTime.withMillis(Long.parseLong(unixTime) * 1000);
			int days = d.get(DateTimeFieldType.dayOfMonth());
			StringBuilder builder = new StringBuilder();
			if (days < 10) {
				builder.append("0");
			}
			builder.append(days);
			
			return builder.toString();
		} catch(Exception e) {
			return "01";
		}
	}
	
	public static String currentDateStr() {
		return DateTime.now().toString("yyyyMMdd");
	}
	
	private DateUtil() {
	}
	
	public static String getBeforeDay(String curDate, int days){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		
		try {
			cal.setTime(sdf.parse(curDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cal.add(Calendar.DATE, days);
				
		return sdf.format(cal.getTime());
	}
	
	/** 
	*字符串的日期格式的计算 
	*/  
    public static int daysBetween(String smdate,String bdate) {  
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");  
        Calendar cal = Calendar.getInstance();   
        
        try {
			cal.setTime(sdf.parse(smdate));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
        
        long time1 = cal.getTimeInMillis();                 
        try {
			cal.setTime(sdf.parse(bdate));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));     
    }  
}
