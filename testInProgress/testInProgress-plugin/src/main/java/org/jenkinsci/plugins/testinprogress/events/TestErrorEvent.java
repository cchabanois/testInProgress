package org.jenkinsci.plugins.testinprogress.events;

import org.jenkinsci.plugins.testinprogress.messages.MessageIds;

/**
 * Notification that an error occured during a test
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class TestErrorEvent extends AbstractTestEvent {
	private final String testId;
	private final String testName;
	private final String trace;

	public TestErrorEvent(String runId, String testId, String testName,
			String trace) {
		super(runId);
		this.testId = testId;
		this.testName = testName;
		this.trace = trace;
	}

	public String getTestId() {
		return testId;
	}

	public String getTestName() {
		return testName;
	}

	public String getTrace() {
		return trace;
	}

	public String getType() {
		return "ERROR";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageIds.TEST_ERROR).append(testId).append(",").append(testName);
		sb.append("\n");
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
		TestErrorEvent other = (TestErrorEvent) obj;
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
