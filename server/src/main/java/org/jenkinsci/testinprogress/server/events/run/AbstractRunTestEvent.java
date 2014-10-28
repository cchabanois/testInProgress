package org.jenkinsci.testinprogress.server.events.run;

/**
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public abstract class AbstractRunTestEvent implements IRunTestEvent {
	private final long timestamp;
	private final String runId;

	public AbstractRunTestEvent(long timestamp, String runId) {
		this.timestamp = timestamp;
		this.runId = runId;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public String getRunId(){
		return this.runId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
		AbstractRunTestEvent other = (AbstractRunTestEvent) obj;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

}
