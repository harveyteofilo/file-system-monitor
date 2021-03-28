package com.hicx.solutions.service;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hicx.solutions.model.FileStats;
import com.hicx.solutions.service.processor.FileContentsProcessor;
import com.hicx.solutions.service.reader.FileReader;
import com.hicx.solutions.service.reader.FileReaderFactory;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class FileMonitoringServiceImpl implements FileMonitoringService {
	
	@Value("#{'${monitoring.file.extensions}'.split(',')}")
	private String[] supportedExtensions;
	
	@Value("${monitoring.file.dest_directory}")
	private String destDirectory;
	
	@Autowired
	private FileReaderFactory fileReaderFactory;
	
	@Autowired
	private FileContentsProcessor fileContentsProcessor;
	
	@Override
	public void monitorFiles(File directory) throws Exception {
		// FILTER ALL FILES THAT MATCHES THE SUPPORTED FILE EXTENSIONS
		List<File> validFiles = Arrays.asList(filterFiles(directory));
		validFiles.stream().forEach(file -> {
			new Thread(() -> {
				FileStats fileStats = processFile(file);
				if (null != fileStats) {
					moveFile(directory, file);
				}
			}).run();
		});
	}
	
	private File[] filterFiles(File directory) {
		return directory.listFiles((dir, fileName) -> {
			String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
			return Arrays.asList(supportedExtensions).contains(fileExtension);
		});
	}
	
	private FileStats processFile(File file) {
		log.info(String.format("Processing file - %s", file.getName()));
		Optional<FileReader> fileReader = fileReaderFactory.getReader(file.getName());
		if (fileReader.isEmpty()) {
			log.info(String.format("No Reader found for specified file - %s", file.getName()));
			return null;
		} else {
			List<String> fileContents = fileReader.get().read(file);
			FileStats fileStats = fileContentsProcessor.processContents(file.getName(), fileContents);
			System.out.println(fileStats);
			return fileStats;
		}
	}
	
	private void moveFile(File currDirectory, File file) {
		String separator = FileSystems.getDefault().getSeparator();
		String currPathStr = currDirectory.getPath().endsWith(separator) ? 
			currDirectory.getPath() : String.format("%s%s", currDirectory.getPath(), separator);
		String destPathStr = String.format("%s%s%s", currPathStr, destDirectory, separator);
		try {
			if (!Files.exists(Paths.get(destPathStr))) {
				Files.createDirectory(Paths.get(destPathStr));
			}
			Path destPath = Paths.get(String.format("%s%s", destPathStr, file.getName()));
			Files.move(Paths.get(file.getPath()), destPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			log.info("Error encountered while moving file.");
		}
	}
}
