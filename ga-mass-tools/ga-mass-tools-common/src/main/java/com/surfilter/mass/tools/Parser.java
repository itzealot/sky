package com.surfilter.mass.tools;

public interface Parser<T> {

	public T[] parse(T line);

}
