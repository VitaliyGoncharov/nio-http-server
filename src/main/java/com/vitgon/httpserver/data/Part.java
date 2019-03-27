package com.vitgon.httpserver.data;

public class Part {
	private final byte[] partData;
	int headerBlockEndPos;
	private Headers headers;
	private byte[] content;
	
	private String contentType;
	private String name;
	private String filename;
	private int size;

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

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
