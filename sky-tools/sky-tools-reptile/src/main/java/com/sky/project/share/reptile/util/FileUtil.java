package com.sky.project.share.reptile.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.sky.project.share.common.thread.Threads;
import com.sky.project.share.common.util.Closeables;

/**
 * 文件读取工具类
 * 
 * @author zealot
 *
 */
public final class FileUtil {

	private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

	private static final AtomicLong counts = new AtomicLong(0);

	public static final Charset UTF_8 = StandardCharsets.UTF_8;
	public static final String NEW_LINE = "\n";
	public static final int BASH_SIZE = 2000;

	/**
	 * 处理单个文件，读取内容到阻塞队列中,阻塞队列数据量到达 counts 数量时，休息 sleep ms(用于降低cpu)
	 * 
	 * @param queue
	 * @param file
	 * @param sleep
	 * @param counts
	 * @throws Exception
	 */
	public static void read(BlockingQueue<String> queue, File file, long sleep, int counts) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			long index = 0;

			while ((line = reader.readLine()) != null) {
				queue.put(line);
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

	/**
	 * 根据配置的源文件目录和文件后缀扫描目标文件并返回
	 * 
	 * @param src
	 * @param suffix
	 * @return
	 */
	public static List<File> getSourceFiles(String src, String suffix) {
		File file = new File(src);
		if (file.exists()) {
			if (file.isDirectory()) {
				return Arrays.asList(file.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.getName().endsWith(suffix);
					}
				}));
			} else {
				if (src.endsWith(suffix)) {
					return Arrays.asList(new File(src));
				}
			}
		}
		LOG.error("error src file or dir path");
		return Arrays.asList();
	}

	/**
	 * 根据配置的源文件目录和文件后缀扫描目标文件并返回
	 * 
	 * @param src
	 * @return
	 */
	public static List<File> getSourceFiles(String src) {
		File file = new File(src);
		if (file.exists()) {
			if (file.isDirectory())
				return Arrays.asList(file.listFiles());
			else
				return Arrays.asList(file);
		} else {
			LOG.error("error src file or dir path");
			return Arrays.asList();
		}
	}

	/**
	 * 文件追加写入
	 * 
	 * @param file
	 * @param lines
	 */
	public static void append(File file, List<String> lines) {
		BufferedWriter writer = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file, true);
			writer = new BufferedWriter(new OutputStreamWriter(fos, UTF_8));

			for (String line : lines) {
				writer.write(line);
				writer.write(NEW_LINE);
			}
		} catch (IOException e) {
			LOG.error("append into file with line error.", e);
		} finally {
			Closeables.close(writer, fos);
		}
	}

	public static void append(String path, List<String> lines) {
		BufferedWriter writer = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(path, true);
			writer = new BufferedWriter(new OutputStreamWriter(fos, UTF_8));

			for (String line : lines) {
				writer.write(line);
				writer.write(NEW_LINE);
			}
		} catch (IOException e) {
			LOG.error("append into file with line error.", e);
		} finally {
			Closeables.close(writer, fos);
		}
	}

	/**
	 * 将多行合并为一行
	 * 
	 * @param src
	 * @param dst
	 * @param everyLineCounts
	 * @param spliter
	 * @throws Exception
	 */
	public static void read(File src, File dst, int everyLineCounts, char spliter) {
		BufferedReader reader = null;
		FileInputStream in = null;

		try {
			in = new FileInputStream(src);
			reader = new BufferedReader(new InputStreamReader(in, FileUtil.UTF_8));

			long lineCounts = 0; // 行数
			int filedCounts = 0; // 字段数

			List<String> lines = new ArrayList<>(BASH_SIZE);
			StringBuffer buffer = new StringBuffer();

			String line = null;
			while ((line = reader.readLine()) != null) {
				lineCounts++;
				buffer.append(line.replace("" + spliter, " ").trim()); // 替换所有的分隔符为空格
				filedCounts++;

				if (filedCounts == everyLineCounts) {// 批量行的最后一行
					filedCounts = 0;
					lines.add(buffer.toString());

					if (lines.size() >= BASH_SIZE) { // 批量行，追加写入目标文件
						FileUtil.append(dst, lines);
						lines.clear();
					}

					buffer = new StringBuffer();
				} else {
					buffer.append(spliter);
				}

				if (lineCounts % 2000 == 0) {
					Thread.sleep(200);
				}
			}

			if (lines.size() > 0) {
				FileUtil.append(dst, lines);
			}

			System.out.println("finish deal file, all lines : " + lineCounts);
		} catch (Exception e) {
			LOG.error("read file contents line by line error.", e);
		} finally {
			Closeables.close(reader, in);
		}
	}

	public static String suffix(String src) {
		int index = src.indexOf('.');
		return index == -1 ? ".txt" : src.substring(index);
	}

	public static String trim(String src) {
		return src.replaceAll("\"", "").trim();
	}

	public static String trim2Null(String src) {
		if (src == null) {
			return "null";
		}
		String newStr = src.replace("&nbsp", "").replace(";", "").replace("\t", "").replace("\n", "").trim();
		return newStr.isEmpty() || "null".equalsIgnoreCase(newStr) || "mull".equalsIgnoreCase(newStr) ? "null" : newStr;
	}

	public static String trim2Empty(String src) {
		if (src == null) {
			return "";
		}
		String newStr = src.trim();
		return newStr.isEmpty() || "null".equalsIgnoreCase(newStr) || "mull".equalsIgnoreCase(newStr) ? "" : newStr;
	}

	public static boolean isBlank(String src) {
		return src == null || src.isEmpty() || "null".equalsIgnoreCase(src) || "mull".equalsIgnoreCase(src);
	}

	public static String blank2NULL(String src) {
		return (src == null || src.isEmpty() || "null".equalsIgnoreCase(src) || "mull".equalsIgnoreCase(src)) ? null
				: src;
	}

	/**
	 * 向文件中写入 json 数据及 .ok 文件，并清除元数据
	 * 
	 * @param path
	 * @param json
	 */
	public static <T> void writeWithJson(String path, List<T> datas) {
		if (datas == null || datas.isEmpty()) {
			return;
		}

		int len = datas.size();
		String json = new Gson().toJson(datas);
		datas.clear();

		BufferedWriter writer = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(new File(path));
			writer = new BufferedWriter(new OutputStreamWriter(fos, UTF_8));
			writer.write(json);

			new File(path + ".ok").createNewFile();

			LOG.info("json writer size:{}, all total counts:{}", len, counts.addAndGet(len));
			json = null;
		} catch (Exception e) {
			LOG.error("write json into file or create ok file error.", e);
		} finally {
			Closeables.close(writer, fos);
		}
	}

	/**
	 * 随机生成三位数，不够0补齐
	 * 
	 * @return
	 */
	public static String random() {
		int i = new Random().nextInt(1000);
		if (i < 10) {
			return "00" + i;
		} else if (i < 100) {
			return "0" + i;
		}

		return "" + i;
	}

	public static String getSp(String spliter) {
		switch (spliter) {
		case "|":
			return "\\|";
		case "\\t":
			return "\t";
		}
		return spliter;
	}

	public static String pathWithSuffix(String str) {
		char ch = str.charAt(str.length() - 1);
		return ch == '/' || ch == '\\' ? str : str + "/";
	}

	/**
	 * 文件名追加后缀
	 * 
	 * @param path
	 *            路径名称
	 * @param suffix
	 *            后缀
	 * @return
	 */
	public static String fileNameAppendSuffix(String path, String suffix) {
		int index = path.lastIndexOf('.');
		return path.substring(0, index) + suffix + path.substring(index);
	}

	public static String date2Path(String prefix, String startDate, String endDate) {
		return prefix + startDate.replace("-", "") + "_" + endDate.replace("-", "") + ".txt";
	}

	private FileUtil() {
	}
}
