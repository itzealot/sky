package com.sky.project.share.hdfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;

/**
 * 使用 hadoop 命令读取hdfs上相应的文件:<br>
 * hadoop URLCat hdfs://filePath
 * 
 * @author zealot
 */
public class URLCat {

	static {
		/**
		 * 1.让java程序能够识别Hadoop的hdfs URL方案，此处使用 FsUrlStreamHandlerFactory 实例设置给
		 * URL <br>
		 * 2.每个Java虚拟机只能调用一次该方法，因此通常在静态方法中调用 <br>
		 * 3.如果程序中其他组件已经设置对应的 URLStreamHandlerFactory，则无法使用该方法从 Hadoop 中读取数据
		 */
		URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
	}

	/**
	 * @param hdfsPath
	 *            hdfs://filePath
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static void read(String hdfsPath) throws MalformedURLException, IOException {
		InputStream in = null;

		try {
			in = new URL(hdfsPath).openStream();
			IOUtils.copyBytes(in, System.out, 4 * 1024, false);
		} finally {
			IOUtils.closeStream(in);
		}
	}

	public static void main(String[] args) {
		try {
			read(args[0]);
		} catch (MalformedURLException e) {
			// TODO
			e.printStackTrace();
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
	}
}
