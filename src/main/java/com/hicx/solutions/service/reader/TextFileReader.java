package com.hicx.solutions.service.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component("txtFileReader")
public class TextFileReader implements FileReader {
	
	private final String supportedType = "txt";
	
	@Override
	public List<String> read(File file) {
		List<String> fileContents = null;
		try {
			if (file.exists()) {
				fileContents = Files.readAllLines(file.toPath());
				log.info(String.format("Successfully read contents from %s", file.getName()));
			}
		} catch (IOException e) {
			log.info("Error occurred while reading contents from file", e);
		}
		return fileContents;
	}
	@Override
	public boolean canRead(String fileType) {
		return supportedType.equals(fileType);
	}
}
