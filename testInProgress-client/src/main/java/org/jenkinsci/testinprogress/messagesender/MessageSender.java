package org.jenkinsci.testinprogress.messagesender;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Abstract class used to send test messages
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public abstract class MessageSender {
	protected PrintWriter writer;

	public void testRunStarted(int testCount) {
		writer.println(MessageIds.TEST_RUN_START + testCount + " " + "v2");
		writer.flush();
	}

	public void testRunEnded(long elapsedTime) {
		writer.println(MessageIds.TEST_RUN_END + elapsedTime);
		writer.flush();
	}

	public void testTree(String testId, String testName, boolean isSuite,
			int testCount) {
		writer.println(MessageIds.TEST_TREE + testId + "," + testName + ","
				+ Boolean.toString(isSuite) + "," + Integer.toString(testCount));
		writer.flush();
	}

	public void testStarted(String testId, String testName, boolean ignored) {
		writer.println(MessageIds.TEST_START + testId + ","
				+ (ignored ? MessageIds.IGNORED_TEST_PREFIX : "") + testName);
		writer.flush();
	}

	public void testEnded(String testId, String testName, boolean ignored) {
		writer.println(MessageIds.TEST_END + testId + ","
				+ (ignored ? MessageIds.IGNORED_TEST_PREFIX : "") + testName);
		writer.flush();
	}

	public void testFailed(String testId, String testName, String expected,
			String actual, String trace) {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageIds.TEST_FAILED).append(testId).append(",")
				.append(testName);
		sb.append("\n");
		if (expected != null) {
			sb.append(MessageIds.EXPECTED_START).append('\n').append(expected)
					.append('\n').append(MessageIds.EXPECTED_END).append('\n');
		}
		if (actual != null) {
			sb.append(MessageIds.ACTUAL_START).append('\n').append(actual)
					.append('\n').append(MessageIds.ACTUAL_END).append('\n');
		}
		sb.append(MessageIds.TRACE_START).append("\n");
		sb.append(trace);
		sb.append('\n');
		sb.append(MessageIds.TRACE_END);
		writer.println(sb.toString());
		writer.flush();
	}

	public void testError(String testId, String testName, String trace) {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageIds.TEST_ERROR).append(testId).append(",")
				.append(testName);
		sb.append("\n");
		sb.append(MessageIds.TRACE_START).append("\n");
		sb.append(trace);
		sb.append('\n');
		sb.append(MessageIds.TRACE_END);
		writer.println(sb.toString());
		writer.flush();
	}

	public void init() throws IOException {

	}

	public void shutdown() throws IOException {

	}

}
