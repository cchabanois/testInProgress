package org.jenkinsci.plugins.testinprogress.events;

import java.util.Iterator;

import org.jenkinsci.plugins.testinprogress.messages.ITestRunListener;
import org.jenkinsci.plugins.testinprogress.messages.MessageIds;

import com.google.common.base.Splitter;

/**
 * Generates events from calls to {@link ITestRunListener}
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class EventsGenerator implements ITestRunListener {
	private final ITestEventListener[] listeners;

	public EventsGenerator(ITestEventListener[] listeners) {
		this.listeners = listeners;
	}

	public void testRunStarted(int testCount) {
		fireEvent(new RunStartEvent(testCount));
	}

	public void testRunEnded(long elapsedTime) {
		fireEvent(new RunEndEvent(elapsedTime));
	}

	public void testStarted(String testId, String testName) {
		boolean ignored = false;
		if (testName.startsWith(MessageIds.IGNORED_TEST_PREFIX)) {
			ignored = true;
			testName = testName.substring(MessageIds.IGNORED_TEST_PREFIX
					.length());
		}
		fireEvent(new TestStartEvent(testId, testName, ignored));
	}

	public void testEnded(String testId, String testName) {
		boolean ignored = false;
		if (testName.startsWith(MessageIds.IGNORED_TEST_PREFIX)) {
			ignored = true;
			testName = testName.substring(MessageIds.IGNORED_TEST_PREFIX
					.length());
		}
		fireEvent(new TestEndEvent(testId, testName, ignored));
	}

	public void testRunTerminated() {

	}

	public void testTreeEntry(String description) {
		Iterator<String> it = Splitter.on(',').split(description).iterator();
		String testId = it.next();
		String testName = it.next();
		boolean isSuite = Boolean.parseBoolean(it.next());
		int testCount = Integer.parseInt(it.next());
		fireEvent(new TestTreeEvent(testId, testName, isSuite, testCount));
	}

	public void testFailed(int status, String testId, String testName,
			String trace, String expected, String actual) {
		if (status == ITestRunListener.STATUS_FAILURE) {
			fireEvent(new TestFailedEvent(testId, testName, expected,
					actual, trace));
		} else {
			fireEvent(new TestErrorEvent(testId, testName, trace));
		}
	}

	private void fireEvent(ITestEvent testEvent) {
		for (ITestEventListener listener : listeners) {
			listener.event(testEvent);
		}
	}

}
