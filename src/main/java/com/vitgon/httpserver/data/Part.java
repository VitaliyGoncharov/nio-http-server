package com.vitgon.httpserver.data;

public class Part {
	private final byte[] partData;

	public Part(byte[] partData) {
		this.partData = partData;
	}

	public byte[] getPartData() {
		return partData;
	}
}
