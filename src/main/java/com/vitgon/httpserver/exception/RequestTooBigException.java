package com.vitgon.httpserver.exception;

public class RequestTooBigException extends Exception {

	private static final long serialVersionUID = -6064666237856695621L;

	public RequestTooBigException() {
		super();
	}

	public RequestTooBigException(String message) {
		super(message);
	}

	public RequestTooBigException(Throwable cause) {
		super(cause);
	}
}
