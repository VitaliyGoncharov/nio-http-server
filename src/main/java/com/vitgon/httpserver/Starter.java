package com.vitgon.httpserver;

import com.vitgon.httpserver.enums.HttpMethod;
import com.vitgon.httpserver.handler.LoginHandler;

public class Starter {
	public static void main(String[] args) {
		Server server = new Server("192.168.1.238", 80);
		server.addHandler("/login", HttpMethod.POST, new LoginHandler());
		server.start();
	}
}
