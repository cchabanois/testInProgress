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

	public RunStartEvent(long timestamp, int testCount, String runId) {
		super(timestamp, runId);
		this.testCount = testCount;
	}
	
	public RunStartEvent(long timestamp, int testCount) {
		this(timestamp,testCount,"");
	}

	public int getTestCount() {
		return testCount;
	}

	public String getType() {
		return "TESTC";
	}

	public String toString(boolean includeTimeStamp) {
		JSONObject jsonMsg = new JSONObject();
		String timeStamp ="";
		if(includeTimeStamp){
			timeStamp = Long.toString(getTimestamp());			
		}
		jsonMsg.put("timeStamp", timeStamp);
		jsonMsg.put("messageId", MessageIds.TEST_RUN_START);
		jsonMsg.put("runId", getRunId());
		jsonMsg.put("testCount", testCount);
		jsonMsg.put("fVersion", "v2");
		
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
