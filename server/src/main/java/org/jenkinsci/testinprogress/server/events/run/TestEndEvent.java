package org.jenkinsci.testinprogress.server.events.run;

import org.jenkinsci.testinprogress.server.messages.MessageIds;
import org.json.JSONObject;

/**
 * Notification that a test has ended
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestEndEvent extends AbstractRunTestEvent {
	private final String testId;
	private final String testName;
	private final boolean ignored;
	private final long elapsedTime;
	
	public TestEndEvent(long timestamp, String testId, String testName,
			boolean ignored, long elapsedTime) {
		super(timestamp);
		this.testId = testId;
		this.testName = testName;
		this.ignored = ignored;
		this.elapsedTime = elapsedTime;
	}

	public String getType() {
		return "TESTE";
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

	public long getElapsedTime() {
		return elapsedTime;
	}
	
	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(boolean includeTimeStamp) {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("timeStamp", Long.toString(getTimestamp()));	
		jsonMsg.put("messageId", MessageIds.TEST_END);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);
		if (ignored) {
			jsonMsg.put("ignored", ignored);
		}
		return jsonMsg.toString();		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (elapsedTime ^ (elapsedTime >>> 32));
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
		TestEndEvent other = (TestEndEvent) obj;
		if (elapsedTime != other.elapsedTime)
			return false;
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
