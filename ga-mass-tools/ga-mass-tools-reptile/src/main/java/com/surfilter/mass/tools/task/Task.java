package com.surfilter.mass.tools.task;

/**
 * Task for run job
 * 
 * @author zealot
 *
 */
public interface Task extends Runnable {

	/**
	 * shutdown
	 */
	void shutdown();
}
