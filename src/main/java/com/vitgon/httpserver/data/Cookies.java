package com.vitgon.httpserver.data;

import java.util.HashSet;
import java.util.Set;

public class Cookies {
	private Set<Cookie> cookies;
	
	public Cookies() {
		cookies = new HashSet<>();
	}
	
	public String get(String cookieName) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
	public Cookie getCookie(String cookieName) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				return cookie;
			}
		}
		return null;
	}
	
	public void add(Cookie cookie) {
		if (containsCookieKey(cookie.getName())) {
			cookies.remove(getCookie(cookie.getName()));
		}
		cookies.add(cookie);
	}
	
	public boolean containsCookieKey(String cookieKey) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieKey)) {
				return true;
			}
		}
		return false;
	}
	
	public Set<Cookie> getAll() {
		return cookies;
	}
	
	public Set<Cookie> setAll(Set<Cookie> cookies) {
		return cookies;
	}
	
	public boolean isEmpty() {
		return cookies.isEmpty();
	}
}
