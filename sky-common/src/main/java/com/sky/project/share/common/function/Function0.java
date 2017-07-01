package com.sky.project.share.common.function;

import java.io.Serializable;

/**
 * None=>R function
 * 
 * @author zealot
 *
 * @param <R>
 */
@FunctionalInterface
public interface Function0<R> extends Serializable {

	/**
	 * None=>R
	 * 
	 * @return
	 * @throws Exception
	 */
	R call() throws Exception;
}