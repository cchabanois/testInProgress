package org.jenkinsci.testinprogress.messagesender;

/**
 * Factory that creates a test messages sender that send messages using TCP.
 * 
 * The port to use is given by the environment variable "TEST_IN_PROGRESS_PORT"
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class SocketMessageSenderFactory implements IMessageSenderFactory {
	private final int port;
	private final String host;
	
	public SocketMessageSenderFactory(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public SocketMessageSenderFactory() {
		String host = System.getProperty("TEST_IN_PROGRESS_HOST");
		if (host == null) {
			host = System.getenv("TEST_IN_PROGRESS_HOST");
		}
		if (host == null) {
			this.host = ""; 
		} else {
			this.host = host;
		}
		String portAsString = System.getProperty("TEST_IN_PROGRESS_PORT"); 
		if (portAsString == null) {
			portAsString = System.getenv("TEST_IN_PROGRESS_PORT");
		}
		if (portAsString == null || portAsString.length() == 0) {
			this.port = -1;
		} else {
			this.port = Integer.parseInt(portAsString);
		}
	}

	public MessageSender getMessageSender() {
		if (port == -1) {
			return new NullMessageSender();
		} else {
			return new SocketMessageSender(host, port);
		}
	}

}
