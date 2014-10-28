package org.jenkinsci.testinprogress.server.events.run;

import org.jenkinsci.testinprogress.server.messages.MessageIds;
import org.json.JSONObject;

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
			String trace, String runId) {
		super(timestamp, runId);
		this.testId = testId;
		this.testName = testName;
		this.trace = trace;
	}
	
	public TestErrorEvent(long timestamp, String testId, String testName,
			String trace) {
		this(timestamp, testId, testName, trace, "");
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
		JSONObject jsonMsg = new JSONObject();
		String timeStamp ="";
		if(includeTimeStamp){
			timeStamp = Long.toString(getTimestamp());			
		}
		jsonMsg.put("timeStamp", timeStamp);
		jsonMsg.put("messageId", MessageIds.TEST_ERROR);
		jsonMsg.put("runId", getRunId());
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);

		jsonMsg.put("errorTrace",trace.concat("\n"));
		
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
