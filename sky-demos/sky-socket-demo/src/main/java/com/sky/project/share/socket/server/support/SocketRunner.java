package com.sky.project.share.socket.server.support;

import java.net.Socket;

import com.sky.project.share.socket.server.MessageServerHandler;
import com.sky.project.share.socket.server.ServerSocketHandler;

/**
 * SocketRunner
 * 
 * @author zealot
 */
public class SocketRunner implements Runnable {

	private final Socket socket;
	private final ServerSocketHandler handler;

	public SocketRunner(Socket socket, ServerSocketHandler handler) {
		this.socket = socket;
		this.handler = handler;
	}

	@Override
	public void run() {
		handler.handle(new MessageServerHandler() {
			@Override
			public String handle(String msg) {
				System.out.println("Server:" + Thread.currentThread().getName() + " finish deal msg:" + msg);
				return msg + ", timespan:" + System.currentTimeMillis();
			}
		}, socket);
	}
}
