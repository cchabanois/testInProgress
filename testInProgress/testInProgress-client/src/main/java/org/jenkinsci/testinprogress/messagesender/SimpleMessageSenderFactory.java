package org.jenkinsci.testinprogress.messagesender;

import java.io.PrintWriter;

/**
 * Creates {@link MessageSender} that send test message to a {@link PrintWriter}
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class SimpleMessageSenderFactory implements IMessageSenderFactory {
	private PrintWriter writer;

	public SimpleMessageSenderFactory(PrintWriter writer) {
		this.writer = writer;
	}

	public MessageSender getMessageSender() {
		return new SimpleMessageSender(writer);
	}

	private static class SimpleMessageSender extends MessageSender {

		public SimpleMessageSender(PrintWriter pw) {
			this.writer = pw;
		}

	}

}
