package com.vitgon.httpserver.request;

import com.vitgon.httpserver.response.Response;

public interface RequestHandler {
	void handle(Request request, Response response);
}
