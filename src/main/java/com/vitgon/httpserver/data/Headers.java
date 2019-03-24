package com.vitgon.httpserver.data;

import java.util.HashSet;
import java.util.Set;

public class Headers {
	
	private Set<Header> headers;
	
	public Headers() {
		headers = new HashSet<Header>();
	}
	
	public String getHeader(String headerName) {
		for (Header header : this.headers) {
			if (header.getName().equalsIgnoreCase(headerName)) {
				return header.getValue();
			}
		}
		return null;
	}

	public void addHeader(Header header) {
		headers.add(header);
	}

	public Set<Header> getHeaders() {
		return headers;
	}
}
