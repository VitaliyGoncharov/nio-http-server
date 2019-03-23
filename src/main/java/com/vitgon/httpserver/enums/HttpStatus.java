package com.vitgon.httpserver.enums;

public enum HttpStatus {
	OK(200, "OK"),
	NOT_FOUND(404, "Not Found"),
	BAD_REQUEST(400, "Bad Request"),
	INTERNAL_SERVER_ERROR(500, "Internal Server Error");
	
	private final int code;
	private final String phrase;
	
	HttpStatus(int code, String phrase) {
		this.code = code;
		this.phrase = phrase;
	}

	public int getCode() {
		return code;
	}

	public String getPhrase() {
		return phrase;
	}
}
