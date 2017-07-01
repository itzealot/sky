package com.sky.project.share.api.registry;

import java.util.Collection;
import java.util.List;

/**
 * Discovery
 * 
 * @author zealot
 *
 * @param <T>
 */
public interface Discovery<T> {

	/**
	 * 返回当前路径下的所有已注册节点名称
	 * 
	 * @return
	 */
	Collection<String> listRegistryNames();

	/**
	 * 在当前路径下根据节点名称返回所有的已注册信息实体
	 * 
	 * @param name
	 * @return
	 */
	List<T> listRegistry(String name);

}
