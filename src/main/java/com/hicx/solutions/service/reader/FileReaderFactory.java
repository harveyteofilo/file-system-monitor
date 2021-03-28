package com.hicx.solutions.service.reader;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileReaderFactory {

	@Autowired
	private List<FileReader> fileReaders;
	
	public Optional<FileReader> getReader(String fileName) {
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
		return fileReaders.stream().filter(fileReader -> fileReader.canRead(fileType)).findFirst();
	}
}
