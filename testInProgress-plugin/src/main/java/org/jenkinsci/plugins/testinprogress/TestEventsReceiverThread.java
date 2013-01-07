package org.jenkinsci.plugins.testinprogress;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.jenkinsci.plugins.testinprogress.events.EventsGenerator;
import org.jenkinsci.plugins.testinprogress.events.ITestEvent;
import org.jenkinsci.plugins.testinprogress.events.ITestEventListener;
import org.jenkinsci.plugins.testinprogress.messages.ITestRunListener;
import org.jenkinsci.plugins.testinprogress.messages.TestMessagesParser;

/**
 * Thread that receives test messages from an InputStream and add test events to
 * {@link TestEvents}. We use the same message format that eclipse uses.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestEventsReceiverThread extends Thread {
	private final InputStream in;
	private final ITestEventListener[] listeners;

	public TestEventsReceiverThread(String threadName, InputStream in,
			ITestEventListener[] listeners) {
		super(threadName);
		this.in = in;
		this.listeners = listeners;
	}

	public void run() {
		EventsGenerator eventsGenerator = new EventsGenerator("runId", listeners);
		TestMessagesParser parser = new TestMessagesParser(
				new ITestRunListener[] { eventsGenerator });
		parser.processTestMessages(new InputStreamReader(in));
	}

}
