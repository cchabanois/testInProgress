package org.jenkinsci.testinprogress.messagesender;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

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
		connect(500);
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private void connect(long timeout) throws UnknownHostException, IOException {
		socket = null;
		long time1 = System.currentTimeMillis();
		do {
			try {
				socket = new Socket(host, port);
			} catch (ConnectException e) {
				// wait for the server to listen a little bit 
				long time2 = System.currentTimeMillis(); 
				if ( time2-time1 > timeout) {
					throw e;
				} else {
					try {
						Thread.sleep(50);
					} catch (InterruptedException ie) {
					}
				}
			}
		} while (socket == null);
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
