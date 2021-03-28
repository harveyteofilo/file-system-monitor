package com.hicx.solutions.service;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.hicx.solutions.model.FileStats;
import com.hicx.solutions.service.processor.FileContentsProcessor;
import com.hicx.solutions.service.reader.FileReaderFactory;
import com.hicx.solutions.service.reader.TextFileReader;

@RunWith(MockitoJUnitRunner.class)
public class FileMonitoringServiceTest {
	
	@Mock
	private FileReaderFactory fileReaderFactory;
	
	@Mock
	private FileContentsProcessor fileContentsProcessor;

	@InjectMocks
	private FileMonitoringServiceImpl fileMonitoringService;
	
	private String[] supportedExtensions = new String[] {"txt"};
	
	private String destDirectory = "";
	
	@Before
	public void init() {
		ReflectionTestUtils.setField(fileMonitoringService, "supportedExtensions", supportedExtensions);
		ReflectionTestUtils.setField(fileMonitoringService, "destDirectory", destDirectory);
	}
	
	@Test
	public void testMonitorFiles() throws Exception {
		File directory = mock(File.class);
		when(directory.getPath()).thenReturn("%s/", RandomStringUtils.randomAlphabetic(5));
		File directoryFile = mock(File.class);	
		when(directoryFile.exists()).thenReturn(true);
		when(directoryFile.getName()).thenReturn(String.format("%s.txt", RandomStringUtils.randomAlphabetic(5)));
		when(directory.listFiles(any(FilenameFilter.class)))
			.thenReturn(new File[] {directoryFile});
		when(fileReaderFactory.getReader(anyString())).thenReturn(Optional.of(new TextFileReader()));
		
		try (MockedStatic<Files> staticFiles = mockStatic(Files.class)) {
			List<String> fileContents = Arrays.asList(RandomStringUtils.randomAlphabetic(5));
			staticFiles.when(() -> Files.readAllLines(directoryFile.toPath())).thenReturn(fileContents);
			
			when(fileContentsProcessor.processContents(anyString(), any(List.class)))
				.thenReturn(mock(FileStats.class));
			
			staticFiles.when(() -> 
				Files.createDirectory(any(Path.class))
			).thenAnswer(o -> {
				// DO NOTHING
				return null;
			});
			
			staticFiles.when(() -> 
				Files.move(any(Path.class), any(Path.class), any(StandardCopyOption.class))
			).thenAnswer(o -> {
				// DO NOTHING
				return null;
			});
			fileMonitoringService.monitorFiles(directory);
			verify(fileContentsProcessor, atLeast(1)).processContents(anyString(), any(List.class));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
