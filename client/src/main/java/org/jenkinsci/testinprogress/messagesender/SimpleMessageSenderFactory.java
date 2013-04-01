package org.jenkinsci.testinprogress.messagesender;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * Creates {@link MessageSender} that send test message to a {@link PrintWriter}
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class SimpleMessageSenderFactory implements IMessageSenderFactory {
	private Writer writer;

	public SimpleMessageSenderFactory(Writer writer) {
		this.writer = writer;
	}

	public MessageSender getMessageSender() {
		return new SimpleMessageSender(writer);
	}

	private static class SimpleMessageSender extends MessageSender {

		public SimpleMessageSender(Writer pw) {
			this.writer = pw;
		}

	}

}
