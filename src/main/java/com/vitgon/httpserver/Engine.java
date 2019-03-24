package com.vitgon.httpserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.vitgon.httpserver.data.HttpSession;
import com.vitgon.httpserver.enums.HttpMethod;
import com.vitgon.httpserver.enums.HttpStatus;
import com.vitgon.httpserver.handler.HandlerMount;
import com.vitgon.httpserver.request.Request;
import com.vitgon.httpserver.request.RequestHandler;
import com.vitgon.httpserver.request.RequestProcessor;
import com.vitgon.httpserver.response.Response;
import com.vitgon.httpserver.util.FileUtil;

public class Engine {
	private Map<HandlerMount, RequestHandler> handlers = new HashMap<>();
	private Map<String, HttpSession> sessions = new HashMap<>();
	private RequestProcessor requestProcessor;
	
	public Engine() {
	}
	
	public void read(SelectionKey key) {
		
		requestProcessor = (RequestProcessor) key.attachment();
		Request request;
		
		try {
			if (requestProcessor == null) {
				requestProcessor = new RequestProcessor();
			}
			
			request = requestProcessor.readRequest(key);
			
			if (request == null) {
				key.attach(requestProcessor);
				return;
			}
			
			if (request.getHeader("Cookie") == null) {
				String newSessionId = UUID.randomUUID().toString();
				HttpSession newSession = new HttpSession();
				newSession.setSessionMap(new HashMap<String, String>());

				sessions.put(newSessionId, newSession);
				request.setSession(newSession);
				request.setSessionId(newSessionId);
			} else {
				if (request.getCookies().get("sessionid") != null) {
					HttpSession session = sessions.get(request.getCookies().get("sessionid"));

					if (session != null) {
						request.setSession(session);
						request.setSessionId(request.getCookies().get("sessionid"));
					}
				}
			}

			key.attach(requestProcessor);
			key.interestOps(SelectionKey.OP_WRITE);
		} catch (Exception e) {
			e.printStackTrace();
			key.interestOps(SelectionKey.OP_WRITE);
			sendResponseError(key);
		}
	}
	
	public void write(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		Request request = (Request) key.attachment();
		
		RequestHandler handler = null;
		for (HandlerMount mount : handlers.keySet()) {
			if (mount.getMethod().equals(request.getMethod()) && mount.getUri().equals(request.getUri())) {
				handler = handlers.get(mount);
			}
		}
		
		Response response = new Response(request.getCookies(), request.getSessionId());
		byte[] responseBytes;
		if (handler == null) {
			int paramsStartPosition = request.getUri().indexOf("?");
			String requestedStaticFile;
			if (paramsStartPosition != -1) {
				requestedStaticFile = request.getUri().substring(1, paramsStartPosition);
			} else {
				requestedStaticFile = request.getUri().substring(1);
			}
			
			// if requested static file exists, then read it
			if (FileUtil.fileExists("static/" + requestedStaticFile)) {
				byte[] requestedFileBytes = FileUtil.readFile("static/" + requestedStaticFile);
				response.setResponseBody(requestedFileBytes);
				responseBytes = response.createResponse(HttpStatus.OK);
			} else {
				response.setResponseBody("<b>Page was not found!</b>");
				responseBytes = response.createResponse(HttpStatus.NOT_FOUND);
			}
		} else {
			handler.handle(request, response);
			responseBytes = response.createResponse(HttpStatus.OK);
		}
		
		ByteBuffer responseBuff = ByteBuffer.wrap(responseBytes);
		client.write(responseBuff);
		client.close();
	}
	
	private void sendResponseError(SelectionKey key) {
		SocketChannel client = (SocketChannel) key.channel();
		Request request = (Request) key.attachment();
		
		Response response = new Response();
		response.setResponseBody("<b>Oops! Internal server error.</b>");
		byte[] responseBytes = response.createResponse(HttpStatus.INTERNAL_SERVER_ERROR);
		
		ByteBuffer responseBuff = ByteBuffer.wrap(responseBytes);
		try {
			client.write(responseBuff);
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addHandler(String uri, HttpMethod method, RequestHandler handler) {
		handlers.put(new HandlerMount(uri, method), handler);
	}
}