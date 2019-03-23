package com.vitgon.httpserver.request;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.vitgon.httpserver.Server;
import com.vitgon.httpserver.data.Cookie;
import com.vitgon.httpserver.data.Header;
import com.vitgon.httpserver.data.RequestBody;
import com.vitgon.httpserver.data.RequestParameter;
import com.vitgon.httpserver.data.RequestParameters;
import com.vitgon.httpserver.enums.HttpMethod;
import com.vitgon.httpserver.exception.RequestTooBigException;

public class RequestParser {
	
	// just \r
	private static final int CARRIAGE_RETURN = 13;
	// just \n
	private static final int NEW_LINE = 10;
	
	public Request parseRequest(SocketChannel client) throws IOException, RequestTooBigException {
		ByteBuffer reqBuff = ByteBuffer.allocateDirect(8192);
		
		int readBytes;
		while ((readBytes = client.read(reqBuff)) != 0) {
			
		}
		byte[] requestData = new byte[8192];
		
		// copy request bytes to requestData array
		for (int i = 0; i < readBytes; i++) {
			requestData[i] = reqBuff.get(i);
		}
		@SuppressWarnings("unused")
		String ourReqStr = new String(requestData);
		
		Request request = new Request();
		parseHeader(requestData, request);
		getBody(requestData, request);
		parseBody(request);
		
		if (request.getHeader("Cookie") != null) {
			parseCookie(request.getHeader("Cookie"), request);
		}
		
		return request;
	}
	
	private void parseHeader(byte[] requestData, Request request) {
		int lastHeaderByteLocation = 0;
		boolean parsedFirstString = false;
		for (int i = lastHeaderByteLocation; i < requestData.length; i++) {
			if (i == 0) {
				continue;
			}
			
			// 10 == \n, 13 == \r
			if ((requestData[i - 1] == CARRIAGE_RETURN) && (requestData[i] == NEW_LINE)) {
				
				String line;
				
				
				line = new String(requestData, lastHeaderByteLocation, i - lastHeaderByteLocation - 1);
				
				// check if we parsed the first line (GET / HTTP/1.1)
				if (!parsedFirstString) {
					parsedFirstString = true;
					parseFirstLine(request, line);
				} else {
					int headerNameEndPosition = line.indexOf(":"); 
					String name = line.substring(0, headerNameEndPosition);
					String value = line.substring(headerNameEndPosition + 2, line.length());
					
					request.getHeaders().add(new Header(name, value));
				}
				
				lastHeaderByteLocation = (i + 1);
				
				// if we reached header end
				if (requestData[i+1] == CARRIAGE_RETURN && requestData[i+2] == NEW_LINE) {
					break;
				}
			}
		}
	}
	
	private void getBody(byte[] requestData, Request request) throws RequestTooBigException {
		String ourReqStr = new String(requestData);
		int bodyStartPosition = 0;
		for (int i = 0; i < requestData.length; i++) {
			if (i == 0) continue;
			if ((requestData[i-1] == CARRIAGE_RETURN && requestData[i] == NEW_LINE) &&
					(requestData[i+1] == CARRIAGE_RETURN && requestData[i+2] == NEW_LINE)) {
				bodyStartPosition = i + 3;
			}
		}
		
		if (bodyStartPosition == 0) return;
		
		int requestBodyLength = requestData.length - bodyStartPosition;
		if (requestBodyLength > Server.getServerParameters().getMaxPostSize()) {
			throw new RequestTooBigException("Request body size exceeded limit!");
		}
		byte[] requestBody = new byte[requestBodyLength];
		System.arraycopy(requestData.clone(), bodyStartPosition, requestBody, 0, requestBodyLength);
		request.setRequestBody(new RequestBody(requestBody));
	}
	
	private void parseFirstLine(Request request, String line) {
		String[] lineParts = line.split(" ");
		request.setMethod(HttpMethod.valueOf(lineParts[0]));
		request.setUri(lineParts[1]);
		request.setHttpVersion(lineParts[2]);
	}
	
	private void parseCookie(String cookiesStr, Request request) {

		// parse cookies
		int cookiePointer = 0;
		for (int i = 0; i < cookiesStr.length(); i++) {
			int curCookieEndPosition = cookiesStr.indexOf(";", i);

			String curCookie;
			if (curCookieEndPosition == -1) {
				curCookie = cookiesStr;
				i = cookiesStr.length();
			} else {
				curCookie = cookiesStr.substring(cookiePointer, curCookieEndPosition);
				i = curCookieEndPosition;
			}

			String[] curCookieNameValueParts = curCookie.split("=");
			String curCookieName = curCookieNameValueParts[0];
			String curCookieValue = curCookieNameValueParts[1];
			
			request.getCookies().add(new Cookie(curCookieName, curCookieValue));
		}
	}
	
	private void parseBody(Request request) {
		if (request.getHeader("Content-Type") != null) {
			String contentType = getContentType(request.getHeader("Content-Type"));
			
			if (contentType.equals("application/x-www-form-urlencoded")) {
				parseUrlEncodedBody(request);
			}
		}
	}
	
	private void parseUrlEncodedBody(Request request) {
		byte[] requestBody = request.getRequestBody().getBody();
		RequestParameters requestParameters = request.getParameters();
		
		// parse parameters (email=admin@gmail.com&password=****)
		// and put them into RequestParameters
		String requestBodyStr = new String(requestBody);
		requestBodyStr = requestBodyStr.trim();
		String[] requestParamsStrArray = requestBodyStr.split("&");
		for (String requestParamStr : requestParamsStrArray) {
			String[] requestParamNameValueArr = requestParamStr.split("=");
			requestParameters.add(new RequestParameter(requestParamNameValueArr[0], requestParamNameValueArr[1]));
		}
		request.setParameters(requestParameters);
	}
	
	private String getContentType(String contentTypeHeader) {
		int contentTypeEndPosition = contentTypeHeader.indexOf(";");

		String contentType = null;
		// in case if Content-Type: text/html; charset=utf-8 
		if (contentTypeEndPosition != -1) {
			contentType = contentTypeHeader.substring(0, contentTypeEndPosition);
		} else {
			contentType = contentTypeHeader;
		}
		return contentType.toLowerCase();
	}
}
