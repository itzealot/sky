package com.sky.projects.analysis.main;

public class PYAnalysis implements IMain {
	public void execute(String[] args) {
		new DGAnalysis().execute(args);
	}
}