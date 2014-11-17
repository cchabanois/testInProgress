package org.jenkinsci.testinprogress.server.messages;

import java.io.Reader;

public interface ITestMessagesParser {

	public abstract void processTestMessages(Reader reader);

}