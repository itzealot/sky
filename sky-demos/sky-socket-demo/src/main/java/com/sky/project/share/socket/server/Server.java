package com.sky.project.share.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sky.project.share.socket.Container;
import com.sky.project.share.socket.server.support.SocketRunner;
import com.sky.project.share.socket.util.Closeables;

/**
 * 服务端
 * 
 * @author zealot
 */
public class Server implements Container {

	private final ExecutorService socketService;
	private final ServerSocket ss;
	private final int port;
	private final ServerSocketHandler handler;

	public Server(int port, ServerSocketHandler handler) throws IOException {
		this.port = port;
		ss = new ServerSocket(port);
		socketService = Executors.newFixedThreadPool(10);
		this.handler = handler;
	}

	@Override
	public void start() {
		while (true) { // 服务端一直等待客户端连接
			try {
				// 设置以毫秒为单位的超时时间
				// ss.setSoTimeout(1000);
				// 阻塞方法，一直等待到客户端发送建立连接的请求
				Socket socket = ss.accept();

				// 开启线程执行任务
				socketService.execute(new SocketRunnerFactory() {
					@Override
					public SocketRunner getRunner(Socket socket, ServerSocketHandler handler) {
						return new SocketRunner(socket, handler);
					}
				}.getRunner(socket, handler));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() {
		Closeables.close(ss);
		socketService.shutdown();
	}

	public int getPort() {
		return port;
	}

}
