package org.jenkinsci.plugins.testinprogress.filters;

import org.jenkinsci.plugins.testinprogress.messages.ITestRunListener;

/**
 * 
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class StackTraceFilterTestRunnerWrapper implements ITestRunListener {
	private final ITestRunListener testRunListener;
	private final StackTraceFilter stackTraceFilter;
	
	public StackTraceFilterTestRunnerWrapper(ITestRunListener testRunListener, StackTraceFilter stackTraceFilter) {
		this.testRunListener = testRunListener;
		this.stackTraceFilter = stackTraceFilter;
	}

	public void testRunStarted(long timestamp, int testCount) {
		testRunListener.testRunStarted(timestamp, testCount);
	}

	public void testRunEnded(long timestamp, long elapsedTime) {
		testRunListener.testRunEnded(timestamp, elapsedTime);
	}

	public void testStarted(long timestamp, String testId, String testName) {
		testRunListener.testStarted(timestamp, testId, testName);
	}

	public void testEnded(long timestamp, String testId, String testName) {
		testRunListener.testEnded(timestamp, testId, testName);
	}

	public void testRunTerminated() {
		testRunListener.testRunTerminated();
	}

	public void testTreeEntry(long timestamp, String description) {
		testRunListener.testTreeEntry(timestamp, description);
	}

	public void testFailed(long timestamp, int status, String testId, String testName,
			String trace, String expected, String actual) {
		trace = stackTraceFilter.filter(trace);
		testRunListener.testFailed(timestamp, status, testId, testName, trace, expected,
				actual);
	}

}
