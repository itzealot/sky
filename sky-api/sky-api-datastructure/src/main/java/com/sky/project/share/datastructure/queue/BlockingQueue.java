package com.sky.project.share.datastructure.queue;

/**
 * BlockingQueue
 * 
 * @author zealot
 * @param <T>
 */
public interface BlockingQueue<T> {

	/**
	 * 访问队首元素，队列为空时返回null
	 * 
	 * @return
	 */
	T peek();

	/**
	 * 移除队列中队首元素，队列为空时阻塞
	 * 
	 * @return
	 */
	T take();

	/**
	 * 往队列中 put 一条数据，队列满时阻塞
	 * 
	 * @param e
	 * @throws InterruptedException
	 */
	void put(T e) throws InterruptedException;
}
