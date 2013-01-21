package org.jenkinsci.plugins.testinprogress.events.run;

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

}
