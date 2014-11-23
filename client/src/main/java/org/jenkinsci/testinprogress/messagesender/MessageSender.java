package org.jenkinsci.testinprogress.messagesender;

import java.io.IOException;
import java.io.Writer;

import org.json.JSONObject;

/**
 * Abstract class used to send test messages
 * 
 * Generally, the sequence of methods called will be :
 * testRunStarted : runId is optional, must be unique for the build
 * testTree (1..n) : creates the tree of tests
 * testStarted
 * (testFailed/testAssumptionFailed/testError)
 * testEnded
 * testRunEnded
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public abstract class MessageSender {
	// @GuardedBy("this")
	protected Writer writer;

	public void testRunStarted(String runId) throws IOException {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_RUN_START);
		jsonMsg.put("runId", runId);
		jsonMsg.put("fVersion", "v3");
		println(jsonMsg.toString());
	}
	
	public void testRunStarted() throws IOException {
		testRunStarted(null);		
	}

	public void testRunEnded(long elapsedTime) throws IOException {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_RUN_END);
		jsonMsg.put("elapsedTime", elapsedTime);
		println(jsonMsg.toString());
	}
	
	public void testTree(String testId, String testName, String parentId, boolean isSuite) throws IOException {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_TREE);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);
		
		jsonMsg.put("parentId", parentId);
		jsonMsg.put("isSuite", isSuite);
		
		println(jsonMsg.toString());
	}
	
	public void testStarted(String testId, String testName, boolean ignored) throws IOException {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_START);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);
		if (ignored) {
			jsonMsg.put("ignored", ignored);
		}
		println(jsonMsg.toString());
	}
	
	public void testEnded(String testId, String testName, boolean ignored) throws IOException {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_END);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);
		if (ignored) {
			jsonMsg.put("ignored", ignored);
		}
		println(jsonMsg.toString());
	}
	
	public void testFailed(String testId, String testName, String expected,
			String actual, String trace) throws IOException {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_FAILED);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);

		jsonMsg.put("errorTrace",trace);
		jsonMsg.put("expectedMsg", expected);
		jsonMsg.put("actualMsg", actual);
		
		println(jsonMsg.toString());
	}
	
	public void testAssumptionFailed(String testId, String testName,
			String trace, String runId) throws IOException {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_FAILED);
		jsonMsg.put("runId", runId);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);

		jsonMsg.put("errorTrace",trace);
		jsonMsg.put("assumptionFailed", true);
		
		println(jsonMsg.toString());
	}
	
	public void testAssumptionFailed(String testId, String testName,
			String trace) throws IOException {
		testAssumptionFailed(testId, testName, trace, null);
	}

	public void testError(String testId, String testName, String trace) throws IOException {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_ERROR);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);

		jsonMsg.put("errorTrace",trace);
		
		println(jsonMsg.toString());
	}
	
	synchronized protected void println(String str) throws IOException {
		writer.write(str);
		writer.write('\n');
		writer.flush();
	}

	public void init() throws IOException {

	}

	public void shutdown() throws IOException {

	}

}
