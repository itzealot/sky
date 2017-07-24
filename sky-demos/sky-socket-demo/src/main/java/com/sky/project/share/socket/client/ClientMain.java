package com.sky.project.share.socket.client;

import java.io.IOException;
import java.net.UnknownHostException;

import com.sky.project.share.socket.Container;
import com.sky.project.share.socket.SocketConst;
import com.sky.project.share.socket.util.Threads;

public class ClientMain {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			synchronized (ClientMain.class) {
				ClientMain.class.notify();
			}
		}));

		for (int i = 1; i <= 5; i++) {
			Container client = new Client(SocketConst.SERVER_HOST, SocketConst.SERVER_PORT, "Client-" + i + "-");
			client.start();
			Threads.sleep(2 * 1000);
		}

		synchronized (ClientMain.class) {
			ClientMain.class.wait();
		}
	}
}
