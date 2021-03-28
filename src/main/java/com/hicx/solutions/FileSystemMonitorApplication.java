package com.hicx.solutions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.hicx.solutions.service.FileMonitoringService;

@SpringBootApplication
public class FileSystemMonitorApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;
	
	@Value("${monitoring.poll.interval}")
	private Long pollInterval;

	public static void main(String[] args) {
		SpringApplication.run(FileSystemMonitorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		File directory = null;
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.println("Please input a valid directory path to monitor:");
				String pathStr = scanner.nextLine();
				try {
					Path path = Paths.get(StringUtils.stripToNull(pathStr));
					if (Files.exists(path) && Files.isDirectory(path)) {
						directory = new File(pathStr);
						break;
					}
				} catch (Exception e) {
					// INVALID PATH
				}
			}
			// MONITOR SPECIFIED DIRECTORY PATH
			FileMonitoringService fileMonitoringService = applicationContext.getBean(FileMonitoringService.class);
			while (true) {
				fileMonitoringService.monitorFiles(directory);
				Thread.sleep(pollInterval);
			}
		}
		
	}

}
