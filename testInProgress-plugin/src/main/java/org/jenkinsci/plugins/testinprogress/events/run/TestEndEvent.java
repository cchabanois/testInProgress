package org.jenkinsci.plugins.testinprogress.events.run;

import org.jenkinsci.plugins.testinprogress.messages.MessageIds;

/**
 * Notification that a test has ended
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestEndEvent implements IRunTestEvent {
	private final String testId;
	private final String testName;
	private final boolean ignored;

	public TestEndEvent(String testId, String testName,
			boolean ignored) {
		this.testId = testId;
		this.testName = testName;
		this.ignored = ignored;
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

	@Override
	public String toString() {
		return MessageIds.TEST_END + testId + ","
				+ (ignored ? MessageIds.IGNORED_TEST_PREFIX : "") + testName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestEndEvent other = (TestEndEvent) obj;
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
