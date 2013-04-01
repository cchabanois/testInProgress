package org.jenkinsci.testinprogress.messagesender;


/**
 * {@link MessageSender} that do not send messages
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class NullMessageSender extends MessageSender {

	public NullMessageSender() {
		this.writer = new NullWriter();
	}
	
}
