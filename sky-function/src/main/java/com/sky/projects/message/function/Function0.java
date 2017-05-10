package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * None=>R function
 * 
 * @author zealot
 *
 * @param <R>
 */
public abstract interface Function0<R> extends Serializable {
	/**
	 * None=>R
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract R call() throws Exception;
}