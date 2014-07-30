package org.jenkinsci.plugins.testinprogress.filters;

import org.jenkinsci.plugins.testinprogress.messages.ITestRunListener;

/**
 * Wrapper around an {@link ITestRunListener} that filter stacktraces for failed
 * tests
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class StackTraceFilterTestRunnerWrapper implements ITestRunListener {
	private final ITestRunListener testRunListener;
	private final StackTraceFilter stackTraceFilter;

	public StackTraceFilterTestRunnerWrapper(ITestRunListener testRunListener,
			StackTraceFilter stackTraceFilter) {
		this.testRunListener = testRunListener;
		this.stackTraceFilter = stackTraceFilter;
	}

	public void testRunStarted(long timestamp, int testCount, String runId) {
		testRunListener.testRunStarted(timestamp, testCount, runId);
	}

	public void testRunEnded(long timestamp, long elapsedTime, String runId) {
		testRunListener.testRunEnded(timestamp, elapsedTime, runId);
	}

	public void testStarted(long timestamp, String testId, String testName, boolean ignored, String runId) {
		testRunListener.testStarted(timestamp, testId, testName, ignored, runId);
	}

	public void testEnded(long timestamp, String testId, String testName, boolean ignored, String runId) {
		testRunListener.testEnded(timestamp, testId, testName, ignored, runId);
	}

	public void testRunTerminated() {
		testRunListener.testRunTerminated();
	}

	public void testTreeEntry(long timestamp, String testId, String testName,
			String parentId, String parentName, boolean isSuite, int testCount, String runId) {
		testRunListener.testTreeEntry(timestamp, testId, testName, parentId, parentName, isSuite, testCount, runId);
	}

	public void testFailed(long timestamp, int status, String testId,
			String testName, String trace, String expected, String actual, String runId) {
		trace = stackTraceFilter.filter(trace);
		testRunListener.testFailed(timestamp, status, testId, testName, trace,
				expected, actual, runId);
	}

}
