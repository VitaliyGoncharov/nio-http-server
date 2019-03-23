package com.vitgon.httpserver.handler;

import com.vitgon.httpserver.request.Request;
import com.vitgon.httpserver.request.RequestHandler;
import com.vitgon.httpserver.response.Response;

public class LoginHandler implements RequestHandler {

	@Override
	public void handle(Request request, Response response) {
//		request
//		String name;
//		if (request.getSession() != null) {
//			name = request.getSession().getSessionMap().get("name");
//		} else {
//			name = "Guest";
//		}
//		response.setResponseBody("Hey, hello " + name + "!!!");
		response.setResponsePage("index.html");
	}
}
