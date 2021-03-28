package com.hicx.solutions.model;

import lombok.Data;

@Data
public class FileStats {

	private String fileName;
	private int totalWords;
	private int totalDots;
	private String[] commonWords;
	
	public FileStats(String fileName) {
		this.fileName = fileName;
	}
}
