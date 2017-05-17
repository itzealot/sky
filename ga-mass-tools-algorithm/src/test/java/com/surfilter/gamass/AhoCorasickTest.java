package com.surfilter.gamass;

import java.util.Iterator;

import com.surfilter.gamass.ac.AhoCorasick;
import com.surfilter.gamass.ac.SearchResult;

import junit.framework.TestCase;

public class AhoCorasickTest extends TestCase {

	public void testSearch() {
		AhoCorasick<String> tree = new AhoCorasick<>();
		tree.add("hello".getBytes(), "hello");
		tree.add("world".getBytes(), "world");
		tree.prepare();

		Iterator<SearchResult<String>> searcher = tree.search("hello world".getBytes());

		while (searcher.hasNext()) {
			SearchResult<String> result = searcher.next();
			System.out.println(result.getOutputs());

			System.out.println("Found at index: " + result.getLastIndex());
		}
	}
}
