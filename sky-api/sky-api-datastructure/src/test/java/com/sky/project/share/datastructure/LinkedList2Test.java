package com.sky.project.share.datastructure;

import junit.framework.TestCase;

public class LinkedList2Test extends TestCase {

	LinkedList2<String> list = new LinkedList2<String>();

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
