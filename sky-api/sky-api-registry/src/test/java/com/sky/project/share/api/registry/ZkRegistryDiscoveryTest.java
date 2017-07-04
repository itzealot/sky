package com.sky.project.share.api.registry;

import java.util.Collection;

import com.sky.project.share.api.registry.entity.RegistryInfo;
import com.sky.project.share.api.registry.support.zookeeper.ZkRegistryDiscovery;

public class ZkRegistryDiscoveryTest {

	public static void main(String[] args) {
		String zkUrl = "localhost:2181";

		Discovery<RegistryInfo> discovery = new ZkRegistryDiscovery(zkUrl);

		System.out.println("names:" + discovery.listRegistryNames());

		Collection<RegistryInfo> infos = discovery.listRegistry("tools-test");

		if (infos != null)
			for (RegistryInfo info : infos) {
				System.out.println("appId=" + info.getAppId() + ", register Name=" + info.getRegisterName()
						+ ", hostName=" + info.getHostName());
			}
	}

}
