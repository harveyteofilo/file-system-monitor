package com.hicx.solutions.service.processor;

import java.util.List;

import com.hicx.solutions.model.FileStats;

public interface FileContentsProcessor {

	FileStats processContents(String fileName, List<String> fileContents);
}
