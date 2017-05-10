package com.sky.projects.message.function;

import java.io.Serializable;

/**
 * Tuple<S1, S2>
 * 
 * @author zealot
 *
 * @param <S1>
 * @param <S2>
 */
@SuppressWarnings("serial")
public class Tuple2<S1, S2> implements Serializable {
	private String _1;
	private String _2;

	public Tuple2(String first, String second) {
		super();
		this._1 = first;
		this._2 = second;
	}

	public String _1() {
		return _1;
	}

	public String first() {
		return _1;
	}

	public String key() {
		return _1;
	}

	public String _2() {
		return _2;
	}

	public String second() {
		return _2;
	}

	public String value() {
		return _2;
	}
}
