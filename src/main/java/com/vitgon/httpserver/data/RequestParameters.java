package com.vitgon.httpserver.data;

import java.util.ArrayList;
import java.util.Collection;


public class RequestParameters extends ArrayList<RequestParameter> {

	private static final long serialVersionUID = 6347368208032883341L;

	public RequestParameters() {
		super();
	}

	public RequestParameters(Collection<? extends RequestParameter> c) {
		super(c);
	}

	public RequestParameters(int initialCapacity) {
		super(initialCapacity);
	}	
}
