package com.sky.project.share.algorithm;

/**
 * Numbers
 * 
 * @author zealot
 */
public final class Numbers {

	/**
	 * 判断一个数的奇偶性，为奇数返回true，为偶数返回false
	 * 
	 * @param num
	 * @return
	 */
	public static boolean isOdd(int num) {
		return (num & 0x1) == 1;
	}

	/**
	 * 判断一个数的奇偶性，为偶数返回true，为奇数返回false
	 * 
	 * @param num
	 * @return
	 */
	public static boolean isEven(int num) {
		return !isOdd(num);
	}

	/**
	 * 快速设置0：从末尾到第一次遇到1的位置 x = x & (x - 1) <br>
	 * 表达式的意思就是把x的二进制表示从最低位 直到遇到第一个1的比特置0 <br>
	 * 把一个整数减去1，再和原整数做与运算，会把该整数最右边一个1变成0.那么一个整数的二进制表示中有多少个1，就可以进行多少次这样的操作
	 * 
	 * 十进制数中二进制位的个数
	 * 
	 * @param n
	 * @return
	 */
	public static int bitOneAmount(int n) {
		int counts = 0;

		while (n != 0) {
			n = n & (n - 1);
			counts++;
		}

		return counts;
	}

	/**
	 * 判断是否为2的次方
	 * 
	 * @param n
	 * @return
	 */
	public static boolean isPow2(int n) {
		return (n & (n - 1)) == 0;
	}

	/**
	 * n * pow(2, m)
	 * 
	 * @param n
	 * @param m
	 * @return
	 */
	public static int moveLeft(final int n, final int m) {
		return n << m;
	}

	/**
	 * n / pow(2, m)
	 * 
	 * @param n
	 * @param m
	 * @return
	 */
	public static int moveRight(final int n, final int m) {
		return n >> m;
	}

	private Numbers() {
	}
}
