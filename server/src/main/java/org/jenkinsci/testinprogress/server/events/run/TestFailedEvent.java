package org.jenkinsci.testinprogress.server.events.run;

import org.jenkinsci.testinprogress.server.messages.MessageIds;
import org.json.JSONObject;

/**
 * Notification that a failure occurred during a test
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestFailedEvent extends AbstractRunTestEvent {
	private final String testId;
	private final String testName;
	private final String expected;
	private final String actual;
	private final String trace;
	private final boolean assumptionFailed;

	public TestFailedEvent(long timestamp, String testId, String testName,
			String expected, String actual, String trace,
			boolean assumptionFailed, String runId) {
		super(timestamp, runId);
		this.testId = testId;
		this.testName = testName;
		this.expected = expected;
		this.actual = actual;
		this.trace = trace;
		this.assumptionFailed = assumptionFailed;
	}
	
	public TestFailedEvent(long timestamp, String testId, String testName,
			String expected, String actual, String trace,
			boolean assumptionFailed) {
		this(timestamp, testId, testName, expected, actual, trace, assumptionFailed, "");
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

	public boolean isAssumptionFailed() {
		return assumptionFailed;
	}
	
	public String toString(boolean includeTimeStamp) {
		JSONObject jsonMsg = new JSONObject();
		String timeStamp ="";
		if(includeTimeStamp){
			timeStamp = Long.toString(getTimestamp());			
		}
		jsonMsg.put("timeStamp", timeStamp);
		jsonMsg.put("messageId", MessageIds.TEST_FAILED);
		jsonMsg.put("runId", getRunId());
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);

		jsonMsg.put("errorTrace",trace.concat("\n"));
		jsonMsg.put("expectedMsg", expected.concat("\n"));
		jsonMsg.put("actualMsg", actual.concat("\n"));
		
		return jsonMsg.toString();
	}

	@Override
	public String toString() {
		return toString(true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((actual == null) ? 0 : actual.hashCode());
		result = prime * result + (assumptionFailed ? 1231 : 1237);
		result = prime * result
				+ ((expected == null) ? 0 : expected.hashCode());
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
		if (assumptionFailed != other.assumptionFailed)
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
		if (trace == null) {
			if (other.trace != null)
				return false;
		} else if (!trace.equals(other.trace))
			return false;
		return true;
	}


}
