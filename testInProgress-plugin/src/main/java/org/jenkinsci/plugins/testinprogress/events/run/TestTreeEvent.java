package org.jenkinsci.plugins.testinprogress.events.run;

import org.jenkinsci.plugins.testinprogress.messages.MessageIds;

/**
 * Test tree notification
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestTreeEvent extends AbstractRunTestEvent {
	private final String testId;
	private final String testName;
	private final boolean isSuite;
	private final int testCount;

	public TestTreeEvent(long timestamp, String testId, String testName,
			boolean isSuite, int testCount) {
		super(timestamp);
		this.testCount = testCount;
		this.testId = testId;
		this.isSuite = isSuite;
		this.testName = testName;
	}

	public int getTestCount() {
		return testCount;
	}

	public String getTestId() {
		return testId;
	}

	public boolean isSuite() {
		return isSuite;
	}

	public String getTestName() {
		return testName;
	}

	public String getType() {
		return "TSTTREE";
	}

	public String toString(boolean includeTimeStamp) {
		return (includeTimeStamp ? Long.toString(getTimestamp()) + " " : "")
				+ MessageIds.TEST_TREE + testId + "," + testName + ","
				+ Boolean.toString(isSuite) + "," + Integer.toString(testCount);
	}

	@Override
	public String toString() {
		return toString(true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isSuite ? 1231 : 1237);
		result = prime * result + testCount;
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
		TestTreeEvent other = (TestTreeEvent) obj;
		if (isSuite != other.isSuite)
			return false;
		if (testCount != other.testCount)
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
