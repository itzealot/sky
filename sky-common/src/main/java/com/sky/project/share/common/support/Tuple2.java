package com.sky.project.share.common.support;

import java.io.Serializable;

/**
 * Tuple2
 * 
 * @author zealot
 *
 * @param <S1>
 * @param <S2>
 */
public final class Tuple2<S1, S2> implements Serializable {

	private static final long serialVersionUID = 4078648044492137455L;

	private final String first;
	private final String second;

	public Tuple2(String first, String second) {
		super();
		this.first = first;
		this.second = second;
	}

	public String _1() {
		return first;
	}

	public String first() {
		return first;
	}

	public String key() {
		return first;
	}

	public String _2() {
		return second;
	}

	public String second() {
		return second;
	}

	public String value() {
		return second;
	}

	@Override
	public String toString() {
		return first + "=" + second;
	}
}
