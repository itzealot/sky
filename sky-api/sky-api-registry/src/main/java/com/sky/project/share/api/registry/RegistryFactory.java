package com.sky.project.share.api.registry;

import com.sky.project.share.api.registry.entity.RegistryInfo;

/**
 * Registry
 * 
 * @author zealot
 *
 */
@FunctionalInterface
public interface RegistryFactory {

	/**
	 * 
	 * @return
	 */
	RegistryInfo newInstance();
}
