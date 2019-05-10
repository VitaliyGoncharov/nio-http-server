package com.vitgon.httpserver.request;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.vitgon.httpserver.Server;
import com.vitgon.httpserver.data.Cookie;
import com.vitgon.httpserver.data.Header;
import com.vitgon.httpserver.data.Part;
import com.vitgon.httpserver.data.RequestBody;
import com.vitgon.httpserver.data.RequestParameter;
import com.vitgon.httpserver.data.RequestParameters;
import com.vitgon.httpserver.enums.HttpMethod;
import com.vitgon.httpserver.exception.EmptyRequestException;
import com.vitgon.httpserver.exception.RequestTooBigException;
import com.vitgon.httpserver.util.ByteUtil;
import com.vitgon.httpserver.util.FileUtil;

public class RequestProcessor {
	private ByteBuffer tempBuffer = ByteBuffer.allocate(1024);
	private ByteBuffer requestBuffer = ByteBuffer.allocate(Server.getServerParameters().getMaxPostSize());
	
	private int bytesRemain;
	private boolean headerReceived;
	private boolean bodyReceived;
	private int headerBlockLastBytePosition;
	private int contentLength;
	
	private RequestHeaderFactory headerFactory;
	private RequestBodyFactory bodyFactory;
	
	private static final int CARRIAGE_RETURN = 13; // \r
	private static final int NEW_LINE = 10; // \n
	
	public RequestProcessor() {
		headerFactory = new RequestHeaderFactory();
		bodyFactory = new RequestBodyFactory();
	}
	
	public Request readRequest(SelectionKey key) throws IOException, RequestTooBigException, EmptyRequestException {
		SocketChannel client = (SocketChannel) key.channel();
		
		tempBuffer.clear();
		int read = 0;
		while ((read = client.read(tempBuffer)) > 0) {
			tempBuffer.flip();
			byte[] bytes = new byte[tempBuffer.limit()];
			tempBuffer.get(bytes);
			tempBuffer.clear();
			addToRequestBuffer(bytes);
		}
		
		// if it is an empty request, then stop processing
		if (requestBuffer.position() == 0) {
			throw new EmptyRequestException();
		}
		
		if (!headerReceived) {
			parseHeader();
		}
		
		// check for test reason (see variable value in debug)
		if (headerFactory.getHeaderData() != null) {
			String headerStr = new String(headerFactory.getHeaderData());
		}
		
		// if we did not get full header
		if (headerReceived == false) return null;
		
		if (headerFactory.getMethod() == HttpMethod.GET) {
			parseGetParameters();
		}
		
		// if request method is POST we should expect body
		if (shouldExpectBody()) {
			
			// when we get here for the first time, our contentLength is 0
			if (contentLength == 0) {
				contentLength = getContentLength();
				checkContentLength();
				bodyFactory.initialize(contentLength);
				bytesRemain = contentLength;
			}
			
			// if full body was not received
			int receivedInTheLastTime = contentLength - bytesRemain;
			int receivedNow = requestBuffer.position() - headerBlockLastBytePosition - receivedInTheLastTime;
			
			bytesRemain = bytesRemain - receivedNow;
			
			// in case if we received only part of body
			// then return null and start reading new bytes
			if (bytesRemain > 0) {
				return null;
			}
			
			// extract body from request and parse it
			getBody();
			parseBody();
		}
		
		return generateRequest();
	}

	private void parseHeader() {
		byte[] requestData = requestBuffer.array();
		int lastHeaderBytePosition = 0;
		boolean parsedFirstString = false;
		for (int i = 0; i < requestData.length; i++) {
			if (i == 0) {
				continue;
			}
			
			if ((requestData[i - 1] == CARRIAGE_RETURN) && (requestData[i] == NEW_LINE)) {
				
				String line = new String(requestData, lastHeaderBytePosition, i - lastHeaderBytePosition - 1);
				
				// check if we parsed the first line (GET / HTTP/1.1)
				if (!parsedFirstString) {
					parsedFirstString = true;
					parseFirstLine(line);
				} else {
					int headerNameEndPosition = line.indexOf(":"); 
					String name = line.substring(0, headerNameEndPosition);
					String value = line.substring(headerNameEndPosition + 2, line.length());
					
					headerFactory.addHeader(new Header(name, value));
				}
				
				lastHeaderBytePosition = (i + 1);
				
				// if we reached header block end
				if (requestData[i+1] == CARRIAGE_RETURN && requestData[i+2] == NEW_LINE) {
					headerBlockLastBytePosition = i + 2;
					headerReceived = true;
					headerFactory.addHeaderData(Arrays.copyOfRange(requestData, 0, headerBlockLastBytePosition));
					break;
				}
			}
		}
	}
	
	private void parseGetParameters() {
		RequestParameters requestParameters = headerFactory.getParameters();
		String uri = headerFactory.getUri();
		int paramsStartPos = uri.indexOf("?");
		if (paramsStartPos != -1) {
			String paramsStr = uri.substring(paramsStartPos + 1);
			String[] paramsKeyValue = paramsStr.split("&");
			for (String paramKeyValue : paramsKeyValue) {
				String[] paramKeyValueArr = paramKeyValue.split("=");
				requestParameters.add(new RequestParameter(paramKeyValueArr[0], paramKeyValueArr[1]));
			}
		}
	}
	
	private void getBody() throws RequestTooBigException, IOException {
		byte[] requestByteArr = requestBuffer.array();
		byte[] requestBodyPart = new byte[contentLength];
		System.arraycopy(requestByteArr, headerBlockLastBytePosition + 1, requestBodyPart, 0, contentLength);
		bodyFactory.setBodyData(requestBodyPart);
		
		// for test purpose
		FileUtil.saveToFile("src/main/resources/debug/requestBody.txt", requestBodyPart);
		FileUtil.saveToFile("src/main/resources/debug/request.txt", requestByteArr);
	}
	
	private void parseFirstLine(String line) {
		String[] lineParts = line.split(" ");
		headerFactory.setMethod(HttpMethod.valueOf(lineParts[0]));
		headerFactory.setUri(lineParts[1]);
		headerFactory.setHttpVersion(lineParts[2]);
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
	
	private void parseBody() {
		if (headerFactory.getHeader("Content-Type") != null) {
			String contentType = getHeaderFirstValue(headerFactory.getHeader("Content-Type"));
			
			if (contentType.equals("application/x-www-form-urlencoded")) {
				parseUrlEncodedBody();
			} else if (contentType.equals("multipart/form-data")) {
				parseMultipartFormData();
			}
		}
	}
	
	private void parseMultipartFormData() {
		byte[] bodyData = bodyFactory.getBodyData();
		String boundary = headerFactory.getBoundary();
		byte[] delimeterBytes = ("--" + boundary).getBytes(StandardCharsets.UTF_8);
		
		int lastDelimeterStartPos = 0;
		int lastPartEndPos = 0;
		while ((lastDelimeterStartPos = ByteUtil.indexOf(bodyData, lastPartEndPos, delimeterBytes)) != -1) {
			int partStartPosition = lastDelimeterStartPos + delimeterBytes.length + 2;
			int nextDelimeterStartPos = ByteUtil.indexOf(bodyData, partStartPosition, delimeterBytes);
			
			// 2 bytes for \r\n between part content and next boundary
			byte[] partData = Arrays.copyOfRange(bodyData, partStartPosition, nextDelimeterStartPos - 2);
			bodyFactory.addPart(new Part(partData));
			
			// decrease iterations over bodyData while searching
			// for next delimeter from lastPartEndPos
			lastPartEndPos = nextDelimeterStartPos - 2;  
			
			// if we reached requestBody last delimeter (4 = 2 bytes for "--" and 2 bytes for "\r\n")
			// then get out of this loop and stop searching for next delimeter
			if (nextDelimeterStartPos + delimeterBytes.length + 4 == contentLength) break;
		}
		
		parseParts();
	}
	
	private void parseParts() {
		for (Part part : bodyFactory.getParts()) {
			byte[] partData = part.getPartData();
			
			// get part headers
			int lastHeaderEndPos = -1;
			for (int i = 0; i < partData.length; i++) {
				if (i == 0) continue;
				
				if (partData[i - 1] == CARRIAGE_RETURN && partData[i] == NEW_LINE) {
					// plus 1 - because we copy from (including this) position,
					// and we need current header start position that is right
					// after last header end position
					byte[] headerBytes = Arrays.copyOfRange(partData, lastHeaderEndPos + 1, i);
					String headerStr = new String(headerBytes, StandardCharsets.UTF_8).trim();
					String[] headerNameValuePair = headerStr.split(":");
					String headerName = headerNameValuePair[0];
					String headerValue = headerNameValuePair[1].trim();
					part.addHeader(new Header(headerName, headerValue));
					
					// position at \n
					lastHeaderEndPos = i;
					
					if (partData[i + 1] == CARRIAGE_RETURN && partData[i + 2] == NEW_LINE) {
						part.setHeaderBlockEndPos(i + 2);
						break;
					}
				}
			}
			
			// get part body content (3 bytes = 2 bytes for \r\n and 1 byte is for start position of content)
			byte[] partContent = Arrays.copyOfRange(partData, lastHeaderEndPos + 3, partData.length);
			part.setContent(partContent);
			
			extractPartContentType(part);
			countPartSize(part);
			extractPartName(part);
			extractPartFilename(part);
		}
		
		// for test purpose
		List<Part> parts = bodyFactory.getParts();
		for (int i = 0; i < parts.size(); i++) {
			Part curPart = parts.get(i);
			String toFileName;
			
			if (curPart.getFilename() != null && !curPart.getFilename().equals("")){
				toFileName = curPart.getFilename();
			} else if (curPart.getName() != null && !curPart.getName().equals("")) {
				toFileName = "part-" + curPart.getName() + ".txt";
			} else {
				toFileName = "part-" + i + ".txt";
			}
			
			try {
				FileUtil.saveToFile("src/main/resources/debug/part-" + toFileName, curPart.getContent());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void extractPartFilename(Part part) {
		String[] contentDispositionStrArr = part.getHeader("Content-Disposition").split(";");
		
		String filename = null;
		if (contentDispositionStrArr.length == 3) {
			String[] fileNameKeyValueStrArr = contentDispositionStrArr[2].trim().split("=");
			String filenameValue = fileNameKeyValueStrArr[1];
			
			// remove double quotes from filename value
			filename = filenameValue.substring(1, filenameValue.length() - 1);
		}
		part.setFilename(filename);
	}

	private void extractPartName(Part part) {
		String[] contentDispositionStrArr = part.getHeader("Content-Disposition").split(";");
		
		String partName = null;
		if (contentDispositionStrArr.length >= 2) {
			String[] partNameKeyValueStrArr = contentDispositionStrArr[1].trim().split("=");
			String partNameValue = partNameKeyValueStrArr[1];
			
			// remove double quotes from part name value
			partName = partNameValue.substring(1, partNameValue.length() - 1);
		}
		part.setName(partName);
	}

	private void countPartSize(Part part) {
		part.setSize(part.getPartData().length);
	}

	private void extractPartContentType(Part part) {
		String contentType = part.getHeader("Content-Type");
		
		if (contentType != null) {
			part.setContentType(contentType);
		}
	}

	private void parseUrlEncodedBody() {
		byte[] requestBody = bodyFactory.getBodyData();
		RequestParameters requestParameters = headerFactory.getParameters();
		
		// parse parameters (email=admin@gmail.com&password=****)
		// and put them into RequestParameters
		String requestBodyStr = new String(requestBody);
		requestBodyStr = requestBodyStr.trim();
		String[] requestParamsStrArray = requestBodyStr.split("&");
		for (String requestParamStr : requestParamsStrArray) {
			String[] requestParamNameValueArr = requestParamStr.split("=");
			requestParameters.add(new RequestParameter(requestParamNameValueArr[0], requestParamNameValueArr[1]));
		}
		headerFactory.setParameters(requestParameters);
	}
	
	private String getHeaderFirstValue(String header) {
		int colon = header.indexOf(";");

		String firstValue = null;
		// in case if Content-Type: text/html; charset=utf-8 
		if (colon != -1) {
			firstValue = header.substring(0, colon);
		} else {
			firstValue = header;
		}
		return firstValue.toLowerCase();
	}
	
	private int getContentLength() {
		String contentLengthStr = getHeaderFirstValue(headerFactory.getHeader("Content-Length"));
		int contentLengthInt;
		try {
			contentLengthInt = Integer.parseInt(contentLengthStr);
		} catch (Exception e) {
			return 0;
		}
		return contentLengthInt;
	}
	
	public Request generateRequest() {
		Request request = new Request();
		request.setMethod(headerFactory.getMethod());
		request.setParameters(headerFactory.getParameters());
		request.setHttpVersion(headerFactory.getHttpVersion());
		request.setRequestBody(new RequestBody(bodyFactory.getBodyData()));
		request.setHeaders(headerFactory.getHeaders());
		
		if (request.getHeader("Cookie") != null) {
			parseCookie(request.getHeader("Cookie"), request);
		}
		request.setUri(headerFactory.getUri());
		request.setParts(bodyFactory.getParts());
		
		return request;
	}
	
	private void addToRequestBuffer(byte[] requestPartBytes) {
		this.requestBuffer.put(requestPartBytes);
	}
	
	private boolean shouldExpectBody() {
		if (headerFactory.getMethod() == HttpMethod.POST) {
			return true;
		}
		return false;
	}
	
	private void checkContentLength() throws RequestTooBigException {
		if (contentLength > Server.getServerParameters().getMaxPostSize()) {
			throw new RequestTooBigException("Request body size exceeded limit!");
		}
	}
}