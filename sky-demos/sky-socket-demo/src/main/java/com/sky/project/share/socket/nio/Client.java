package com.sky.project.share.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.sky.project.share.socket.SocketConst;
import com.sky.project.share.socket.util.SimpleMessageUtil;

/**
 * {@link java.nio.channels.Selector} : Selector<br>
 * {@link java.nio.channels.Selector#select} : 阻塞至有感兴趣的IO事件发生
 * {@link java.nio.channels.Selector#select(long)} : 阻塞至有感兴趣的IO事件发生，或达到超时时间
 * {@link java.nio.channels.Selector#selectNow} : 直接返回是否有感兴趣的IO事件发生
 * 
 * @author zealot
 */
public class Client {

	private static final Charset UTF_8 = SimpleMessageUtil.UTF_8;

	public static void main(String[] args) {
		Selector selector = null;

		try {
			SocketChannel channel = SocketChannel.open();

			// 设置为非阻塞模式
			channel.configureBlocking(false);

			// 对于非阻塞模式，立刻返回 false，表示连接正在建立中
			channel.connect(new InetSocketAddress(SocketConst.SERVER_HOST, SocketConst.SERVER_PORT));

			selector = Selector.open();

			// 向 Channel 注册Selector 以及感兴趣的连接事件
			channel.register(selector, SelectionKey.OP_CONNECT);
		} catch (IOException e) {
			e.printStackTrace(); // TODO
			return;
		}

		while (true) {
			try {
				// 阻塞至有感兴趣的IO事件发生，或达到超时时间
				// Selector.select(long timeout)
				int nKeys = selector.select();

				if (nKeys > 0) { // 表明有感兴趣的IO事件发生
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> iterator = keys.iterator();

					ByteBuffer buffer = ByteBuffer.allocate(128);

					while (iterator.hasNext()) { // 遍历所有的 SelectionKey
						SelectionKey key = iterator.next();
						iterator.remove(); // 移除事件，防止事件重复

						try {
							if (key.isConnectable()) { // 对于连接事件
								System.out.println("connect event...");
								dealConnectionEvent(selector, key);
							} else if (key.isReadable()) { // 有流可读取
								dealReadEvent(selector, key, buffer);
							} else if (key.isWritable()) { // 可写入流
								System.out.println("write event...");
								dealWriteEvent(selector, key, buffer);
							}
						} catch (IOException e) {
							e.printStackTrace(); // TODO
						}
					}

					keys.clear(); // 清除
				}
			} catch (IOException e) {
				e.printStackTrace(); // TODO
			}
		}
	}

	public static void dealWriteEvent(Selector selector, SelectionKey key, ByteBuffer buffer) throws IOException {
		// 取消对OP_WRITE的注册
		key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
		SocketChannel sc = (SocketChannel) key.channel();

		int writeSize = 0;

		try {
			buffer.put(SimpleMessageUtil.simpleMessage("id=" + new Random().nextInt(), 64));
			buffer.flip();

			// 阻塞操作，知道写入操作系统发生缓冲区或网络IO出现异常，返回值为成功写入的字节数，当操作系统的发送缓冲区已满，返回0
			writeSize = sc.write(buffer);

			if (writeSize == 0) { // 如果未写入，则继续注册感兴趣的OP_WRITE事件
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			}
		} finally {
			buffer.clear();
		}
	}

	public static void dealReadEvent(Selector selector, SelectionKey key, ByteBuffer buffer) throws IOException {
		SocketChannel sc = (SocketChannel) key.channel();

		int byteLen = 0;
		// 读取目前可读的流，sc.read 阻塞操作，返回的为成功负责到 Buffer
		// 中的字节数，值可能为0，为流的结尾时，返回-1
		byteLen = sc.read(buffer);

		if (byteLen <= 0) { // 输入输出流结束
			return;
		}

		try {
			System.out.println("read event...");
			buffer.flip(); // flip 操作，可以用于后续的buffer操作
			System.out.println("receive Server message:" + new String(buffer.array(), UTF_8));
		} finally {
			buffer.clear();
		}
	}

	public static void dealConnectionEvent(Selector selector, SelectionKey key)
			throws ClosedChannelException, IOException {
		SocketChannel sc = (SocketChannel) key.channel();

		/*
		 * 注册感兴趣的IO读事件，通常不直接注册写事件，在发送缓存区未满的情况下，一直是可写的， 因此如果注册了写事件，
		 * 而又不用写数据，很容易造成CPU消耗100%的现象
		 */
		sc.register(selector, SelectionKey.OP_READ);

		// 完成链接的建立
		sc.finishConnect();
		System.out.println("Client finish connect.");
	}
}
