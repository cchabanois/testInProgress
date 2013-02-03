package org.jenkinsci.plugins.testinprogress.events.run;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jenkinsci.plugins.testinprogress.messages.ITestRunListener;
import org.jenkinsci.plugins.testinprogress.messages.MessageIds;

import com.google.common.base.Splitter;

/**
 * Generates events from calls to {@link ITestRunListener}
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class RunTestEventsGenerator implements ITestRunListener {
	private final IRunTestEventListener[] listeners;
	private final ConcurrentMap<String, TestStartEvent> runningTests = new ConcurrentHashMap<String, TestStartEvent>(); 
	
	public RunTestEventsGenerator(IRunTestEventListener[] listeners) {
		this.listeners = listeners;
	}

	public void testRunStarted(long timestamp, int testCount) {
		fireEvent(new RunStartEvent(timestamp,testCount));
	}

	public void testRunEnded(long timestamp, long elapsedTime) {
		fireEvent(new RunEndEvent(timestamp,elapsedTime));
	}

	public void testStarted(long timestamp,String testId, String testName) {
		boolean ignored = false;
		if (testName.startsWith(MessageIds.IGNORED_TEST_PREFIX)) {
			ignored = true;
			testName = testName.substring(MessageIds.IGNORED_TEST_PREFIX
					.length());
		}
		TestStartEvent testStartEvent = new TestStartEvent(timestamp,testId, testName, ignored);
		runningTests.put(testId, testStartEvent);
		fireEvent(testStartEvent);
	}

	public void testEnded(long timestamp,String testId, String testName) {
		boolean ignored = false;
		if (testName.startsWith(MessageIds.IGNORED_TEST_PREFIX)) {
			ignored = true;
			testName = testName.substring(MessageIds.IGNORED_TEST_PREFIX
					.length());
		}
		TestStartEvent testStartEvent = runningTests.remove(testId);
		long timeElapsed = 0;
		if (testStartEvent != null) {
			timeElapsed = timestamp-testStartEvent.getTimestamp();
		}
		fireEvent(new TestEndEvent(timestamp,testId, testName, ignored, timeElapsed));
	}

	public void testRunTerminated() {

	}

	public void testTreeEntry(long timestamp,String description) {
		Iterator<String> it = Splitter.on(',').split(description).iterator();
		String testId = it.next();
		String testName = it.next();
		boolean isSuite = Boolean.parseBoolean(it.next());
		int testCount = Integer.parseInt(it.next());
		fireEvent(new TestTreeEvent(timestamp,testId, testName, isSuite, testCount));
	}

	public void testFailed(long timestamp,int status, String testId, String testName,
			String trace, String expected, String actual) {
		if (status == ITestRunListener.STATUS_FAILURE) {
			fireEvent(new TestFailedEvent(timestamp,testId, testName, expected,
					actual, trace));
		} else {
			fireEvent(new TestErrorEvent(timestamp,testId, testName, trace));
		}
	}

	private void fireEvent(IRunTestEvent testEvent) {
		for (IRunTestEventListener listener : listeners) {
			listener.event(testEvent);
		}
	}

}
