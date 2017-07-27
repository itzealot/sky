package com.sky.project.share.algorithm;

public final class Calculates {

	public static int min(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	/**
	 * 设Ai为字符串A(a1a2a3 … am)的前i个字符(即为a1,a2,a3 … ai) <br>
	 * 设Bj为字符串B(b1b2b3 … bn)的前j个字符(即为b1,b2,b3 … bj) <br>
	 * 设 L(i,j)为使两个字符串和Ai和Bj相等的最小操作次数。
	 * 
	 * 计算字符串的相似度，返回相似度数组
	 * 
	 * @param strA
	 * @param strB
	 * @return
	 */
	public static int[][] distance(String strA, String strB) {
		int lenA = strA.length();
		int lenB = strB.length();

		int iLen = lenA + 1;
		int jLen = lenA + 1;
		int[][] temp = new int[iLen][];
		for (int i = 0; i < iLen; i++) {
			temp[i] = new int[jLen];
		}

		int i, j;
		for (i = 1; i <= lenA; i++) { // 初始化 temp[i][0]
			temp[i][0] = i;
		}

		for (j = 1; j <= lenB; j++) { // 初始化 temp[0][j]
			temp[0][j] = j;
		}

		temp[0][0] = 0;

		for (i = 1; i <= lenA; i++) {
			for (j = 1; j <= lenB; j++) {
				if (strA.charAt(i - 1) == strB.charAt(j - 1)) { // 相等则变化值等于其前面的长度
					temp[i][j] = temp[i - 1][j - 1];
				} else { // 取最小变换值
					temp[i][j] = min(temp[i - 1][j], temp[i][j - 1], temp[i - 1][j - 1]) + 1;
				}
			}
		}

		return temp;
	}

	/**
	 * 计算字符串的相似度，返回字符串的最大相似度
	 * 
	 * @param strA
	 * @param strB
	 * @return
	 */
	public static int maxDistance(String strA, String strB) {
		return distance(strA, strB)[strA.length()][strB.length()];
	}
}
