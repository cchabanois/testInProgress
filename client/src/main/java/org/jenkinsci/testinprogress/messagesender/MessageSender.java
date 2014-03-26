package org.jenkinsci.testinprogress.messagesender;

import java.io.IOException;
import java.io.Writer;

import org.json.JSONObject;

/**
 * Abstract class used to send test messages
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public abstract class MessageSender {
	protected Writer writer;

	public void testRunStarted(int testCount) {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_RUN_START);
		jsonMsg.put("testCount", testCount);
		jsonMsg.put("fVersion", "v2");
		println(jsonMsg.toString());
		flush();
	}

	public void testRunEnded(long elapsedTime) {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_RUN_END);
		jsonMsg.put("elapsedTime", elapsedTime);
		println(jsonMsg.toString());
		flush();
	}

	public void testTree(String testId, String testName, String parentId,String parentName, boolean isSuite,
			int testCount) {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_TREE);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);
		
		jsonMsg.put("parentId", parentId);
		jsonMsg.put("parentName", parentName);
		jsonMsg.put("isSuite", isSuite);
		jsonMsg.put("testCount", testCount);
		
		println(jsonMsg.toString());
		flush();
	}
	
	public void testTree(String testId, String testName, boolean isSuite,
			int testCount) {
		this.testTree(testId, testName, "", "", isSuite, testCount);
	}

	public void testStarted(String testId, String testName, boolean ignored) {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_START);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);
		
		jsonMsg.put("ignored", ignored);
		println(jsonMsg.toString());
		flush();
	}

	public void testEnded(String testId, String testName, boolean ignored) {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_END);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);
		jsonMsg.put("ignored", ignored);
		println(jsonMsg.toString());
		
		flush();
	}

	public void testFailed(String testId, String testName, String expected,
			String actual, String trace) {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_FAILED);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);

		jsonMsg.put("errorTrace",trace.concat("\n"));
		jsonMsg.put("expectedMsg", expected.concat("\n"));
		jsonMsg.put("actualMsg", actual.concat("\n"));
		
		println(jsonMsg.toString());
		flush();
	}

	public void testAssumptionFailed(String testId, String testName,
			String trace) {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_FAILED);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);

		jsonMsg.put("errorTrace",trace.concat("\n"));
		jsonMsg.put("assumptionFailed", true);
		
		println(jsonMsg.toString());
		flush();
	}

	public void testError(String testId, String testName, String trace) {
		JSONObject jsonMsg = new JSONObject();
		jsonMsg.put("messageId", MessageIds.TEST_ERROR);
		jsonMsg.put("testId", testId);
		jsonMsg.put("testName", testName);

		jsonMsg.put("errorTrace",trace.concat("\n"));
		
		println(jsonMsg.toString());
		flush();
	}

	protected void println(String str) {
		try {
			writer.write(str);
			writer.write('\n');
		} catch (IOException e) {
			throw new RuntimeException("Could not send message", e);
		}
	}

	protected void flush() {
		try {
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Could not send message", e);
		}
	}

	public void init() throws IOException {

	}

	public void shutdown() throws IOException {

	}

}
