package org.jenkinsci.plugins.testinprogress.events.run;

import org.jenkinsci.plugins.testinprogress.messages.MessageIds;
import org.json.JSONObject;

/**
 * Notification that a test has started
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestStartEvent extends AbstractRunTestEvent {
	private final String testId;
	private final String testName;
	private final boolean ignored;

	public TestStartEvent(long timestamp, String testId, String testName,
			boolean ignored, String runId) {
		super(timestamp, runId);
		this.testId = testId;
		this.testName = testName;
		this.ignored = ignored;
	}
	
	public TestStartEvent(long timestamp, String testId, String testName,
			boolean ignored){
		this(timestamp, testId, testName, ignored, "");
	}
	

	public String getType() {
		return "TESTS";
	}

	public String getTestId() {
		return testId;
	}

	public String getTestName() {
		return testName;
	}

	public boolean isIgnored() {
		return ignored;
	}

	public String toString(boolean includeTimeStamp) {
		JSONObject jsonMsg = new JSONObject();
		String timeStamp ="";
		if(includeTimeStamp){
			timeStamp = Long.toString(getTimestamp());			
		}
		jsonMsg.put("timeStamp", timeStamp);
		jsonMsg.put("messageId", MessageIds.TEST_START);
		jsonMsg.put("runId", getRunId());
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);
		jsonMsg.put("ignored", ignored);
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
		result = prime * result + (ignored ? 1231 : 1237);
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
		TestStartEvent other = (TestStartEvent) obj;
		if (ignored != other.ignored)
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
