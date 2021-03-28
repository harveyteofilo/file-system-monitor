package com.hicx.solutions.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class TextFileReaderTest {

	private TextFileReader textFileReader = new TextFileReader();
	
	@Test
	public void testRead() {
		File mockFile = mock(File.class);
		when(mockFile.exists()).thenReturn(true);
		try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
			List<String> fileContents = Arrays.asList(RandomStringUtils.randomAlphabetic(5));
			mockFiles.when(() -> Files.readAllLines(mockFile.toPath())).thenReturn(fileContents);
			List<String> readResult = textFileReader.read(mockFile);
			assertEquals(fileContents, readResult);
		}
	}
	
	@Test
	public void testCanRead_Txt() {
		assertTrue(textFileReader.canRead("txt"));
	}
	
	@Test
	public void testCanRead_NonTxt() {
		assertFalse(textFileReader.canRead("doc"));
	}
}
