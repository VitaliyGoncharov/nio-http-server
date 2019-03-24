package com.vitgon.httpserver.request;

public class RequestBodyFactory {
	private byte[] bodyData;
	private int bytesReceived;
	
	public void addBodyData(byte[] bodyBytes) {
		// temp
		if (bodyData.length < bodyBytes.length) {
			bodyData = new byte[bodyBytes.length];
		}
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
