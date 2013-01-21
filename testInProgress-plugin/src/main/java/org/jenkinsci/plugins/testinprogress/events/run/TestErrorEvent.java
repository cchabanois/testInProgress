package org.jenkinsci.plugins.testinprogress.events.run;

import org.jenkinsci.plugins.testinprogress.messages.MessageIds;

/**
 * Notification that an error occured during a test
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class TestErrorEvent extends AbstractRunTestEvent {
	private final String testId;
	private final String testName;
	private final String trace;

	public TestErrorEvent(long timestamp, String testId, String testName,
			String trace) {
		super(timestamp);
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

	public String toString(boolean includeTimeStamp) {
		StringBuilder sb = new StringBuilder();
		if (includeTimeStamp) {
			sb.append(Long.toString(getTimestamp())).append(' ');
		}
		sb.append(MessageIds.TEST_ERROR).append(testId).append(",").append(testName);
		sb.append("\n");
		sb.append(MessageIds.TRACE_START).append("\n");
		sb.append(trace);
		sb.append('\n');
		sb.append(MessageIds.TRACE_END);
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return toString(true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((testId == null) ? 0 : testId.hashCode());
		result = prime * result
				+ ((testName == null) ? 0 : testName.hashCode());
		result = prime * result + ((trace == null) ? 0 : trace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
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
		if (trace == null) {
			if (other.trace != null)
				return false;
		} else if (!trace.equals(other.trace))
			return false;
		return true;
	}

	

}
