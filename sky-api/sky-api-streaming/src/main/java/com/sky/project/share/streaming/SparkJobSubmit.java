package com.sky.project.share.streaming;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;

import com.sky.project.share.streaming.conf.MassConfiguration;

/**
 * Submit Spark Job(Driver)
 * 
 * @author zealot
 *
 */
public class SparkJobSubmit {

	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			System.out.println("args[" + i + "] = " + args[i]);
		}

		// main jar
		String jarName = args[1];
		// 依赖的jar
		String addJars = args[2].replace(":", ",file://").substring(1);

		MassConfiguration configuration = new MassConfiguration();

		// streaming main class
		String streamClass = configuration.get("ga.streaming.main.class", "com.ga.projects.spark.SparkJobMain");

		String coreSite = configuration.get("ga.core_site");
		String hdfsSite = configuration.get("ga.hdfs_site");
		String yarnSite = configuration.get("ga.yarn_site");

		// kafka url 9092
		String brokers = configuration.getStrings("ga.brokers");

		// url 2181
		String zkUrl = configuration.getStrings("ga.zookeeper.url");

		// topic
		String topic = configuration.getStrings("ga.topic");
		String group = configuration.getStrings("ga.topic.group");
		String kafkaMode = configuration.getStrings("ga.kafka.mode");

		String name = configuration.get("ga.app.name");

		String driverMemory = configuration.get("ga.driver-memory");
		String numExecutors = configuration.get("ga.num-executors");
		String executorMemory = configuration.get("ga.executor-memory");
		String executorCores = configuration.get("ga.executor-cores");
		String driverJavaOptions = configuration.get("ga.driver-java-options");
		String executorJavaOptions = configuration.get("ga.executor-java-options");
		String maxRatePerPartition = configuration.get("ga.maxRatePerPartition");
		String jobDuration = configuration.get("ga.jobDuration");

		System.out.println("jarName: " + jarName);
		System.out.println("streamClass: " + streamClass);
		System.out.println("addJars: " + addJars);
		System.out.println("coreSite: " + coreSite);
		System.out.println("hdfsSite: " + hdfsSite);
		System.out.println("yarnSite: " + yarnSite);

		System.out.println("brokers: " + brokers);
		System.out.println("zkUrl: " + zkUrl);
		System.out.println("topic: " + topic);
		System.out.println("group: " + group);
		System.out.println("kafkaMode: " + kafkaMode);

		System.out.println("driverMemory: " + driverMemory);
		System.out.println("numExecutors: " + numExecutors);
		System.out.println("executorMemory: " + executorMemory);
		System.out.println("executorCores: " + executorCores);
		System.out.println("driverJavaOptions: " + driverJavaOptions);
		System.out.println("executorJavaOptions: " + executorJavaOptions);
		System.out.println("maxRatePerPartition: " + maxRatePerPartition);
		System.out.println("jobDuration: " + jobDuration);

		// org.apache.spark.deploy.yarn.Client object
		String[] clientArguments = new String[] {
				// the name of your application
				"--name", name,
				// memory for driver (optional)
				"--driver-memory", driverMemory,

				"--num-executors", numExecutors,

				"--executor-memory", executorMemory,

				"--executor-cores", executorCores,

				// path to your application's JAR file
				// required in yarn-cluster mode
				"--jar", jarName, // main jar

				// name of your application's main class (required)
				"--class", streamClass,

				// comma separated list of local jars that want
				// SparkContext.addJar to work with
				"--addJars", addJars,

				// yarn mode
				"--arg", "yarn-cluster",

				// broker list
				"--arg", brokers,

				// zk url
				"--arg", zkUrl,

				"--arg", topic,

				"--arg", group,

				"--arg", kafkaMode,

				"--arg", maxRatePerPartition,

				"--arg", jobDuration,

		};

		// create a Hadoop Configuration object
		Configuration config = new Configuration();

		config.addResource(new Path(coreSite));
		config.addResource(new Path(hdfsSite));
		config.addResource(new Path(yarnSite));

		System.out.println("yarn.resourcemanager.address: " + config.get("yarn.resourcemanager.address"));

		// identify that you will be using Spark as YARN mode
		System.setProperty("SPARK_YARN_MODE", "true");

		SparkConf sparkConf = new SparkConf();
		sparkConf.set("spark.executor.instances", numExecutors);

		String sparkLogFile = "file:///etc/spark/conf/log4j.properties";
		sparkConf.set("spark.executor.extraJavaOptions",
				"-Dlog4j.configuration=" + sparkLogFile + " " + executorJavaOptions);
		sparkConf.set("spark.driver.extraJavaOptions",
				"-Dlog4j.configuration=" + sparkLogFile + " " + driverJavaOptions);

		ClientArguments cArgs = new ClientArguments(clientArguments, sparkConf);
		Client client = new Client(cArgs, config, sparkConf);
		ApplicationId appid = client.submitApplication();

		System.out.println("Spark YARN ApplicationId:" + appid.toString());
	}

}
