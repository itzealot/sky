package com.sky.project.share.datastructure;

public class DoubleLinkedList<E> {

	private Node head;
	private Node tail;
	private int size;

	class Node {
		E data;
		Node prev;
		Node next;

		public Node(E data, Node prev, Node next) {
			super();
			this.data = data;
			this.prev = prev;
			this.next = next;
		}
	}

	public DoubleLinkedList() {
		head = null;
		tail = null;
		size = 0;
	}

	public void addFirst(E data) {
		this.head = new Node(data, null, this.head);

		if (isEmpty()) {
			this.tail = this.head;
		}

		size++;
	}

	public void add(E data) {
		this.tail = new Node(data, this.tail, null);

		if (isEmpty()) {
			this.head = this.tail;
		}

		size++;
	}

	public E removeFirst() {
		if (isEmpty())
			return null;

		Node next = head.next;
		E data = next.data;
		head = next;
		size--;

		return data;
	}

	public E removeLast() {
		if (isEmpty())
			return null;

		Node prev = tail.next;
		E data = prev.data;
		tail = prev;
		size--;

		return data;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int size() {
		return size;
	}

}
