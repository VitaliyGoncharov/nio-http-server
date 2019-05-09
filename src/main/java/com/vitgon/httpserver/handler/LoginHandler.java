package com.vitgon.httpserver.handler;

import com.vitgon.httpserver.data.HttpSession;
import com.vitgon.httpserver.request.Request;
import com.vitgon.httpserver.request.RequestHandler;
import com.vitgon.httpserver.response.Response;

public class LoginHandler implements RequestHandler {

	@Override
	public void handle(Request request, Response response) {
		String name = request.getParameter("name");
		HttpSession session = request.getSession();
		session.setAttribute("name", name);
		response.setResponseBody("<b>You successfully set name!</b>");
	}
}
