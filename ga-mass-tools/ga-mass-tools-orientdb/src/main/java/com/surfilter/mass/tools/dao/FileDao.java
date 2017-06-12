package com.surfilter.mass.tools.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDao {
	
	private String path;
	private boolean append;
	
	private BufferedWriter bufferedWriter;
	private FileWriter fw;
	
	
	public FileDao(String path) {
		super();
		this.path = path;
		this.append = false;
	}
	
	
	public FileDao(String path, boolean append) {
		super();
		this.path = path;
		this.append = append;
	}
	
	
	public void init(){
		File file = new File(this.path);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile(), append);
			bufferedWriter = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public boolean write(String writeStr){
		boolean returnFlag = false;
		try {
			bufferedWriter.write(writeStr);
			returnFlag = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnFlag;
	}
	
	
	public void close(){
		try {
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
			if (fw != null) {
				fw.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
}
