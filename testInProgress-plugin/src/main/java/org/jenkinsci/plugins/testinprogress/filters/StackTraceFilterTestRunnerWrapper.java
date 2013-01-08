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

	public void testRunStarted(int testCount) {
		testRunListener.testRunStarted(testCount);
	}

	public void testRunEnded(long elapsedTime) {
		testRunListener.testRunEnded(elapsedTime);
	}

	public void testStarted(String testId, String testName) {
		testRunListener.testStarted(testId, testName);
	}

	public void testEnded(String testId, String testName) {
		testRunListener.testEnded(testId, testName);
	}

	public void testRunTerminated() {
		testRunListener.testRunTerminated();
	}

	public void testTreeEntry(String description) {
		testRunListener.testTreeEntry(description);
	}

	public void testFailed(int status, String testId, String testName,
			String trace, String expected, String actual) {
		trace = stackTraceFilter.filter(trace);
		testRunListener.testFailed(status, testId, testName, trace, expected,
				actual);
	}

}
