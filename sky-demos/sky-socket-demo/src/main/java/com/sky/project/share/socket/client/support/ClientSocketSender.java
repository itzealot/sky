package com.sky.project.share.socket.client.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import com.sky.project.share.socket.util.Closeables;
import com.sky.project.share.socket.util.Threads;

public class ClientSocketSender implements Runnable {

	private final Socket socket;

	public ClientSocketSender(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		if (socket.isClosed()) {
			return;
		}

		Random random = new Random();
		PrintWriter writer = null;

		try {// 创建向服务器写入的流
			writer = new PrintWriter(socket.getOutputStream(), true);
			while (!socket.isClosed()) {
				String msg = "random int value:" + random.nextInt();
				writer.println(msg); // 向服务器发送消息
				System.out.println("Client:" + Thread.currentThread().getName() + " send msg:" + msg);
				Threads.sleep(random.nextInt(1000) + 2000);
			}
		} catch (IOException e) {
			Closeables.close(writer);
		}
	}

}
