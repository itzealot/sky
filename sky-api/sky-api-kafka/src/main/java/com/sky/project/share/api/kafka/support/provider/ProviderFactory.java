package com.sky.project.share.api.kafka.support.provider;

public interface ProviderFactory {

	void register(Provider provider);

	void remove(Provider provider);
}
