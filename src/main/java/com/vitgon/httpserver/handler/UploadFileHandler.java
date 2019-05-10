package com.vitgon.httpserver.handler;

import java.io.IOException;

import com.vitgon.httpserver.data.Part;
import com.vitgon.httpserver.request.Request;
import com.vitgon.httpserver.request.RequestHandler;
import com.vitgon.httpserver.response.Response;
import com.vitgon.httpserver.util.FileUtil;

public class UploadFileHandler implements RequestHandler {

	@Override
	public void handle(Request request, Response response) {
		Part part = request.getPart("rabbit");
		if (part != null) {
			try {
				FileUtil.saveToFile("src/main/resources/uploads/" + part.getFilename(), part.getContent());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		response.setResponseBody("<b>File " + part.getFilename() + "was successfully saved!");
		response.sendRedirect("/upload.html", 4);
	}	
}
