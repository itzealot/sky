package com.sky.project.share.socket.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class SimpleMessageUtil {

	public static final Charset UTF_8 = Charset.forName("UTF-8");
	public static final byte TAB_DELIM = '\r';
	public static final byte NEW_LINE = '\n';

	public static ByteBuffer wrapMessage(byte[] datas, int capacity) {
		ByteBuffer buffer = ByteBuffer.allocate(capacity + 2);

		buffer.put(datas).put(TAB_DELIM).put(NEW_LINE).flip();

		return buffer;
	}

	static byte[] produceSimpleMessage(String value) {
		return value.getBytes(UTF_8);
	}

	public static ByteBuffer simpleMessage(String value, int capacity) {
		return wrapMessage(produceSimpleMessage(value), capacity);
	}

	public static ByteBuffer wrapMessageWithTimespan(String value, int capacity) {
		ByteBuffer buffer = ByteBuffer.allocate(capacity);

		buffer.put(value.getBytes(UTF_8)).put(", timespan:".getBytes(UTF_8))
				.put(String.valueOf(System.currentTimeMillis() / 1000).getBytes(UTF_8)).put(TAB_DELIM).put(NEW_LINE)
				.flip();

		return buffer;
	}

	private SimpleMessageUtil() {
	}
}
