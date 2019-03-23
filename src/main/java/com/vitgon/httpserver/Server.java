package com.vitgon.httpserver;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.vitgon.httpserver.data.ServerParameters;
import com.vitgon.httpserver.enums.HttpMethod;
import com.vitgon.httpserver.request.RequestHandler;

public class Server extends Thread {
	private final static String HOST = "localhost";
	private final static int PORT = 80;
	public final static String SERVER_NAME = "NioHttpServer v0.1";
	public final static int MAX_POST_SIZE = 8_000_000;
	private static ServerParameters serverParameters;
	
	private Engine engine;

	public Server() {
		this(HOST, PORT, MAX_POST_SIZE, SERVER_NAME);
	}
	
	public Server(int port) {
		this(HOST, port, MAX_POST_SIZE, SERVER_NAME);
	}
	
	public Server(String host, int port) {
		this(host, port, MAX_POST_SIZE, SERVER_NAME);
	}
	
	public Server(String host, int port, int maxPostSize, String serverName) {
		serverParameters = new ServerParameters(host, port, maxPostSize, serverName);
		engine = new Engine();
	}
	
	public void addHandler(String uri, HttpMethod method, RequestHandler handler) {
		engine.addHandler(uri, method, handler);
	}

	@Override
	public void run() {
		try {
			ServerSocketChannel server = ServerSocketChannel.open();
			server.socket().bind(new InetSocketAddress(serverParameters.getHost(), serverParameters.getPort()));
			server.configureBlocking(false);

			Selector selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT);
			
			System.out.println("Server started on port: " + serverParameters.getPort());

			while (true) {
				System.out.println("Waiting for available keys!");
				int keysNum = selector.select();

				if (keysNum == 0) {
					continue;
				}

				Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
				while (keysIterator.hasNext()) {
					SelectionKey key = keysIterator.next();

					if (key.isAcceptable()) {
						SocketChannel client = server.accept();
						System.out.printf("[%s]:\t%s\n", client.getRemoteAddress(), "accepted connection");
						client.configureBlocking(false);
						client.register(selector, SelectionKey.OP_READ);
					} else if (key.isReadable()) {
						SocketChannel client = (SocketChannel) key.channel();
						System.out.printf("[%s]:\t%s\n", client.getRemoteAddress(), "READ key available");
						engine.read(key);
					} else if (key.isWritable()) {
						SocketChannel client = (SocketChannel) key.channel();
						System.out.printf("[%s]:\t%s\n", client.getRemoteAddress(), "WRITE key available");
						engine.write(key);
					}

					keysIterator.remove();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void setServerName(String serverName) {
		serverParameters.setServerName(serverName);
	}

	public static ServerParameters getServerParameters() {
		return serverParameters;
	}
}
