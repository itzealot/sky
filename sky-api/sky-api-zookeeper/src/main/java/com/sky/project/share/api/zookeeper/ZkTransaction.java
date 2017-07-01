package com.sky.project.share.api.zookeeper;

/**
 * ZkTransaction
 * 
 * @author zealot
 *
 */
public interface ZkTransaction {

	/**
	 * 开启事务
	 */
	void beginTransaction();

	/**
	 * 是否进行事务
	 * 
	 * @return
	 */
	boolean inTransaction();

	/**
	 * 提交事务
	 */
	void commitTransaction();
}
