package com.vitgon.httpserver.exception;

public class EmptyRequestException extends Exception {

	private static final long serialVersionUID = -1579093152947306816L;

	public EmptyRequestException() {
		super("Request is empty");
	}

	public EmptyRequestException(String message) {
		super(message);
	}

	public EmptyRequestException(Throwable cause) {
		super(cause);
	}
}
