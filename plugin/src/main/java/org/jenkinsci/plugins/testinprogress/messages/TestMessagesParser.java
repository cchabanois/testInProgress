/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Julien Ruaux: jruaux@octo.com
 * 	   Vincent Massol: vmassol@octo.com
 *******************************************************************************/
package org.jenkinsci.plugins.testinprogress.messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Handles the marshaling of the different messages.
 * 
 * copied from org.eclipse.jdt.internal.junit.model.RemoteTestRunnerClient and
 * modified
 */
public class TestMessagesParser {
	
	public TestMessagesParser(ITestRunListener[] listeners) {
		this.fListeners = listeners;
	}

	public void processTestMessages(Reader reader) {
		fPushbackReader = new PushbackReader(new BufferedReader(reader));
		try {
			String message;
			while (fPushbackReader != null
					&& (message = readMessage(fPushbackReader)) != null)
				receiveMessage(message);
		} catch (IOException e) {
			System.out.println("Testin progress plugin: Got IO Exception:" +e.getMessage());
			notifyTestRunTerminated();
		}
		shutDown();
	}

	private void shutDown() {
		try {
			if (fPushbackReader != null) {
				fPushbackReader.close();
				fPushbackReader = null;
			}
		} catch (IOException e) {
		}
	}

	/**
	 * A simple state machine to process requests from the RemoteTestRunner
	 */
	abstract class ProcessingState {
		abstract ProcessingState readMessage(JSONObject jsonMsg);
	}

	class DefaultProcessingState extends ProcessingState {
		ProcessingState readMessage(JSONObject jsonMsg) {
			String msgId = getValue(jsonMsg, "messageId", "").toString().trim();
		
			if (msgId.contentEquals(MessageIds.TEST_RUN_START.trim())) {
				int count = 0;
				fVersion = getValue(jsonMsg, "fVersion", "v1").toString();
				count = jsonMsg.getInt("testCount");
				notifyTestRunStarted(jsonMsg, count);
			} else if (msgId.contentEquals(MessageIds.TEST_START.trim())) {
				notifyTestStarted(jsonMsg);

			} else if (msgId.contentEquals(MessageIds.TEST_END.trim())) {
				notifyTestEnded(jsonMsg);

			} else if (msgId.contentEquals(MessageIds.TEST_ERROR.trim())) {
				notifyTestFailed(jsonMsg, ITestRunListener.STATUS_ERROR);

			} else if (msgId.contentEquals(MessageIds.TEST_FAILED.trim())) {
				notifyTestFailed(jsonMsg, ITestRunListener.STATUS_FAILURE);

			} else if (msgId.contentEquals(MessageIds.TEST_RUN_END.trim())) {
				String elTime = getValue(jsonMsg, "elapsedTime", "").toString();
				
				long elapsedTime = Long.parseLong(elTime);
				testRunEnded(jsonMsg, elapsedTime);

			} else if (msgId.contentEquals(MessageIds.TEST_TREE.trim())) {
				notifyTestTreeEntry(jsonMsg);
			}
			
			return this;
		}
	}

	//private long timestamp;
	
	ProcessingState fDefaultState = new DefaultProcessingState();
	ProcessingState fCurrentState = fDefaultState;

	/**
	 * An array of listeners that are informed about test events.
	 */
	private ITestRunListener[] fListeners;

	private PushbackReader fPushbackReader;
	
	private long startTime;
	
	/**
	 * The protocol version
	 */
	private String fVersion;
	
	private String readMessage(PushbackReader in) throws IOException {
		StringBuffer buf = new StringBuffer(128);
		int ch;
		while ((ch = in.read()) != -1) {
			if(ch == '}'){
				buf.append((char)ch);
				ch = in.read();
				if (ch == '\n' || ch == '\r') {
					return buf.toString();
				} else {
					buf.append((char)ch);
					in.unread(ch);
				}
			} else {
				buf.append((char) ch);
			}
		}
		
		if (buf.length() == 0)
			return null;
		return buf.toString();
	}

	private void receiveMessage(String message) throws JSONException {		
		JSONObject jsonMessage = new JSONObject(message);
		fCurrentState = fCurrentState.readMessage(jsonMessage);
	}

	private void notifyTestTreeEntry(final JSONObject jsonMsg) {
		String testName = jsonMsg.getString("testName");
		String testId = getStringValue(jsonMsg, "testId", testName);
		String parentId = getStringValue(jsonMsg, "parentId", "");
		String parentName = getStringValue(jsonMsg, "parentName", "");
		boolean isSuite = (Boolean)getValue(jsonMsg, "isSuite", false);
		int count = (Integer) getValue(jsonMsg, "testCount", 1);
		
		long timeStamp = getTimeStamp(jsonMsg);
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testTreeEntry(timeStamp, testId, testName, parentId, parentName, isSuite, count);
		}
	}

	private void testRunEnded(JSONObject jsonMsg, final long elapsedTime) {
		long timeStamp = getTimeStamp(jsonMsg);
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testRunEnded(timeStamp, elapsedTime);
		}
	}

	private void notifyTestEnded(final JSONObject jsonMsg) {
		String testName = jsonMsg.getString("testName");
		String testId = getStringValue(jsonMsg, "testId", testName);
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
		boolean ignored = (Boolean) getValue(jsonMsg, "ignored", false);
		long timeStamp = getTimeStamp(jsonMsg);
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			
			listener.testStarted(timeStamp,  testId, testName, ignored);
		}
	}

	private void notifyTestRunStarted(JSONObject jsonMsg, final int count) {
		long timeStamp = getTimeStamp(jsonMsg);
		startTime = timeStamp;
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testRunStarted(timeStamp, count);
		}
	}

	private void notifyTestFailed(JSONObject jsonMsg, int failureKind) {
		String testName = jsonMsg.getString("testName");
		String testId = getStringValue(jsonMsg, "testId", testName);
		String errorTrace = getStringValue(jsonMsg, "errorTrace", "");
		String expectedMsg = getStringValue(jsonMsg, "expectedMsg", "");
		String actualMsg = getStringValue(jsonMsg, "actualMsg", "");
		long timeStamp = getTimeStamp(jsonMsg);
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testFailed(timeStamp, failureKind, testId, testName,
					errorTrace, expectedMsg,
					actualMsg);
		}
	}


	private void notifyTestRunTerminated() {
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testRunTerminated();
		}
	}
	
	public Object getValue(JSONObject jsonData,String key, Object defaultValue){
		try{
			Object data = jsonData.get(key);
			return data;
		}catch(JSONException e){
			return defaultValue;			
		}
	}
	
	public String getStringValue(JSONObject jsonData,String key, Object defaultValue){
		return this.getValue(jsonData, key, defaultValue).toString();
	}
	
	
	private long getTimeStamp(JSONObject jsonMsg){
		long timeStamp;
		String timeAsString = getStringValue(jsonMsg, "timeStamp", "");
		
		if (!timeAsString.contentEquals("")) {
			timeStamp = Long.parseLong(timeAsString);				
		} else {
			timeStamp = System.currentTimeMillis();
		}
		
		return timeStamp;
	}
}
