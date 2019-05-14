package com.vitgon.httpserver;

import com.vitgon.httpserver.enums.HttpMethod;
import com.vitgon.httpserver.handler.HomeHandler;
import com.vitgon.httpserver.handler.LoginGetHandler;
import com.vitgon.httpserver.handler.LoginPostHandler;
import com.vitgon.httpserver.handler.UploadFileHandler;

public class Starter {
	public static void main(String[] args) {
		Server server = new Server("localhost", 80);
		server.addHandler("/", HttpMethod.GET, new HomeHandler());
		server.addHandler("/login", HttpMethod.GET, new LoginGetHandler());
		server.addHandler("/login", HttpMethod.POST, new LoginPostHandler());
		server.addHandler("/upload", HttpMethod.POST, new UploadFileHandler());
		server.start();
	}
}
