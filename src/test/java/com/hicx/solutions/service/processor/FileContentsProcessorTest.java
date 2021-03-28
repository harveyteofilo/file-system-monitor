package com.hicx.solutions.service.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import com.hicx.solutions.model.FileStats;

public class FileContentsProcessorTest {

	private FileContentsProcessorImpl fileContentsProcessor = 
			new FileContentsProcessorImpl();
	
	@Test
	public void testProcessContents() {
		String fileName = RandomStringUtils.randomAlphabetic(5);
		List<String> fileContents = Arrays.asList(RandomStringUtils.random(100, 'a', 'b', 'c', '.', ' '));
		FileStats fileStats = fileContentsProcessor.processContents(fileName, fileContents);
		assertNotNull(fileStats);
		assertEquals(fileName, fileStats.getFileName());
		assertTrue(fileStats.getTotalDots() != 0);
		assertTrue(fileStats.getTotalWords() != 0);
		assertNotNull(fileStats.getCommonWords());
	}
}
