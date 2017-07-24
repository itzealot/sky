package com.sky.project.share.socket.server;

import java.io.IOException;

import com.sky.project.share.socket.Container;
import com.sky.project.share.socket.SocketConst;
import com.sky.project.share.socket.server.support.ServerSocketHandlerImpl;

public class ServerMain {

	public static void main(String[] args) throws IOException {
		ServerSocketHandlerImpl handler = new ServerSocketHandlerImpl();

		Container server = new Server(SocketConst.SERVER_PORT, handler);
		server.start();
	}

}
