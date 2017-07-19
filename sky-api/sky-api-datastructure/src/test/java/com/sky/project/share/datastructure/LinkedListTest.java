package com.sky.project.share.datastructure;

import junit.framework.TestCase;

public class LinkedListTest extends TestCase {

	LinkedList<String> list = new LinkedList<String>();

	{
		list.add("aa");
		list.addFirst("bb");
		list.add("cc");
	}

	public void testRemoveFirst() {
		while (!list.isEmpty()) {
			System.out.println(list.removeFirst());
		}
	}

	public void testRemoveLast() {
		while (!list.isEmpty()) {
			System.out.println(list.removeLast());
		}
	}
}
