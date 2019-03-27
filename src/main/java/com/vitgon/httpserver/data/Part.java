package com.vitgon.httpserver.data;

public class Part {
	private final byte[] partData;
	int headerBlockEndPos;
	private Headers headers;
	private byte[] content;

	public Part(byte[] partData) {
		this.partData = partData;
		this.headers = new Headers();
	}

	public byte[] getPartData() {
		return partData;
	}

	public Headers getHeaders() {
		return headers;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public void addHeader(Header header) {
		headers.addHeader(header);
	}
	
	public String getHeader(String headerName) {
		return headers.getHeader(headerName);
	}

	public int getHeaderBlockEndPos() {
		return headerBlockEndPos;
	}

	public void setHeaderBlockEndPos(int headerBlockEndPos) {
		this.headerBlockEndPos = headerBlockEndPos;
	}
}
