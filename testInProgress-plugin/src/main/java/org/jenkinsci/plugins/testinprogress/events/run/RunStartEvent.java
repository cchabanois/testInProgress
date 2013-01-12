package org.jenkinsci.plugins.testinprogress.events.run;

import org.jenkinsci.plugins.testinprogress.messages.MessageIds;

/**
 * Notification that a test run has started.
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class RunStartEvent implements IRunTestEvent {
	private final int testCount;

	public RunStartEvent(int testCount) {
		this.testCount = testCount;
	}

	public int getTestCount() {
		return testCount;
	}

	public String getType() {
		return "TESTC";
	}

	@Override
	public String toString() {
		return MessageIds.TEST_RUN_START + Integer.toString(testCount) + " "
				+ "v2";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + testCount;
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
		RunStartEvent other = (RunStartEvent) obj;
		if (testCount != other.testCount)
			return false;
		return true;
	}

}
