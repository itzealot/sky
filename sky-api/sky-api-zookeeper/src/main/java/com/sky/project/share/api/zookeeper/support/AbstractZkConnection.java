package com.sky.project.share.api.zookeeper.support;

import com.sky.project.share.api.zookeeper.ZkConnection;

public abstract class AbstractZkConnection implements ZkConnection {
	// 是否进行事务
	protected boolean transaction = false;
	// 是否后台运行
	protected boolean background = false;

	AbstractZkConnection() {
	}

}
