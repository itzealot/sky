package com.sky.project.share.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import com.sky.project.share.socket.SocketConst;
import com.sky.project.share.socket.util.Closeables;
import com.sky.project.share.socket.util.SimpleMessageUtil;

/**
 * 客户端使用通道和缓冲区是可以的，不过实际上通道和缓冲区主要用于需要高效处理很多并发连接的服务器系统。
 * 要处理服务器，除了用于客户端的缓存区和通道外，还需要一些选择器Selector，运行服务器查找所有准备好接收输出或发送输入的连接。
 * 
 * @author zealot
 */
public class ServerSocketChannelMain {

	private static final Charset UTF_8 = SimpleMessageUtil.UTF_8;

	public static void main(String[] args) {
		runServerNotBlcoked(SocketConst.SERVER_HOST, SocketConst.SERVER_PORT);
	}

	public static void runServerBlcoked(String host, int port) {
		try {
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			ServerSocket socket = serverChannel.socket();

			// 与Socket一样，需要绑定端口
			// 在 unix(linux,windows)上必须是root用户，非root用户只能绑定1024及以上端口
			socket.bind(new InetSocketAddress(host, port));

			// 接收客户端连接
			SocketChannel clientChannel = serverChannel.accept();
		} catch (IOException e) {
			e.printStackTrace(); // TODO
		}
	}

	public static void runServerNotBlcoked(String host, int port) {
		ServerSocketChannel serverChannel = null;
		Selector selector = null;

		try {
			serverChannel = ServerSocketChannel.open();

			// 将 ServerSocketChannel 也设置为非阻塞模式
			// 默认情况下 ServerSocketChannel.accept 方法会阻塞
			// 如果没有如站连接，非阻塞的accept几乎会立即返回null，所以需要检查是否为 null
			serverChannel.configureBlocking(false);

			ServerSocket serverSocket = serverChannel.socket();

			// 与Socket一样，需要绑定端口，
			// 在 unix(linux,windows)上必须是root用户，非root用户只能绑定1024及以上端口
			serverSocket.bind(new InetSocketAddress(host, port));

			// 现在有打开两个通道，服务器通道和客户端通道。两个通道都需要处理。
			// 他们都会无限运行下去。此外，处理服务器通道会创建更多打开的客户端通道。
			// 传统方法中，要为每一个连接分配一个线程，线程数目会随着客户端连接迅速攀升。
			// 相反，在新的IO中，可以创建一个Selector，允许程序迭代处理所有准备好的连接。
			// Selector获取通过Selector.open 静态工厂方法。
			selector = Selector.open();

			// 对于服务器Socket，唯一关系的是OP_ACCEPT事件，即服务器通道是否准备好接收一个新连接
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace(); // TODO
			return;
		}

		while (true) {
			int selects = 0;

			try {
				selects = selector.select();
			} catch (IOException e) {
				e.printStackTrace(); // TODO
				break;
			}

			if (selects == 0) {
				continue;
			}

			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = keys.iterator();

			while (iterator.hasNext()) { // 遍历所有的 SelectionKey
				SelectionKey key = iterator.next();
				iterator.remove(); // 移除事件，防止事件重复

				try {
					if (key.isAcceptable()) { // 处理连接事件
						System.out.println("connect event...");
						serverDealConnectEventOnlyWrite(selector, key);
					} else if (key.isWritable()) { // 处理写入事件
						System.out.println("write event...");
						serverDealWriteEvent(selector, key);
					} else if (key.isReadable()) { // 处理读取事件
						System.out.println("read event...");
						serverDealReadEvent(selector, key);
					}
				} catch (IOException e) {
					e.printStackTrace(); // TODO
				} finally {
					key.cancel();
					Closeables.close(key.channel());
				}
			}
		}
	}

	public static void serverDealReadEvent(Selector selector, SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();

		// 从通道中获取 ByteBuffer
		ByteBuffer buffer = (ByteBuffer) key.attachment();
		buffer.flip();

		System.out.println("Read message:" + new String(buffer.array(), UTF_8));
		buffer.clear();
		client.write(SimpleMessageUtil.wrapMessageWithTimespan(new String(buffer.array(), UTF_8), 128));
	}

	public static void serverDealWriteEvent(Selector selector, SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();

		// 从通道中获取 ByteBuffer
		ByteBuffer buffer = (ByteBuffer) key.attachment();

		boolean flag = false;
		if (!buffer.hasRemaining()) { // 覆盖为新的数据
			flag = true;
		}

		if (flag) {
			client.write(SimpleMessageUtil.wrapMessageWithTimespan(new String(buffer.array(), UTF_8), 128));
		} else {
			client.write(SimpleMessageUtil.simpleMessage("Message Overrite", 32));
		}
	}

	public static void serverDealConnectEventOnlyWrite(Selector selector, SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();

		System.out.println("Accept connection from " + client);

		// 在服务器端，将客户端通道处于非阻塞模式，运行服务器处理多个并发连接
		client.configureBlocking(false);

		// 注册写事件
		SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);

		// 发送消息
		clientKey.attach(SimpleMessageUtil.simpleMessage("connect success", 32));
	}
}
