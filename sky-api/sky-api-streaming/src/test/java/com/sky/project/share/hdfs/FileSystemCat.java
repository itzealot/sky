package com.sky.project.share.hdfs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

/**
 * 使用 FileSystem 读取hadoop的文件内容 <br>
 * static 方法:<br>
 * FileSystem get(Configuration conf);<br>
 * FileSystem get(URI, uri, Configuration conf);<br>
 * FileSystem get(URI, uri, Configuration conf, String user); 指定用户来访问文件系统<br>
 * Configuration 对象封装了客户端或服务器的配置，通过设置配置文件读取路径来实现(如 conf/core-site.xml)
 * 
 * LocalFileSystem getLocal(Configuration conf);获取本地文件系统的运行实例<br>
 * 
 * 成员方法:<br>
 * FSDataInputStream open(Path f);获取文件的输入流，默认大小 4KB<br>
 * FSDataInputStream open(Path f, int bufferSize);获取文件的输入流<br>
 * boolean mkdirs(Path f);创建文件目录<br>
 * 
 * 使用hadoop命令输出文件内容:<br>
 * hadoop FileSystemCat hdfs://filePath
 * 
 * @author zealot
 */
public class FileSystemCat {

	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration();
		InputStream in = null;

		try {
			FileSystem fs = FileSystem.get(URI.create(args[0]), conf);
			in = fs.open(new Path(args[0]));
			IOUtils.copyBytes(in, System.out, 4 * 1024, false);
		} finally {
			IOUtils.closeStream(in);
		}
	}

	/**
	 * FSDataInputStream支持文件的随机访问，可以从流的任意位置读取数据 <br>
	 * {@link org.apache.hadoop.fs.Seekable}
	 * 
	 * @param hdfsPath
	 * @param conf
	 * @throws IOException
	 */
	static void read(String hdfsPath, Configuration conf) throws IOException {
		FSDataInputStream in = null;

		try {
			FileSystem fs = FileSystem.get(URI.create(hdfsPath), conf);
			in = fs.open(new Path(hdfsPath));
			IOUtils.copyBytes(in, System.out, 4 * 1024, false);

			in.seek(0); // go back to the start of the file

			// 打印到控制台，所以不能关闭输出流
			IOUtils.copyBytes(in, System.out, 4 * 1024, false);
		} finally {
			IOUtils.closeStream(in);
		}
	}

	/**
	 * 使用hadoop命令进行文件copy:<br>
	 * hadoop FileCopyWithProgress hdfs://fromFilePath hdfs://toFilePath
	 * 
	 * @param from
	 * @param to
	 * @param conf
	 * @throws IOException
	 */
	static void copy(String from, String to, Configuration conf) throws IOException {
		// 创建输入流
		InputStream in = new BufferedInputStream(new FileInputStream(from));

		// 获取文件系统实现类 FileSystem
		FileSystem fs = FileSystem.get(URI.create(to), conf);

		// 创建输出流，此处根据输出打印进度
		OutputStream out = fs.create(new Path(to), new Progressable() {
			@Override
			public void progress() {
				System.out.println(".");
			}
		});

		// copy 完成后关闭
		IOUtils.copyBytes(in, out, 4 * 1024, true);
	}
}
