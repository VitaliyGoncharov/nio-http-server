package com.vitgon.httpserver.handler;

import com.vitgon.httpserver.request.Request;
import com.vitgon.httpserver.request.RequestHandler;
import com.vitgon.httpserver.response.Response;

public class HomeHandler implements RequestHandler {

	@Override
	public void handle(Request request, Response response) {
		String name = request.getSession().getAttribute("name");
		if (name == null) {
			name = "Guest";
		}
        response.setResponseBody("Hello, " + name);
	}
}
