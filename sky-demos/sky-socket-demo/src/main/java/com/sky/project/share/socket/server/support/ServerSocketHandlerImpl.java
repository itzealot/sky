package com.sky.project.share.socket.server.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.sky.project.share.socket.server.MessageServerHandler;
import com.sky.project.share.socket.server.ServerSocketHandler;
import com.sky.project.share.socket.util.Closeables;

/**
 * ServerSocketHandlerImpl
 * 
 * @author zealot
 */
public class ServerSocketHandlerImpl implements ServerSocketHandler {

	@Override
	public void handle(MessageServerHandler handler, Socket socket) {
		if (socket.isClosed()) {
			return;
		}

		// 读取服务器输入的流
		BufferedReader reader = null;
		// 创建向服务器写入的流
		PrintWriter writer = null;

		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);

			while (!socket.isClosed()) {
				String line = reader.readLine();

				String msg = handler.handle(line);
				if (msg != null) {
					writer.println(msg);
				}
			}
		} catch (IOException e) {
			Closeables.close(reader, writer);
		}
	}
}
