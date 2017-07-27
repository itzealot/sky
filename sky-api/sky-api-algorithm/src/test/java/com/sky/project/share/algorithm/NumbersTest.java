package com.sky.project.share.algorithm;

import junit.framework.TestCase;

public class NumbersTest extends TestCase {

	public void testIsOdd() {
		assertEquals(true, Numbers.isOdd(1));
		assertEquals(false, Numbers.isOdd(2));
		assertEquals(true, Numbers.isOdd(3));
		assertEquals(false, Numbers.isOdd(4));
	}

	public void testIsEven() {
		assertEquals(false, Numbers.isEven(1));
		assertEquals(true, Numbers.isEven(2));
		assertEquals(false, Numbers.isEven(3));
		assertEquals(true, Numbers.isEven(4));
	}

	public void testIsPow2() {
		assertEquals(true, Numbers.isPow2(1));
		assertEquals(true, Numbers.isPow2(2));
		assertEquals(false, Numbers.isPow2(3));
		assertEquals(true, Numbers.isPow2(4));
	}

	public void testBitOneAmount() {
		assertEquals(1, Numbers.bitOneAmount(1));
		assertEquals(1, Numbers.bitOneAmount(2));
		assertEquals(2, Numbers.bitOneAmount(3));
		assertEquals(1, Numbers.bitOneAmount(4));
	}

	public void testMove() {
		assertEquals(2, Numbers.moveLeft(1, 1));
		assertEquals(4, Numbers.moveLeft(1, 2));
		assertEquals(8, Numbers.moveLeft(1, 3));

		assertEquals(1, Numbers.moveRight(8, 3));
		assertEquals(1, Numbers.moveRight(9, 3));
		assertEquals(2, Numbers.moveRight(8, 2));
	}

}
