package org.jenkinsci.testinprogress.server.messages.json.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.testinprogress.server.messages.ITestMessagesParser;
import org.jenkinsci.testinprogress.server.messages.ITestRunListener;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handles the marshaling of the different messages for the json v2 format.
 * 
 */
@Deprecated
public class Jsonv2TestMessagesParser implements ITestMessagesParser {
	private final static Logger LOG = Logger.getLogger(Jsonv2TestMessagesParser.class
			.getName());

	private BufferedReader reader;

	/**
	 * An array of listeners that are informed about test events.
	 */
	private final ITestRunListener[] fListeners;

	public Jsonv2TestMessagesParser(ITestRunListener[] listeners) {
		this.fListeners = listeners;
	}

	public void processTestMessages(Reader reader) {
		this.reader = new BufferedReader(reader);
		try {
			JSONObject message;
			while ((message = readMessage(this.reader)) != null)
				processMessage(message);
		} catch (JSONException e) {
			LOG.log(Level.WARNING, "Could not read message", e);
			notifyTestRunTerminated();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Could not read message", e);
			notifyTestRunTerminated();
		}
		shutDown();
	}

	private void shutDown() {
		try {
			if (reader != null) {
				reader.close();
				reader = null;
			}
		} catch (IOException e) {
		}
	}

	private JSONObject readMessage(BufferedReader in) throws IOException,
			JSONException {
		String line = in.readLine();
		if (line == null) {
			return null;
		} else {
			try {
				return new JSONObject(line);
			} catch (JSONException e) {
				throw new IOException("Message is not a valid json object : '"
						+ line + "'", e);
			}
		}
	}

	private void processMessage(JSONObject jsonMsg) {
		String msgId = getValue(jsonMsg, "messageId", "").toString().trim();

		if (msgId.contentEquals(Jsonv2MessageIds.TEST_RUN_START.trim())) {
			int count = 0;
			String version = getValue(jsonMsg, "fVersion", "v2").toString();
			count = jsonMsg.getInt("testCount");
			notifyTestRunStarted(jsonMsg, count);
		} else if (msgId.contentEquals(Jsonv2MessageIds.TEST_START.trim())) {
			notifyTestStarted(jsonMsg);

		} else if (msgId.contentEquals(Jsonv2MessageIds.TEST_END.trim())) {
			notifyTestEnded(jsonMsg);

		} else if (msgId.contentEquals(Jsonv2MessageIds.TEST_ERROR.trim())) {
			notifyTestFailed(jsonMsg, ITestRunListener.STATUS_ERROR);

		} else if (msgId.contentEquals(Jsonv2MessageIds.TEST_FAILED.trim())) {
			notifyTestFailed(jsonMsg, ITestRunListener.STATUS_FAILURE);

		} else if (msgId.contentEquals(Jsonv2MessageIds.TEST_RUN_END.trim())) {
			String elTime = getValue(jsonMsg, "elapsedTime", "").toString();

			long elapsedTime = Long.parseLong(elTime);
			testRunEnded(jsonMsg, elapsedTime);

		} else if (msgId.contentEquals(Jsonv2MessageIds.TEST_TREE.trim())) {
			notifyTestTreeEntry(jsonMsg);
		}
	}

	private final Deque<Suite> suitesStack = new ArrayDeque<Suite>();

	private void notifyTestTreeEntry(final JSONObject jsonMsg) {
		String testName = jsonMsg.getString("testName");
		String testId = getStringValue(jsonMsg, "testId", testName);
		String parentId = getStringValue(jsonMsg, "parentId", null);
		String parentName = getStringValue(jsonMsg, "parentName", null);
		String runId = getStringValue(jsonMsg, "runId", null);
		boolean isSuite = (Boolean) getValue(jsonMsg, "isSuite", false);
		int count = (Integer) getValue(jsonMsg, "testCount", 1);
		long timeStamp = getTimeStamp(jsonMsg);

		if (parentId == null) {
			Suite parentSuite = suitesStack.peek();
			if (parentSuite != null) {
				parentId = parentSuite.testId;
				parentSuite.remainingChildren--;
				if (parentSuite.remainingChildren == 0) {
					suitesStack.pop();
				}
			}
			if (isSuite && count > 0) {
				suitesStack.push(new Suite(testId, count));
			}
		}

		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testTreeEntry(timeStamp, testId, testName, parentId, isSuite);
		}
	}

	private void testRunEnded(JSONObject jsonMsg, final long elapsedTime) {
		long timeStamp = getTimeStamp(jsonMsg);
		String runId = getStringValue(jsonMsg, "runId", null);
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testRunEnded(timeStamp, elapsedTime);
		}
	}

	private void notifyTestEnded(final JSONObject jsonMsg) {
		String testName = jsonMsg.getString("testName");
		String testId = getStringValue(jsonMsg, "testId", testName);
		String runId = getStringValue(jsonMsg, "runId", null);
		boolean ignored = (Boolean) getValue(jsonMsg, "ignored", false);
		long timeStamp = getTimeStamp(jsonMsg);
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testEnded(timeStamp, testId, testName, ignored);
		}
	}

	private void notifyTestStarted(final JSONObject jsonMsg) {
		String testName = jsonMsg.getString("testName");
		String testId = getStringValue(jsonMsg, "testId", testName);
		String runId = getStringValue(jsonMsg, "runId", null);
		boolean ignored = (Boolean) getValue(jsonMsg, "ignored", false);
		long timeStamp = getTimeStamp(jsonMsg);
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testStarted(timeStamp, testId, testName, ignored);
		}
	}

	private void notifyTestRunStarted(JSONObject jsonMsg, final int count) {
		long timeStamp = getTimeStamp(jsonMsg);
		String runId = getStringValue(jsonMsg, "runId", null);
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testRunStarted(timeStamp, runId);
		}
	}

	private void notifyTestFailed(JSONObject jsonMsg, int failureKind) {
		String testName = jsonMsg.getString("testName");
		String testId = getStringValue(jsonMsg, "testId", testName);
		String errorTrace = getStringValue(jsonMsg, "errorTrace", null);
		String expectedMsg = getStringValue(jsonMsg, "expectedMsg", null);
		String actualMsg = getStringValue(jsonMsg, "actualMsg", null);
		String runId = getStringValue(jsonMsg, "runId", null);
		long timeStamp = getTimeStamp(jsonMsg);
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testFailed(timeStamp, failureKind, testId, testName,
					errorTrace, expectedMsg, actualMsg, false);
		}
	}

	private void notifyTestRunTerminated() {
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testRunTerminated();
		}
	}

	public Object getValue(JSONObject jsonData, String key, Object defaultValue) {
		try {
			Object data = jsonData.get(key);
			return data;
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	private String getStringValue(JSONObject jsonData,String key, Object defaultValue){
		Object value = this.getValue(jsonData, key, defaultValue);
		if (value == null) {
			return null;
		} else {
			return value.toString();
		}
	}

	private long getTimeStamp(JSONObject jsonMsg) {
		long timeStamp;
		String timeAsString = getStringValue(jsonMsg, "timeStamp", null);

		if (timeAsString != null) {
			timeStamp = Long.parseLong(timeAsString);
		} else {
			timeStamp = System.currentTimeMillis();
		}

		return timeStamp;
	}

	private static class Suite {
		private String testId;
		private int remainingChildren;

		public Suite(String testId, int remainingChildren) {
			this.testId = testId;
			this.remainingChildren = remainingChildren;
		}

	}

}
