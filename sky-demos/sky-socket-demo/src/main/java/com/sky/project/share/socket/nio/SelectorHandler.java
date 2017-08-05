package com.sky.project.share.socket.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface SelectorHandler {

	void handle(Selector selector, SelectionKey key) throws IOException;
}
