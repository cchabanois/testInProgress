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
	private final String runId;
	
	public RunStartEvent(long timestamp, String runId) {
		super(timestamp);
		this.runId = runId;
	}
	
	public RunStartEvent(long timestamp) {
		this(timestamp,null);
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
		jsonMsg.put("fVersion", "v3");
		
		return jsonMsg.toString();		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((runId == null) ? 0 : runId.hashCode());
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
		if (runId == null) {
			if (other.runId != null)
				return false;
		} else if (!runId.equals(other.runId))
			return false;
		return true;
	}

}
