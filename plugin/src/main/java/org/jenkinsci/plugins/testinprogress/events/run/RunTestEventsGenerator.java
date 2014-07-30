package org.jenkinsci.plugins.testinprogress.events.run;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jenkinsci.plugins.testinprogress.messages.ITestRunListener;


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

	public void testRunStarted(long timestamp, int testCount, String runId) {
		fireEvent(new RunStartEvent(timestamp,testCount,runId));
	}

	public void testRunEnded(long timestamp, long elapsedTime, String runId) {
		fireEvent(new RunEndEvent(timestamp,elapsedTime, runId));
	}

	public void testStarted(long timestamp,String testId, String testName, boolean ignored, String runId ) {
		TestStartEvent testStartEvent = new TestStartEvent(timestamp,testId, testName, ignored, runId);
		runningTests.put(testId, testStartEvent);
		fireEvent(testStartEvent);
	}

	public void testEnded(long timestamp,String testId, String testName, boolean ignored, String runId) {
		TestStartEvent testStartEvent = runningTests.remove(testId);
		long timeElapsed = 0;
		if (testStartEvent != null) {
			timeElapsed = timestamp-testStartEvent.getTimestamp();
		}
		fireEvent(new TestEndEvent(timestamp,testId, testName, ignored, timeElapsed, runId));
	}

	public void testRunTerminated() {

	}

	public void testTreeEntry(long timestamp, String testId, String testName,
			String parentId, String parentName, boolean isSuite, int testCount, String runId) {
		fireEvent(new TestTreeEvent(timestamp,testId, testName, parentId, parentName, isSuite, testCount, runId));
	}

	public void testFailed(long timestamp,int status, String testId, String testName,
			String trace, String expected, String actual, String runId) {
		boolean assumptionFailed = false;
		if(expected!="" && actual!="")
			assumptionFailed = true;
		
		if (status == ITestRunListener.STATUS_FAILURE) {
			fireEvent(new TestFailedEvent(timestamp,testId, testName, expected,
					actual, trace, assumptionFailed, runId));
		} else {
			fireEvent(new TestErrorEvent(timestamp,testId, testName, trace, runId));
		}
	}

	private void fireEvent(IRunTestEvent testEvent) {
		for (IRunTestEventListener listener : listeners) {
			listener.event(testEvent);
		}
	}

}
