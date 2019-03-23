package com.vitgon.httpserver.data;


public class RequestBody {
	private byte[] body;
	
	public RequestBody() {
	}

	public RequestBody(byte[] body) {
		this.body = body;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
}
