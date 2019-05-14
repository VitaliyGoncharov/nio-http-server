package com.vitgon.httpserver.handler;

import com.vitgon.httpserver.request.Request;
import com.vitgon.httpserver.request.RequestHandler;
import com.vitgon.httpserver.response.Response;

public class LoginGetHandler implements RequestHandler {

	@Override
	public void handle(Request request, Response response) {
		response.setResponsePage("login");
	}
}
