package com.sky.project.share.api.zookeeper;

/**
 * ZkBackground
 * 
 * @author zealot
 *
 */
public interface ZkBackground {

	/**
	 * 设置是否是后台运行
	 * 
	 * @param background
	 */
	void setBackground(boolean background);

	/**
	 * 是否是否后台运行
	 * 
	 * @return
	 */
	boolean isBackground();
}
