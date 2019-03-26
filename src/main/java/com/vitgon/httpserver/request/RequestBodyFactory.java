package com.vitgon.httpserver.request;

import java.util.ArrayList;
import java.util.List;

import com.vitgon.httpserver.data.Part;

public class RequestBodyFactory {
	private byte[] bodyData;
	private int bytesReceived;
	
	private List<Part> parts;
	
	public RequestBodyFactory() {
		parts = new ArrayList<>();
	}

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
	
	public void addPart(Part part) {
		parts.add(part);
	}

	public List<Part> getParts() {
		return parts;
	}
}
