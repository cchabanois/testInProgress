package org.jenkinsci.testinprogress.server.events.run;

import org.jenkinsci.testinprogress.server.messages.MessageIds;
import org.json.JSONObject;

/**
 * Notification that a test run has started.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class RunStartEvent extends AbstractRunTestEvent {
	private final int testCount;
	private final String runId;
	
	public RunStartEvent(long timestamp, int testCount, String runId) {
		super(timestamp);
		this.testCount = testCount;
		this.runId = runId;
	}
	
	public RunStartEvent(long timestamp, int testCount) {
		this(timestamp,testCount,null);
	}

	public int getTestCount() {
		return testCount;
	}

	public String getType() {
		return "TESTC";
	}
	
	public String getRunId() {
		return runId;
	}

	@Override
	public String toString() {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("timeStamp", Long.toString(getTimestamp()));
		jsonMsg.put("messageId", MessageIds.TEST_RUN_START);
		jsonMsg.put("runId", getRunId());
		jsonMsg.put("testCount", testCount);
		jsonMsg.put("fVersion", "v2");
		
		return jsonMsg.toString();		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + testCount;
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
		RunStartEvent other = (RunStartEvent) obj;
		if (testCount != other.testCount)
			return false;
		return true;
	}


}
