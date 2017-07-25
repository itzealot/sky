package com.sky.project.share.streaming.util;

import java.io.Serializable;

import org.apache.spark.SparkConf;

/**
 * spark conf util
 * 
 * @author zealot
 *
 */
@SuppressWarnings("serial")
public final class SparkConfUtil implements Serializable {

	/**
	 * spark memory manager settings for after spark 1.6 version
	 * 
	 * 使用 UnifiedMemoryManager 管理内存
	 * 
	 * @param sparkConf
	 * @param memoryFraction
	 *            默认值为0.75,即useableMemory的75%内存用于ExecutionMemory与StorageMemory共享
	 * @param storageFraction
	 *            默认值为0.5;StorageMemory的占用比例为50%
	 */
	public static SparkConf usingUnifiedMemoryManager(SparkConf sparkConf, String memoryFraction,
			String storageFraction) {
		// 设置内存管理模型，在spark 1.6之后默认值为false，即使用 UnifiedMemoryManager 管理内存
		// ExecutionMemory 和 StorageMemory 会共享usableMemory * 0.75的内存。
		sparkConf.set("spark.memory.uselegacymode", "false");

		// 默认值0.75;useableMemory的75%内存用于ExecutionMemory与StorageMemory
		sparkConf.set("spark.memory.fraction", get(memoryFraction, "0.75"));

		// 默认值0.5;StorageMemory的占用比例
		sparkConf.set("spark.memory.storageFraction", get(storageFraction, "0.5"));

		return sparkConf;
	}

	public static SparkConf usingUnifiedMemoryManager(SparkConf sparkConf) {
		return usingUnifiedMemoryManager(sparkConf, null, null);
	}

	/**
	 * spark memory manager settings for before spark 1.6 version by using
	 * StaticMemoryManager
	 * 
	 * 启用spark 1.6之前的内存分配参数设置，使用 StaticMemoryManager 管理内存
	 * 
	 * @param sparkConf
	 * @param storageMemoryFraction
	 *            分配给rdd缓存的比例，默认为0.6(60%)，如果缓存的数据较少可以降低该值
	 * @param shuffleMemoryFraction
	 *            分配给shuffle数据的内存比例，默认为0.2(20%)
	 */
	public static SparkConf usingStaticMemoryManager(SparkConf sparkConf, String storageMemoryFraction,
			String shuffleMemoryFraction) {
		/**
		 * 设置内存管理模型，在spark 1.6之后默认值为false；
		 * 
		 * 使用spark 1.6之前的内存管理模型，需要设置为true，使用 StaticMemoryManager 管理内存
		 * 设置为false，则使用 UnifiedMemoryManager 管理内存
		 */
		sparkConf.set("spark.memory.uselegacymode", "true");

		// 分配给rdd缓存的比例，默认为0.6(60%)，如果缓存的数据较少可以降低该值
		sparkConf.set("spark.storage.memoryFraction", get(storageMemoryFraction, "0.6"));
		// 分配给shuffle数据的内存比例，默认为0.2(20%)
		sparkConf.set("spark.shuffle.memoryFraction", get(shuffleMemoryFraction, "0.2"));

		return sparkConf;
	}

	public static SparkConf usingStaticMemoryManager(SparkConf sparkConf) {
		return usingStaticMemoryManager(sparkConf, null, null);
	}

	private static String get(String value, String defaultValue) {
		return (value == null || value.isEmpty()) ? defaultValue : value;
	}

	/**
	 * shuffle 优化
	 * 
	 * @param conf
	 * @param shuffleFileBuffer
	 *            default:64K
	 * @param maxSizeInFlight
	 *            default:96m
	 * @param maxRetries
	 *            default:6
	 * @param retryWait
	 *            default:10
	 * @return
	 */
	public static SparkConf shuffleOpt(SparkConf conf, String shuffleFileBuffer, String maxSizeInFlight,
			String maxRetries, String retryWait) {
		/**
		 * 该参数用于设置shuffle write task的 BufferedOutputStream
		 * 的buffer缓冲大小。将数据写到磁盘文件之前，会先写入buffer缓冲中，待缓冲写满之后，才会溢写到磁盘；spark 默认大小:32k
		 */
		conf.set("spark.shuffle.file.buffer", get(shuffleFileBuffer, "128k"));

		/**
		 * 该参数用于设置shuffle read task的buffer缓冲大小，而这个buffer缓冲决定了每次能够拉取多少数据；spark
		 * 默认值：48m
		 */
		conf.set("spark.reducer.maxSizeInFlight", get(maxSizeInFlight, "96m"));

		/**
		 * shuffle read task从shuffle write task
		 * 所在节点拉取属于自己的数据时，如果因为网络异常导致拉取失败，是会自动进行重试的。
		 * 该参数就代表了可以重试的最大次数。如果在指定次数之内拉取还是没有成功，就可能会导致作业执行失败。spark 默认值:3
		 */
		conf.set("spark.shuffle.io.maxRetries", get(maxRetries, "3"));

		/**
		 * 该参数代表了每次重试拉取数据的等待间隔，spark 默认是 5s
		 */
		conf.set("spark.shuffle.io.retryWait", get(retryWait, "5s"));

		return conf;
	}

	public static SparkConf shuffleOpt(SparkConf conf) {
		return shuffleOpt(conf, null, null, null, null);
	}

	private SparkConfUtil() {
	}
}
