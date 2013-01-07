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

	public SocketMessageSenderFactory(int port) {
		this.port = port;
	}

	public SocketMessageSenderFactory() {
		String portAsString = System.getenv("TEST_IN_PROGRESS_PORT");
		if (portAsString == null) {
			this.port = -1;
		} else {
			this.port = Integer.parseInt(portAsString);
		}
	}

	public MessageSender getMessageSender() {
		if (port == -1) {
			return new NullMessageSender();
		} else {
			return new SocketMessageSender("", port);
		}
	}

}
