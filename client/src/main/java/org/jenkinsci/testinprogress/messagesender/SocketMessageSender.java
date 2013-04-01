package org.jenkinsci.testinprogress.messagesender;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**
 * Send test messages using socket
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class SocketMessageSender extends MessageSender {
	private final String host;
	private final int port;
	private Socket socket;

	public SocketMessageSender(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void init() throws IOException {
		socket = new Socket(host, port);
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void shutdown() throws IOException {
		if (writer != null) {
			writer.close();
			writer = null;
		}
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}
}
