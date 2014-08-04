package org.jenkinsci.plugins.testinprogress.events.run;

import org.jenkinsci.plugins.testinprogress.messages.MessageIds;
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
	private final String parentName;
	private final boolean isSuite;
	private final int testCount;

	public TestTreeEvent(long timestamp, String testId, String testName, String parentId, String parentName,
			boolean isSuite, int testCount, String runId) {
		super(timestamp);
		this.testCount = testCount;
		this.testId = testId;
		this.isSuite = isSuite;
		this.testName = testName;
		this.parentId = parentId;
		this.parentName = parentName;
		setRunId(runId);
	}
	
	public TestTreeEvent(long timestamp, String testId, String testName,
			boolean isSuite, int testCount, String runId) {
		this(timestamp,testId,testName,"","",isSuite,testCount, runId);
	}
	
	public TestTreeEvent(long timestamp, String testId, String testName,
			boolean isSuite, int testCount){
		this(timestamp, testId, testName, isSuite, testCount, "");
	}
	
	public TestTreeEvent(long timestamp, String testId, String testName, String parentId, String parentName,
			boolean isSuite, int testCount){
		this(timestamp, testId, testName, parentId, parentName, isSuite, testCount, "");
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
	
	public String getParentId(){
		return parentId;
	}
	
	public String getParentName(){
		return parentName;
	}

	public String getType() {
		return "TSTTREE";
	}

	public String toString(boolean includeTimeStamp) {
		JSONObject jsonMsg = new JSONObject();
		String timeStamp ="";
		if(includeTimeStamp){
			timeStamp = Long.toString(getTimestamp());			
		}
		jsonMsg.put("timeStamp", timeStamp);
		jsonMsg.put("messageId", MessageIds.TEST_TREE);
		jsonMsg.put("runId", getRunId());
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);
		jsonMsg.put("parentId", parentId);
		jsonMsg.put("parentName", parentName);
		jsonMsg.put("isSuite", isSuite);
		jsonMsg.put("testCount", testCount);
		
		
		return jsonMsg.toString();
	}

	@Override
	public String toString() {
		return toString(true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isSuite ? 1231 : 1237);
		result = prime * result + testCount;
		result = prime * result + ((testId == null) ? 0 : testId.hashCode());		
		result = prime * result
				+ ((testName == null) ? 0 : testName.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result
				+ ((parentName == null) ? 0 : parentName.hashCode());
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
		if (parentId == null) {
			if (other.parentId!= null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (parentName == null) {
			if (other.parentName != null)
				return false;
		} else if (!parentName.equals(other.parentName))
			return false;
		return true;
	}

}
