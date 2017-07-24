package com.sky.project.share.socket.client.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.sky.project.share.socket.util.Closeables;

public class ClientSocketReceiver implements Runnable {

	private final Socket socket;

	public ClientSocketReceiver(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		if (socket.isClosed()) {
			return;
		}

		BufferedReader reader = null;

		try {// 创建用于读取服务器的响应返回流
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			while (!socket.isClosed()) {
				String line = reader.readLine();

				if (!line.isEmpty()) {
					System.out.println("Client:" + Thread.currentThread().getName() + " receive msg:" + line);
				}
			}
		} catch (IOException e) {
			Closeables.close(reader);
		}
	}

}
