package com.sky.project.share.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import com.sky.project.share.socket.SocketConst;
import com.sky.project.share.socket.util.SimpleMessageUtil;

public class SocketChannelMain {

	private static final Charset UTF_8 = SimpleMessageUtil.UTF_8;

	public static void main(String[] args) {
		connectNotBlocked(SocketConst.SERVER_HOST, SocketConst.SERVER_PORT);
	}

	public static void connectBlocked(String host, int port) {
		try {
			SocketAddress address = new InetSocketAddress(host, port);
			// 以阻塞方式打开通道，阻塞操作，后续代码在真正建立连接前不会执行；如果连接无法建立，则抛出异常
			SocketChannel clientChannel = SocketChannel.open(address);

			// 根据输入/输出流创建通道
			WritableByteChannel out = Channels.newChannel(System.out);

			ByteBuffer buffer = ByteBuffer.allocate(64);

			/**
			 * 1.读写时复用同一个Buffer，提高性能 <br>
			 * 2.通道每次读取返回读取的字节数，返回-1代表结束 <br>
			 * 3.如果将客户端设置为非阻塞模式，没有字节可用时会立即返回0；如果是阻塞模式，则会阻塞 <br>
			 */
			while (clientChannel.read(buffer) != -1) {
				try {
					buffer.flip(); // 用于后续缓冲区的读
					out.write(buffer);
				} finally { // 清空缓冲区
					buffer.clear();
				}
			}
		} catch (IOException e) {
			e.printStackTrace(); // TODO
		}
	}

	public static void connectNotBlocked(String host, int port) {
		try {
			SocketAddress address = new InetSocketAddress(host, port);
			SocketChannel channel = SocketChannel.open(address);

			// 设置为非阻塞
			channel.configureBlocking(false);

			// 根据输入/输出流创建通道
			WritableByteChannel out = Channels.newChannel(System.out);

			// 创建缓冲区用于从通道中读取数据
			ByteBuffer buffer = ByteBuffer.allocate(74);

			/**
			 * 1.读写时复用同一个Buffer，提高性能 <br>
			 * 2.通道每次读取返回读取的字节数，返回-1代表结束 <br>
			 * 3.如果将客户端设置为非阻塞模式，没有字节可用时会立即返回0；如果是阻塞模式，则会阻塞 <br>
			 */
			while (true) {
				int size = channel.read(buffer);

				if (size > 0) { // 有相应的数据
					try {
						buffer.flip(); // 用于后续缓冲区的读
						out.write(buffer);
					} finally { // 清空缓冲区
						buffer.clear();
					}
				} else if (size == -1) { // 此处不当发生，除非服务器故障
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace(); // TODO
		}
	}
}
