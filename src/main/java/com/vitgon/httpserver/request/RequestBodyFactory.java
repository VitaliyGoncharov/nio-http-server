package com.vitgon.httpserver.request;

public class RequestBodyFactory {
	private byte[] bodyData;
	private int bytesReceived;
	
	public void addBodyData(byte[] bodyBytes) {
		System.arraycopy(bodyBytes, 0, bodyData, bytesReceived, bodyBytes.length);
		bytesReceived = bodyBytes.length;
	}
	
	public byte[] getBodyData() {
		return bodyData;
	}

	public void initialize(int contentLength) {
		bodyData = new byte[contentLength];
	}
}
