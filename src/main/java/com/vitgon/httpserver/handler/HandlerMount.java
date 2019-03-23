package com.vitgon.httpserver.handler;

import com.vitgon.httpserver.enums.HttpMethod;

public class HandlerMount {
	private String uri;
	private HttpMethod method;
	public HandlerMount(String uri, HttpMethod method) {
		super();
		this.uri = uri;
		this.method = method;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public HttpMethod getMethod() {
		return method;
	}
	public void setMethod(HttpMethod method) {
		this.method = method;
	}
}
