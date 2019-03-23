package com.vitgon.httpserver.data;

public class ServerParameters {
	
	private String host;
	private int port;
	private String serverName;
	private int maxPostSize;

	public ServerParameters() {
		super();
	}
	
	public ServerParameters(String host, int port, int maxPostSize, String serverName) {
		this.host = host;
		this.port = port;
		this.maxPostSize = maxPostSize;
		this.serverName = serverName;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getMaxPostSize() {
		return maxPostSize;
	}
	public void setMaxPostSize(int maxPostSize) {
		this.maxPostSize = maxPostSize;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}
