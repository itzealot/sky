package com.sky.project.share.socket.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sky.project.share.socket.Container;
import com.sky.project.share.socket.client.support.ClientSocketReceiver;
import com.sky.project.share.socket.client.support.ClientSocketSender;
import com.sky.project.share.socket.pool.NamedThreadFactory;
import com.sky.project.share.socket.util.Closeables;

/**
 * 直接实现: <br>
 * <code>
 * Socket socket = Socket(String host, int port);
 * </code>
 * 
 * 超时方式实现: <br>
 * <code>
 * Socket socket = new Socket();
 * socket.connect(SocketAddress endpoint, int timeout);
 * </code>
 * 
 * @author zealot
 */
public class Client implements Container {

	private final Socket socket;
	private final int port;
	private final ExecutorService sendService;
	private final ExecutorService receiveService;

	public Client(String remoteHost, int port, String name) throws UnknownHostException, IOException {
		socket = new Socket(remoteHost, port);
		this.port = port;
		this.sendService = Executors.newSingleThreadExecutor(new NamedThreadFactory(name + "Sender", true));
		this.receiveService = Executors.newSingleThreadExecutor(new NamedThreadFactory(name + "Receiver", true));
	}

	@Override
	public void start() {
		// 不定时向服务器发送消息
		this.sendService.execute(new ClientSocketSender(socket));

		// 实时接收来自服务器发送的消息
		this.receiveService.execute(new ClientSocketReceiver(socket));
	}

	@Override
	public void close() {
		Closeables.close(socket);
	}

	public int getPort() {
		return port;
	}
}
