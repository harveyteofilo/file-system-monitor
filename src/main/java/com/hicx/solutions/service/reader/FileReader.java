package com.hicx.solutions.service.reader;

import java.io.File;
import java.util.List;

public interface FileReader {

	List<String> read(File file);
	boolean canRead(String fileType);
}
