package org.jenkinsci.plugins.testinprogress.events;

import org.jenkinsci.plugins.testinprogress.messages.MessageIds;

/**
 * Notification that a test has started
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 *
 */
public class TestStartEvent extends AbstractTestEvent {
	private final String testId;
	private final String testName;
	private final boolean ignored;

	public TestStartEvent(String runId, String testId, String testName,
			boolean ignored) {
		super(runId);
		this.testId = testId;
		this.testName = testName;
		this.ignored = ignored;
	}

	public String getType() {
		return "TESTS";
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
		return MessageIds.TEST_START+ testId+","
				+ (ignored ? MessageIds.IGNORED_TEST_PREFIX : "") 
				+ testName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		TestStartEvent other = (TestStartEvent) obj;
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
