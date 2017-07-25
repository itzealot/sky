package com.sky.project.share.streaming;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * 使用 spark-submit 提交Spark任务.<br>
 * 使用 --conf 传递对应的参数.<br>
 * --conf spark.istest=true
 * 
 * @author zealot
 *
 */
public class StreamProcessUsingSparkSubmitDemo {

	public static void main(String[] args) {
		SparkConf conf = new SparkConf();

		// 注册
		conf.registerKryoClasses(new Class[] { String.class, String[].class });

		// 设置相关信息
		if (conf.getBoolean("spark.istest", true)) {
			conf.setAppName("AppName");
			conf.setMaster("local");
		}

		JavaSparkContext sc = new JavaSparkContext(conf);

		try {
			doRun(sc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}

	}

	private static void doRun(JavaSparkContext sc) {

	}

}
