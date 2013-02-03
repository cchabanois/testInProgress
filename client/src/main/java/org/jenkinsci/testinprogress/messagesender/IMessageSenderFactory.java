package org.jenkinsci.testinprogress.messagesender;

/**
 * Factory for {@link MessageSender}
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public interface IMessageSenderFactory {

	public MessageSender getMessageSender();

}
