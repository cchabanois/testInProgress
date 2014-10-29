package org.jenkinsci.testinprogress.server.events.run;

/**
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public abstract class AbstractRunTestEvent implements IRunTestEvent {
	private final long timestamp;

	public AbstractRunTestEvent(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
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
