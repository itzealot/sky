package com.sky.project.share.api.registry;

import java.util.Date;

import com.sky.project.share.api.registry.entity.RegistryInfo;
import com.sky.project.share.api.registry.util.NetUtil;
import com.sky.project.share.api.registry.util.SystemPropertiesUtil;

/**
 * DefaultRegistryFactory
 * 
 * @author zealot
 *
 */
public class DefaultRegistryFactory implements RegistryFactory {

	private final String registerName;
	private final String version;

	public DefaultRegistryFactory(String registerName, String version) {
		this.registerName = registerName;
		this.version = version;
	}

	@Override
	public RegistryInfo newInstance() {
		return new RegistryInfo(registerName, NetUtil.getLocalAddress().getHostAddress(), NetUtil.getLocalHost(),
				new Date().getTime() / 1000, SystemPropertiesUtil.getString("user.dir"), version);
	}
}
