package org.jenkinsci.testinprogress.server.events;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.jenkinsci.testinprogress.server.build.BuildTestResults;
import org.jenkinsci.testinprogress.server.events.run.IRunTestEventListener;
import org.jenkinsci.testinprogress.server.events.run.RunTestEventsGenerator;
import org.jenkinsci.testinprogress.server.filters.StackTraceFilter;
import org.jenkinsci.testinprogress.server.filters.StackTraceFilterTestRunnerWrapper;
import org.jenkinsci.testinprogress.server.messages.ITestRunListener;
import org.jenkinsci.testinprogress.server.messages.TestMessagesParser;

/**
 * Receives test messages from an InputStream and add test events to
 * {@link BuildTestResults}. We use the same message format that eclipse uses.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestEventsReceiver implements Runnable {
	private final InputStream in;
	private final IRunTestEventListener[] listeners;
	private final StackTraceFilter stackTraceFilter;

	public TestEventsReceiver(InputStream in,
			StackTraceFilter stackTraceFilter, IRunTestEventListener[] listeners) {
		this.in = in;
		this.listeners = listeners;
		this.stackTraceFilter = stackTraceFilter;
	}

	public void run() {
		RunTestEventsGenerator eventsGenerator = new RunTestEventsGenerator(
				listeners);
		StackTraceFilterTestRunnerWrapper wrapper = new StackTraceFilterTestRunnerWrapper(
				eventsGenerator, stackTraceFilter);
		TestMessagesParser parser = new TestMessagesParser(
				new ITestRunListener[] { wrapper });
		parser.processTestMessages(new InputStreamReader(in));
	}
}
