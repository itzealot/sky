package com.sky.project.share.datastructure;

/**
 * 
 * @param <E>
 * @author zealot
 */
public class LinkedList2<E> {

	private Node head;
	private Node tail;
	private int size;

	public LinkedList2() {
		this.head = null;
		this.tail = null;
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
		/**
		 * NewNode.next = this.head;<br>
		 * this.head = NewNode;<br>
		 */
		head = new Node(data, head);

		if (isEmpty()) {
			tail = head;
		}

		size++;
	}

	/**
	 * add Last
	 * 
	 * @param data
	 */
	public void add(E data) {
		if (isEmpty()) {
			head = tail = new Node(data, null);
			size++;
			return;
		}

		tail.next = new Node(data, null);
		size++;
	}

	public E removeFirst() {
		if (!isEmpty()) {
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

			while (next.next != null) {
				next = next.next;
			}

			E data = tail.data;
			tail = next;
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
