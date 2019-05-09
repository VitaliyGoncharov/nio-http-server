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
	
	public String getAttribute(String name) {
		for (Map.Entry<String, String> sessionEntry : sessionMap.entrySet()) {
			if (sessionEntry.getKey().equals(name)) {
				return sessionEntry.getValue();
			}
		}
		return null;
	}
	
	public void setAttribute(String name, String value) {
		sessionMap.put(name, value);
	}
}
