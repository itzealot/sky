package com.sky.project.share.api.registry;

import com.sky.project.share.api.registry.support.ZkRegistry;

public class ZkRegistryTest {

	public static void main(String[] args) {
		String zkUrl = "loclhost:2181";
		ZkRegistry registry = new ZkRegistry(zkUrl);

		Thread thread = new Thread(() -> {
			registry.register(new DefaultRegistryFactory("tools-test", "V0.0.1"));
		});

		thread.setDaemon(true);
		thread.start();

		try {
			while (true) {
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
		} finally {
			registry.close();
		}
	}

}
