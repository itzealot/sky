/**
 * 
 */
package com.surfilter.mass.services;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

/**
 * @author hapuer
 *
 */
public class TestUniq {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
		File file = new File("c://rootPath//export");
		File[] files = file.listFiles();
		Set<String> uniqSet = new HashSet<String>();
		for(File f:files){
			    List<String> macs = FileUtils.readLines(f);
			    for(String s:macs){
			    	if(!uniqSet.contains(s)){
			    		uniqSet.add(s);
			    	}
			    }
		}
		
		FileUtils.writeLines(new File("c://rootPath//result.txt"), uniqSet, true);
	}

}
