package com.sky.project.share.socket;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class InetAddressTest {

	public static void main(String[] args) throws UnknownHostException, SocketException {
		InetAddress address = InetAddress.getLocalHost();
		System.out.println(address.getHostName());
		System.out.println(address.getHostAddress());
		System.out.println("mac:" + getLocalMac(address));
	}

	private static String getLocalMac(InetAddress inetAddress) throws SocketException {
		// 获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
		StringBuffer sb = new StringBuffer();
		for (int i = 0, len = mac.length; i < len; i++) {
			if (i != 0) {
				sb.append("-");
			}

			// 字节转换为整数
			String str = Integer.toHexString(mac[i] & 0xFF);
			if (str.length() == 1) {
				sb.append("0");
			}

			sb.append(str);
		}

		return sb.toString().toUpperCase();
	}

}
