package com.sky.projects.analysis.main;

import java.io.Serializable;

import com.sky.projects.analysis.config.Config;

@SuppressWarnings("serial")
public class Main implements Config, Serializable {
	public static void main(String[] args) {
		try {
			Class<?> cls = Class.forName(MAINCLASS);
			IMain main = (IMain) cls.newInstance();

			System.out.println("main class:" + main);
			main.execute(args);
		} catch (Exception e) {
			System.err.print(e);
			for (StackTraceElement ste : e.getStackTrace()) {
				System.err.print(ste);
			}
		}
	}
}