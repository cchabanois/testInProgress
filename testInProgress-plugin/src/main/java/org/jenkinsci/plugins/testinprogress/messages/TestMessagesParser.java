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
		abstract ProcessingState readMessage(String message);
	}

	class DefaultProcessingState extends ProcessingState {
		ProcessingState readMessage(String message) {
			if (message.startsWith(MessageIds.TRACE_START)) {
				fFailedTrace.setLength(0);
				return fTraceState;
			}
			if (message.startsWith(MessageIds.EXPECTED_START)) {
				fExpectedResult.setLength(0);
				return fExpectedState;
			}
			if (message.startsWith(MessageIds.ACTUAL_START)) {
				fActualResult.setLength(0);
				return fActualState;
			}
			String arg = message.substring(MessageIds.MSG_HEADER_LENGTH);
			if (message.startsWith(MessageIds.TEST_RUN_START)) {
				// version < 2 format: count
				// version >= 2 format: count+" "+version
				int count = 0;
				int v = arg.indexOf(' ');
				if (v == -1) {
					fVersion = "v1"; //$NON-NLS-1$
					count = Integer.parseInt(arg);
				} else {
					fVersion = arg.substring(v + 1);
					String sc = arg.substring(0, v);
					count = Integer.parseInt(sc);
				}
				notifyTestRunStarted(count);
				return this;
			}
			if (message.startsWith(MessageIds.TEST_START)) {
				notifyTestStarted(arg);
				return this;
			}
			if (message.startsWith(MessageIds.TEST_END)) {
				notifyTestEnded(arg);
				return this;
			}
			if (message.startsWith(MessageIds.TEST_ERROR)) {
				extractFailure(arg, ITestRunListener.STATUS_ERROR);
				return this;
			}
			if (message.startsWith(MessageIds.TEST_FAILED)) {
				extractFailure(arg, ITestRunListener.STATUS_FAILURE);
				return this;
			}
			if (message.startsWith(MessageIds.TEST_RUN_END)) {
				long elapsedTime = Long.parseLong(arg);
				testRunEnded(elapsedTime);
				return this;
			}
			if (message.startsWith(MessageIds.TEST_TREE)) {
				notifyTestTreeEntry(arg);
				return this;
			}
			return this;
		}
	}

	/**
	 * Base class for states in which messages are appended to an internal
	 * string buffer until an end message is read.
	 */
	class AppendingProcessingState extends ProcessingState {
		private final StringBuffer fBuffer;
		private String fEndString;

		AppendingProcessingState(StringBuffer buffer, String endString) {
			this.fBuffer = buffer;
			this.fEndString = endString;
		}

		ProcessingState readMessage(String message) {
			if (message.startsWith(fEndString)) {
				entireStringRead();
				return fDefaultState;
			}
			fBuffer.append(message);
			if (fLastLineDelimiter != null)
				fBuffer.append(fLastLineDelimiter);
			return this;
		}

		/**
		 * subclasses can override to do special things when end message is read
		 */
		void entireStringRead() {
		}
	}

	class TraceProcessingState extends AppendingProcessingState {
		TraceProcessingState() {
			super(fFailedTrace, MessageIds.TRACE_END);
		}

		void entireStringRead() {
			notifyTestFailed();
			fExpectedResult.setLength(0);
			fActualResult.setLength(0);
		}

		ProcessingState readMessage(String message) {
			if (message.startsWith(MessageIds.TRACE_END)) {
				notifyTestFailed();
				fFailedTrace.setLength(0);
				fActualResult.setLength(0);
				fExpectedResult.setLength(0);
				return fDefaultState;
			}
			fFailedTrace.append(message);
			if (fLastLineDelimiter != null)
				fFailedTrace.append(fLastLineDelimiter);
			return this;
		}
	}

	/**
	 * The failed trace that is currently reported from the RemoteTestRunner
	 */
	private final StringBuffer fFailedTrace = new StringBuffer();
	/**
	 * The expected test result
	 */
	private final StringBuffer fExpectedResult = new StringBuffer();
	/**
	 * The actual test result
	 */
	private final StringBuffer fActualResult = new StringBuffer();

	ProcessingState fDefaultState = new DefaultProcessingState();
	ProcessingState fTraceState = new TraceProcessingState();
	ProcessingState fExpectedState = new AppendingProcessingState(
			fExpectedResult, MessageIds.EXPECTED_END);
	ProcessingState fActualState = new AppendingProcessingState(fActualResult,
			MessageIds.ACTUAL_END);
	ProcessingState fCurrentState = fDefaultState;

	/**
	 * An array of listeners that are informed about test events.
	 */
	private ITestRunListener[] fListeners;

	private PushbackReader fPushbackReader;
	private String fLastLineDelimiter;
	/**
	 * The protocol version
	 */
	private String fVersion;
	/**
	 * The failed test that is currently reported from the RemoteTestRunner
	 */
	private String fFailedTest;
	/**
	 * The Id of the failed test
	 */
	private String fFailedTestId;
	/**
	 * The kind of failure of the test that is currently reported as failed
	 */
	private int fFailureKind;

	private String readMessage(PushbackReader in) throws IOException {
		StringBuffer buf = new StringBuffer(128);
		int ch;
		while ((ch = in.read()) != -1) {
			if (ch == '\n') {
				fLastLineDelimiter = "\n"; //$NON-NLS-1$
				return buf.toString();
			} else if (ch == '\r') {
				ch = in.read();
				if (ch == '\n') {
					fLastLineDelimiter = "\r\n"; //$NON-NLS-1$
				} else {
					in.unread(ch);
					fLastLineDelimiter = "\r"; //$NON-NLS-1$
				}
				return buf.toString();
			} else {
				buf.append((char) ch);
			}
		}
		fLastLineDelimiter = null;
		if (buf.length() == 0)
			return null;
		return buf.toString();
	}

	private void receiveMessage(String message) {
		fCurrentState = fCurrentState.readMessage(message);
	}

	private void extractFailure(String arg, int status) {
		String s[] = extractTestId(arg);
		fFailedTestId = s[0];
		fFailedTest = s[1];
		fFailureKind = status;
	}

	/**
	 * @param arg
	 *            test name
	 * @return an array with two elements. The first one is the testId, the
	 *         second one the testName.
	 */
	String[] extractTestId(String arg) {
		String[] result = new String[2];
		if (!hasTestId()) {
			result[0] = arg; // use the test name as the test Id
			result[1] = arg;
			return result;
		}
		int i = arg.indexOf(',');
		result[0] = arg.substring(0, i);
		result[1] = arg.substring(i + 1, arg.length());
		return result;
	}

	private boolean hasTestId() {
		if (fVersion == null) // TODO fix me
			return true;
		return fVersion.equals("v2"); //$NON-NLS-1$
	}

	private void notifyTestTreeEntry(final String treeEntry) {
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			if (!hasTestId())
				listener.testTreeEntry(fakeTestId(treeEntry));
			else
				listener.testTreeEntry(treeEntry);
		}
	}

	private String fakeTestId(String treeEntry) {
		// extract the test name and add it as the testId
		int index0 = treeEntry.indexOf(',');
		String testName = treeEntry.substring(0, index0).trim();
		return testName + "," + treeEntry; //$NON-NLS-1$
	}

	private void testRunEnded(final long elapsedTime) {
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testRunEnded(elapsedTime);
		}
	}

	private void notifyTestEnded(final String test) {
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			String s[] = extractTestId(test);
			listener.testEnded(s[0], s[1]);
		}
	}

	private void notifyTestStarted(final String test) {
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			String s[] = extractTestId(test);
			listener.testStarted(s[0], s[1]);
		}
	}

	private void notifyTestRunStarted(final int count) {
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testRunStarted(count);
		}
	}

	private void notifyTestFailed() {
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testFailed(fFailureKind, fFailedTestId, fFailedTest,
					fFailedTrace.toString(), nullifyEmpty(fExpectedResult),
					nullifyEmpty(fActualResult));
		}
	}

	/**
	 * Returns a comparison result from the given buffer. Removes the
	 * terminating line delimiter.
	 * 
	 * @param buf
	 *            the comparison result
	 * @return the result or <code>null</code> if empty
	 * @since 3.7
	 */
	private static String nullifyEmpty(StringBuffer buf) {
		int length = buf.length();
		if (length == 0)
			return null;

		char last = buf.charAt(length - 1);
		if (last == '\n') {
			if (length > 1 && buf.charAt(length - 2) == '\r')
				return buf.substring(0, length - 2);
			else
				return buf.substring(0, length - 1);
		} else if (last == '\r') {
			return buf.substring(0, length - 1);
		}
		return buf.toString();
	}

	private void notifyTestRunTerminated() {
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testRunTerminated();
		}
	}
}
