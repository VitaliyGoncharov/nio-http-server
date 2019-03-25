package com.vitgon.httpserver.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
	public static byte[] readFile(String filePath) throws IOException {
		return Files.readAllBytes(Paths.get("src/main/resources/" + filePath));
	}
	
	public static boolean fileExists(String filePath) {
		return Files.isRegularFile(Paths.get("src/main/resources/" + filePath));
	}
	
	public static void saveToFile(String path, byte[] arr) throws IOException {
		File file = Paths.get(path).toFile();
		System.out.println(file.getAbsolutePath());
		if (!file.exists()) {
			file.createNewFile();
		}
		BufferedWriter buffWriter = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8);
		buffWriter.write(new String(arr, StandardCharsets.UTF_8));
		buffWriter.close();
	}
}
