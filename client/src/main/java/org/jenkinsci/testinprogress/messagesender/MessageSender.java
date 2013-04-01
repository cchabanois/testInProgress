package org.jenkinsci.testinprogress.messagesender;

import java.io.IOException;
import java.io.Writer;

/**
 * Abstract class used to send test messages
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public abstract class MessageSender {
	protected Writer writer;

	public void testRunStarted(int testCount) {
		println(MessageIds.TEST_RUN_START + testCount + " " + "v2");
		flush();
	}

	public void testRunEnded(long elapsedTime) {
		println(MessageIds.TEST_RUN_END + elapsedTime);
		flush();
	}

	public void testTree(String testId, String testName, boolean isSuite,
			int testCount) {
		println(MessageIds.TEST_TREE + testId + "," + testName + ","
				+ Boolean.toString(isSuite) + "," + Integer.toString(testCount));
		flush();
	}

	public void testStarted(String testId, String testName, boolean ignored) {
		println(MessageIds.TEST_START + testId + ","
				+ (ignored ? MessageIds.IGNORED_TEST_PREFIX : "") + testName);
		flush();
	}

	public void testEnded(String testId, String testName, boolean ignored) {
		println(MessageIds.TEST_END + testId + ","
				+ (ignored ? MessageIds.IGNORED_TEST_PREFIX : "") + testName);
		flush();
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
		println(sb.toString());
		flush();
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
		println(sb.toString());
		flush();
	}

	protected void println(String str) {
		try {
			writer.write(str);
			writer.write('\n');
		} catch (IOException e) {
			throw new RuntimeException("Could not send message", e);
		}
	}

	protected void flush() { 
		try {
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Could not send message", e);
		}
	}
	
	public void init() throws IOException {

	}

	public void shutdown() throws IOException {

	}

}
