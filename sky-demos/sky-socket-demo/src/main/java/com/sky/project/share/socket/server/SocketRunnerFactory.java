package com.sky.project.share.socket.server;

import java.net.Socket;

import com.sky.project.share.socket.server.support.SocketRunner;

public interface SocketRunnerFactory {

	SocketRunner getRunner(Socket socket, ServerSocketHandler handler);
}
