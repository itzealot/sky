package com.sky.project.share.datastructure;

public class LinkedList<E> {

	private Node head;
	private int size;

	public LinkedList() {
		this.head = null;
		this.size = 0;
	}

	class Node {
		E data;
		Node next;

		public Node(E data, Node next) {
			super();
			this.data = data;
			this.next = next;
		}
	}

	/**
	 * add First
	 * 
	 * @param data
	 */
	public void addFirst(E data) {
		if (this.head == null) {
			this.head = new Node(data, null);
		} else {
			/**
			 * NewNode.next = this.head;<br>
			 * this.head = NewNode;<br>
			 */
			this.head = new Node(data, this.head);
		}
		size++;
	}

	/**
	 * add Last
	 * 
	 * @param data
	 */
	public void add(E data) {
		if (this.head == null) {
			this.head = new Node(data, null);
		} else {
			Node node = this.head;
			while (node.next != null) {
				node = node.next;
			}

			/**
			 * NewNode.next = null;<br>
			 */
			Node n = new Node(data, null);
			node.next = n;
		}

		size++;
	}

	public E removeFirst() {
		if (size > 0) {
			E data = head.data;
			head = head.next;
			size--;
			return data;
		}
		return null;
	}

	public E removeLast() {
		if (size > 0) {
			Node next = head;
			Node prev = null;

			while (next.next != null) {
				prev = next;
				next = next.next;
			}

			E data = next.data;
			if (size > 1) {
				prev.next = null;
			} else {
				head = null;
			}
			size--;
			return data;
		}
		return null;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int size() {
		return size;
	}

}
