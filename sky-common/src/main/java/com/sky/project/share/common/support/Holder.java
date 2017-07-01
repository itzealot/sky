package com.sky.project.share.common.support;

import java.io.Serializable;
import java.util.Objects;

/**
 * Holder
 * 
 * @author zealot
 *
 * @param <T>
 */
public final class Holder<T> implements Serializable {

	private static final long serialVersionUID = -5594578012388105205L;

	private final T value;

	/**
	 * @param value
	 *            can't be null
	 */
	public Holder(T value) {
		super();
		Objects.requireNonNull(value, "value can't be null");
		this.value = value;
	}

	public T get() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
