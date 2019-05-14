package com.vitgon.httpserver.view;

import java.util.Map;

import com.vitgon.httpserver.request.Request;
import com.vitgon.httpserver.response.Response;


public interface View {
	void render(Map<String, ?> model, Request request, Response response);
}
