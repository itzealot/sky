/**
 * 
 */
package com.surfilter.mass.services;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * @author hapuer
 *
 */
public class TestSplit {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		File file = new File("c:\\rootPath\\111.log");
		String ff = FileUtils.readFileToString(file);
		System.out.println(ff.split("\002").length);
	}

}
