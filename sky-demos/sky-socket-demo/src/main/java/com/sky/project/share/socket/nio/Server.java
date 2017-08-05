package com.sky.project.share.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;

import com.sky.project.share.socket.SocketConst;

public class Server {

	public static void main(String[] args) throws IOException {
		ServerSocketChannel channel = ServerSocketChannel.open();

		ServerSocket socket = channel.socket();

		socket.bind(new InetSocketAddress(SocketConst.SERVER_PORT));

		// 设置为非阻塞
		channel.configureBlocking(false);

		// channel.register(sel, ops)
	}

}
