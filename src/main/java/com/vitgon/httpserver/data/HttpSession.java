package com.vitgon.httpserver.data;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
	private Map<String, String> sessionMap = new HashMap<>();
	
	public Map<String, String> getSessionMap() {
		return sessionMap;
	}
	public void setSessionMap(Map<String, String> sessionMap) {
		this.sessionMap = sessionMap;
	}
}
