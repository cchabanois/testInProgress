package org.jenkinsci.testinprogress.server.events.run;

import org.jenkinsci.testinprogress.server.messages.MessageIds;
import org.json.JSONObject;

/**
 * Test tree notification
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class TestTreeEvent extends AbstractRunTestEvent {
	private final String testId;
	private final String testName;
	private final String parentId;
	private final boolean isSuite;

	public TestTreeEvent(long timestamp, String testId, String testName, String parentId, 
			boolean isSuite) {
		super(timestamp);
		this.testId = testId;
		this.isSuite = isSuite;
		this.testName = testName;
		this.parentId = parentId;
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
	
	public String getParentId(){
		return parentId;
	}
	
	public String getType() {
		return "TSTTREE";
	}

	public String toString() {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("timeStamp", Long.toString(getTimestamp()));
		jsonMsg.put("messageId", MessageIds.TEST_TREE);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);
		jsonMsg.put("parentId", parentId);
		jsonMsg.put("isSuite", isSuite);
		
		return jsonMsg.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isSuite ? 1231 : 1237);
		result = prime * result + ((testId == null) ? 0 : testId.hashCode());		
		result = prime * result
				+ ((testName == null) ? 0 : testName.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
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
		TestTreeEvent other = (TestTreeEvent) obj;
		if (isSuite != other.isSuite)
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
		if (parentId == null) {
			if (other.parentId!= null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		return true;
	}

}
