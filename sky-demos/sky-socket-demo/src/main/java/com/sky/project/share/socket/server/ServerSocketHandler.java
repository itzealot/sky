package com.sky.project.share.socket.server;

import java.net.Socket;

public interface ServerSocketHandler {

	void handle(MessageServerHandler handler, Socket socket);
}
