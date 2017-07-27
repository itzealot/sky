package com.sky.project.share.hdfs;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * FileStatus 类封装了文件系统中文件和目录的元数据，包括文件长度、块大小、副本、修改时间、所有者以及权限信息
 * 
 * @author zealot
 */
public class FileStatusUse {
	private FileSystem fs;
	Configuration conf = new Configuration();

	@Before
	public void before() throws IOException {
		OutputStream out = fs.create(new Path("/tmp/test"));
		out.write("HelloWorld".getBytes("UTF-8"));
		out.close();
	}

	@Test
	public void testFileStatus() throws IOException {
		FileStatus status = fs.getFileStatus(new Path("/tmp/test.txt"));
		status.isDirectory();
		status.getLen();
		status.getModificationTime();
		status.getBlockSize();
		status.getGroup();
		status.getReplication();
		status.getOwner();
		status.getPermission();
	}

	@After
	public void after() throws IOException {
		if (fs != null) {
			fs.close();
		}
	}
}
