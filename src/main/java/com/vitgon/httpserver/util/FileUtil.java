package com.vitgon.httpserver.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
		if (!file.exists()) {
			file.createNewFile();
		}
		
		
		if (path.endsWith(".txt")) {
			BufferedWriter buffWriter = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8);
			buffWriter.write(new String(arr, StandardCharsets.UTF_8));
			buffWriter.close();
		} else {
			FileOutputStream outputStream = new FileOutputStream(path);
			outputStream.write(arr);
			outputStream.close();
		}
		
		System.out.printf("File was created: %s%n", file.getName());
	}
}
