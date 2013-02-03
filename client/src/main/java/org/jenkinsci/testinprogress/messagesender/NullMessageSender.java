package org.jenkinsci.testinprogress.messagesender;

import java.io.PrintWriter;

/**
 * {@link MessageSender} that do not send messages
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class NullMessageSender extends MessageSender {

	public NullMessageSender() {
		this.writer = new PrintWriter(new NullWriter());
	}
	
}
