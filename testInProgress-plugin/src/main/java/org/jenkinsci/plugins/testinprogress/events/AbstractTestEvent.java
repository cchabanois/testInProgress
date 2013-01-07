package org.jenkinsci.plugins.testinprogress.events;

/**
 * Abstract class for test events
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public abstract class AbstractTestEvent implements ITestEvent {
	private final String runId;
	
	public AbstractTestEvent(String runId) {
		this.runId = runId;
	}

	public String getRunId() {
		return runId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((runId == null) ? 0 : runId.hashCode());
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
		AbstractTestEvent other = (AbstractTestEvent) obj;
		if (runId == null) {
			if (other.runId != null)
				return false;
		} else if (!runId.equals(other.runId))
			return false;
		return true;
	}
	
}
