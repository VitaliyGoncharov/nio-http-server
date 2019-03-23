package com.vitgon.httpserver.request;

import java.util.HashSet;
import java.util.Set;

import com.vitgon.httpserver.data.Cookies;
import com.vitgon.httpserver.data.Header;
import com.vitgon.httpserver.data.HttpSession;
import com.vitgon.httpserver.data.RequestBody;
import com.vitgon.httpserver.data.RequestParameters;
import com.vitgon.httpserver.enums.HttpMethod;


public class Request {
	private HttpMethod method;
	private String uri;
	private String httpVersion;
	
	private String sessionId;
	private HttpSession session;
	private Cookies cookies;
	private Set<Header> headers;
	private RequestBody requestBody;
	private RequestParameters parameters;
	
	public Request() {
		headers = new HashSet<>();
		cookies = new Cookies();
		parameters = new RequestParameters();
		requestBody = new RequestBody();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public Set<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(Set<Header> headers) {
		this.headers = headers;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}
	
	public String getHeader(String headerName) {
		for (Header header : this.headers) {
			if (header.getName().equals(headerName)) {
				return header.getValue();
			}
		}
		return null;
	}

	public Cookies getCookies() {
		return cookies;
	}

	public void setCookies(Cookies cookies) {
		this.cookies = cookies;
	}

	public RequestParameters getParameters() {
		return parameters;
	}

	public void setParameters(RequestParameters parameters) {
		this.parameters = parameters;
	}

	public RequestBody getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(RequestBody requestBody) {
		this.requestBody = requestBody;
	}
}
