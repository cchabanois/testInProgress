package org.jenkinsci.plugins.testinprogress.events;

import org.jenkinsci.plugins.testinprogress.messages.MessageIds;

/**
 * Notification that a failure occurred during a test
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class TestFailedEvent implements ITestEvent {
	private final String testId;
	private final String testName;
	private final String expected;
	private final String actual;
	private final String trace;

	public TestFailedEvent(String testId, String testName,
			String expected, String actual, String trace) {
		this.testId = testId;
		this.testName = testName;
		this.expected = expected;
		this.actual = actual;
		this.trace = trace;
	}

	public String getTestId() {
		return testId;
	}

	public String getTestName() {
		return testName;
	}

	public String getExpected() {
		return expected;
	}

	public String getActual() {
		return actual;
	}

	public String getTrace() {
		return trace;
	}

	public String getType() {
		return "FAILED";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageIds.TEST_FAILED).append(testId).append(",").append(testName);
		sb.append("\n");
		if (expected != null) {
			sb.append(MessageIds.EXPECTED_START).append('\n').append(expected).append('\n')
					.append(MessageIds.EXPECTED_END).append('\n');
		}
		if (actual != null) {
			sb.append(MessageIds.ACTUAL_START).append('\n').append(actual).append('\n')
					.append(MessageIds.ACTUAL_END).append('\n');
		}
		sb.append(MessageIds.TRACE_START).append("\n");
		sb.append(trace);
		sb.append('\n');
		sb.append(MessageIds.TRACE_END);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((actual == null) ? 0 : actual.hashCode());
		result = prime * result
				+ ((expected == null) ? 0 : expected.hashCode());
		result = prime * result + ((testId == null) ? 0 : testId.hashCode());
		result = prime * result
				+ ((testName == null) ? 0 : testName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestFailedEvent other = (TestFailedEvent) obj;
		if (actual == null) {
			if (other.actual != null)
				return false;
		} else if (!actual.equals(other.actual))
			return false;
		if (expected == null) {
			if (other.expected != null)
				return false;
		} else if (!expected.equals(other.expected))
			return false;
		if (testId == null) {
			if (other.testId != null)
				return false;
		} else if (!testId.equals(other.testId))
			return false;
		if (testName == null) {
			if (other.testName != null)
				return false;
		} else if (!testName.equals(other.testName))
			return false;
		return true;
	}

}
