package com.sky.projects.message.support;

import java.util.Map;

import com.google.common.collect.Maps;
import com.sky.projects.message.function.Function;

public class FunctionHolder<K, V1, V2> {

	private final Map<K, Function<V1, V2>> map = Maps.newHashMap();

	public FunctionHolder<K, V1, V2> add(K key, Function<V1, V2> func) {
		this.map.put(key, func);
		return this;
	}

	public FunctionHolder<K, V1, V2> remove(K key) {
		this.map.remove(key);
		return this;
	}

	public Function<V1, V2> get(K key) {
		return map.get(key);
	}
}
