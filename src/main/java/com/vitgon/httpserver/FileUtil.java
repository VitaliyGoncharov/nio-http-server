package com.vitgon.httpserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
	public static byte[] readFile(String filePath) throws IOException {
		return Files.readAllBytes(Paths.get("src/main/resources/" + filePath));
	}
	
	public static boolean fileExists(String filePath) {
		return Files.isRegularFile(Paths.get("src/main/resources/" + filePath));
	}
}
