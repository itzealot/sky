package com.sky.project.share.api.kafka.support.provider.impl;

import java.util.Set;

import com.sky.project.share.api.kafka.support.provider.Provider;
import com.sky.project.share.api.kafka.support.provider.ProviderFactory;
import com.sky.project.share.common.support.ConcurrentHashSet;

/**
 * RroviderFactory
 * 
 * @author zealot
 *
 */
public class DefaultProviderFactory implements Provider, ProviderFactory {

	private final Set<Provider> providers;

	public DefaultProviderFactory() {
		this.providers = new ConcurrentHashSet<>();
	}

	public DefaultProviderFactory(int capacity) {
		this.providers = new ConcurrentHashSet<>(capacity);
	}

	@Override
	public void register(Provider provider) {
		if (provider != null) {
			providers.add(provider);
		}
	}

	@Override
	public void remove(Provider provider) {
		if (provider != null) {
			providers.remove(provider);
		}
	}

	@Override
	public void close() {
		if (providers != null) {
			for (Provider provider : providers) {
				provider.close();
			}
		}
	}

	@Override
	public void provide() {
		for (Provider provider : providers) {
			provider.provide();
		}
	}

}
