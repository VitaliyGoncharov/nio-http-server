package com.vitgon.httpserver.request;

import java.util.ArrayList;
import java.util.List;

import com.vitgon.httpserver.data.Cookies;
import com.vitgon.httpserver.data.Headers;
import com.vitgon.httpserver.data.HttpSession;
import com.vitgon.httpserver.data.Part;
import com.vitgon.httpserver.data.RequestBody;
import com.vitgon.httpserver.data.RequestParameter;
import com.vitgon.httpserver.data.RequestParameters;
import com.vitgon.httpserver.enums.HttpMethod;


public class Request {
	private HttpMethod method;
	private String uri;
	private String httpVersion;
	
	private String sessionId;
	private HttpSession session;
	private Cookies cookies;
	private Headers headers;
	private RequestBody requestBody;
	private RequestParameters parameters;
	private List<Part> parts;
	
	public Request() {
		headers = new Headers();
		cookies = new Cookies();
		parameters = new RequestParameters();
		requestBody = new RequestBody();
		parts = new ArrayList<>();
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
		return headers.getHeader(headerName);
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
	
	public String getParameter(String name) {
		for (RequestParameter parameter : parameters) {
			if (parameter.getName().equals(name)) {
				return parameter.getValue();
			}
		}
		return null;
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

	public Headers getHeaders() {
		return headers;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}

	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> parts) {
		this.parts = parts;
	}
	
	public Part getPart(String name) {
		for (Part part : parts) {
			if (part.getName().equals(name)) {
				return part;
			}
		}
		return null;
	}
}
