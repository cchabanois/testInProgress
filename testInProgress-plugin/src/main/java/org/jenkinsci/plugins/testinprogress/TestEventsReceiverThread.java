package org.jenkinsci.plugins.testinprogress;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.jenkinsci.plugins.testinprogress.events.run.RunTestEventsGenerator;
import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEventListener;
import org.jenkinsci.plugins.testinprogress.filters.StackTraceFilter;
import org.jenkinsci.plugins.testinprogress.filters.StackTraceFilterTestRunnerWrapper;
import org.jenkinsci.plugins.testinprogress.messages.ITestRunListener;
import org.jenkinsci.plugins.testinprogress.messages.TestMessagesParser;

/**
 * Thread that receives test messages from an InputStream and add test events to
 * {@link BuildTestResults}. We use the same message format that eclipse uses.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestEventsReceiverThread extends Thread {
	private final InputStream in;
	private final IRunTestEventListener[] listeners;
	private final StackTraceFilter stackTraceFilter;

	public TestEventsReceiverThread(String threadName, InputStream in,
			StackTraceFilter stackTraceFilter, IRunTestEventListener[] listeners) {
		super(threadName);
		this.in = in;
		this.listeners = listeners;
		this.stackTraceFilter = stackTraceFilter;
	}

	public void run() {
		RunTestEventsGenerator eventsGenerator = new RunTestEventsGenerator(listeners);
		StackTraceFilterTestRunnerWrapper wrapper = new StackTraceFilterTestRunnerWrapper(
				eventsGenerator, stackTraceFilter);
		TestMessagesParser parser = new TestMessagesParser(false,
				new ITestRunListener[] { wrapper });
		parser.processTestMessages(new InputStreamReader(in));
	}
}
