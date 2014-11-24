package org.jenkinsci.testinprogress.server.events.run;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jenkinsci.testinprogress.server.messages.ITestRunListener;


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

	public void testRunStarted(long timestamp, String runId) {
		fireEvent(new RunStartEvent(timestamp, runId));
	}

	public void testRunEnded(long timestamp, long elapsedTime) {
		fireEvent(new RunEndEvent(timestamp,elapsedTime));
	}

	public void testStarted(long timestamp,String testId, String testName, boolean ignored) {
		TestStartEvent testStartEvent = new TestStartEvent(timestamp,testId, testName, ignored);
		runningTests.put(testId, testStartEvent);
		fireEvent(testStartEvent);
	}

	public void testEnded(long timestamp,String testId, String testName, boolean ignored) {
		TestStartEvent testStartEvent = runningTests.remove(testId);
		long timeElapsed = 0;
		if (testStartEvent != null) {
			timeElapsed = timestamp-testStartEvent.getTimestamp();
		}
		fireEvent(new TestEndEvent(timestamp,testId, testName, ignored, timeElapsed));
	}

	public void testRunTerminated() {

	}

	public void testTreeEntry(long timestamp, String testId, String testName,
			String parentId, boolean isSuite) {
		fireEvent(new TestTreeEvent(timestamp,testId, testName, parentId, isSuite));
	}

	public void testFailed(long timestamp,int status, String testId, String testName,
			String trace, String expected, String actual, boolean assumptionFailed) {	
		if (status == ITestRunListener.STATUS_FAILURE) {
			fireEvent(new TestFailedEvent(timestamp,testId, testName, expected,
					actual, trace, assumptionFailed));
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
