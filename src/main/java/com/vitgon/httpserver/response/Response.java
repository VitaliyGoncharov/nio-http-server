package com.vitgon.httpserver.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import com.vitgon.httpserver.FileUtil;
import com.vitgon.httpserver.Server;
import com.vitgon.httpserver.data.Cookie;
import com.vitgon.httpserver.data.Cookies;
import com.vitgon.httpserver.data.Header;
import com.vitgon.httpserver.enums.HttpStatus;

public class Response {
	private final static String NEW_LINE = "\r\n";
	
	private ByteArrayOutputStream responseBody;
	private HttpStatus status;
	private String server;
	
	private Cookies cookies;
	private Set<Header> headers;
	private String responsePage;
	
	public Response() {
		headers = new HashSet<>();
		responseBody = new ByteArrayOutputStream();
		cookies = new Cookies();
	}
	
	public Response(Cookies reqCookies, String sessionId) {
		headers = new HashSet<>();
		responseBody = new ByteArrayOutputStream();
		
		if (cookies != null) {
			this.cookies = reqCookies;
			
			// if we created session for the first time, we need to add its id to response cookies
			if (sessionId != null && cookies.get("sessionid") == null) {
				cookies.add(new Cookie("sessionid", sessionId));
			}
		} else {
			cookies = new Cookies();
		}
	}

	public byte[] createResponse(HttpStatus status) {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		StringBuilder responseHeaderBuilder = new StringBuilder();
		
		responseHeaderBuilder.append("HTTP/1.1 " + status.getCode() + " " + status.getPhrase() + NEW_LINE);
		responseHeaderBuilder.append("Server: " + Server.getServerParameters().getServerName() + NEW_LINE);
		responseHeaderBuilder.append("Connection: close" + NEW_LINE);
		
		// set headers
		if (!headers.isEmpty()) {
			responseHeaderBuilder.append(createHeadersString());
		}
		
		// set cookie header
		if (!cookies.isEmpty()) {
			responseHeaderBuilder.append(createCookiesHeaderString());
		}
		
		responseHeaderBuilder.append(NEW_LINE);
		
		// if responseBody is empty and user set responsePage
		if (responseBody.size() == 0 && responsePage != null) {
			try {
				byte[] fileBytes = Files.readAllBytes(Paths.get("src/main/resources/templates/"+responsePage));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// merge response header and response body
		try {
			responseStream.write(responseHeaderBuilder.toString().getBytes(StandardCharsets.UTF_8));
			responseStream.write(responseBody.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return responseStream.toByteArray();
	}
	
	private String createHeadersString() {
		StringBuilder headersStrBuilder = new StringBuilder();
		for (Header header : headers) {
			headersStrBuilder.append(header.getName() + ": " + header.getValue() + NEW_LINE);
		}
		return headersStrBuilder.toString();
	}
	
	private String createCookiesHeaderString() {
		StringBuilder cookieStrBuilder = new StringBuilder();
		for (Cookie cookie : cookies.getAll()) {
			cookieStrBuilder.append(cookie.getName() + "=" + cookie.getValue() + "; ");
		}
		cookieStrBuilder.append("HttpOnly; ");
		cookieStrBuilder.append("Path=/");
		return "Set-Cookie: " + cookieStrBuilder.toString() + NEW_LINE;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public Set<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(Set<Header> headers) {
		this.headers = headers;
	}
	
	public ByteArrayOutputStream getResponseBody() {
		return responseBody;
	}
	
	public void setResponseBody(String responseBodyStr) {
		this.responseBody.reset();
		try {
			this.responseBody.write(responseBodyStr.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setResponseBody(byte[] responseBody) {
		try {
			this.responseBody.write(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		};
	}

	public void setResponseBody(ByteArrayOutputStream responseBody) {
		this.responseBody = responseBody;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public Cookies getCookies() {
		return cookies;
	}

	public void setCookies(Cookies cookies) {
		this.cookies = cookies;
	}

	public String getResponsePage() {
		return responsePage;
	}

	public void setResponsePage(String responsePage) {
		this.responsePage = responsePage;
	}
	
	public byte[] readResponseFile() throws IOException {
		return FileUtil.readFile("templates/" + responsePage);
	}
}
