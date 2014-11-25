package org.jenkinsci.testinprogress.server.messages.jdt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.jenkinsci.testinprogress.server.messages.ITestMessagesParser;
import org.jenkinsci.testinprogress.server.messages.ITestRunListener;

import com.google.common.base.Splitter;


/**
 * Handles the marshaling of the different messages.
 *
 * copied from org.eclipse.jdt.internal.junit.model.RemoteTestRunnerClient and
 * modified
 */
@Deprecated
public class JdtTestMessagesParser implements ITestMessagesParser {
		
	public JdtTestMessagesParser(ITestRunListener[] listeners) {
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
			int index = message.indexOf('%');
			if (index != 0) {
				String timeAsString = message.substring(0, index).trim();
				timestamp = Long.parseLong(timeAsString);
				message = message.substring(index);
			} else {
				timestamp = System.currentTimeMillis();
			}
			if (message.startsWith(JdtMessageIds.TRACE_START)) {
				fFailedTrace.setLength(0);
				return fTraceState;
			}
			if (message.startsWith(JdtMessageIds.EXPECTED_START)) {
				fExpectedResult.setLength(0);
				return fExpectedState;
			}
			if (message.startsWith(JdtMessageIds.ACTUAL_START)) {
				fActualResult.setLength(0);
				return fActualState;
			}
			String arg = message.substring(JdtMessageIds.MSG_HEADER_LENGTH);
			if (message.startsWith(JdtMessageIds.TEST_RUN_START)) {
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
			if (message.startsWith(JdtMessageIds.TEST_START)) {
				notifyTestStarted(arg);
				return this;
			}
			if (message.startsWith(JdtMessageIds.TEST_END)) {
				notifyTestEnded(arg);
				return this;
			}
			if (message.startsWith(JdtMessageIds.TEST_ERROR)) {
				extractFailure(arg, ITestRunListener.STATUS_ERROR);
				return this;
			}
			if (message.startsWith(JdtMessageIds.TEST_FAILED)) {
				extractFailure(arg, ITestRunListener.STATUS_FAILURE);
				return this;
			}
			if (message.startsWith(JdtMessageIds.TEST_RUN_END)) {
				long elapsedTime = Long.parseLong(arg);
				testRunEnded(elapsedTime);
				return this;
			}
			if (message.startsWith(JdtMessageIds.TEST_TREE)) {
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
			super(fFailedTrace, JdtMessageIds.TRACE_END);
		}

		void entireStringRead() {
			notifyTestFailed();
			fExpectedResult.setLength(0);
			fActualResult.setLength(0);
		}

		ProcessingState readMessage(String message) {
			if (message.startsWith(JdtMessageIds.TRACE_END)) {
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
	private long timestamp;
	ProcessingState fDefaultState = new DefaultProcessingState();
	ProcessingState fTraceState = new TraceProcessingState();
	ProcessingState fExpectedState = new AppendingProcessingState(
			fExpectedResult, JdtMessageIds.EXPECTED_END);
	ProcessingState fActualState = new AppendingProcessingState(fActualResult,
			JdtMessageIds.ACTUAL_END);
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

	private final Deque<Suite> suitesStack = new ArrayDeque<Suite>();
	
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
		String description;
		if (!hasTestId())
			description = fakeTestId(treeEntry);
		else
			description = treeEntry;
		Iterator<String> it = Splitter.on(',').split(description).iterator();
		String testId = it.next();
		String testName = it.next();
		boolean isSuite = Boolean.parseBoolean(it.next());
		int testCount = Integer.parseInt(it.next());
		
		Suite parentSuite = suitesStack.peek();
		String parentId = null;
		if (parentSuite != null) {
			parentId = parentSuite.testId;
			parentSuite.remainingChildren--;
			if (parentSuite.remainingChildren == 0) {
				suitesStack.pop();
			}
		}
		if (isSuite && testCount > 0) {
			suitesStack.push(new Suite(testId, testCount));
		}
		
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testTreeEntry(timestamp, testId, testName, parentId, isSuite);
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
			listener.testRunEnded(timestamp, elapsedTime);
		}
	}

	private void notifyTestEnded(final String test) {
		String s[] = extractTestId(test);
		String testId = s[0];
		String testName = s[1];
		boolean ignored = false;
		if (testName.startsWith(JdtMessageIds.IGNORED_TEST_PREFIX)) {
			ignored = true;
			testName = testName.substring(JdtMessageIds.IGNORED_TEST_PREFIX
					.length());
		}

		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testEnded(timestamp, testId, testName, ignored);
		}
	}

	private void notifyTestStarted(final String test) {
		String s[] = extractTestId(test);
		String testId = s[0];
		String testName = s[1];
		boolean ignored = false;
		if (testName.startsWith(JdtMessageIds.IGNORED_TEST_PREFIX)) {
			ignored = true;
			testName = testName.substring(JdtMessageIds.IGNORED_TEST_PREFIX
					.length());
		}
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testStarted(timestamp, testId, testName, ignored);
		}
	}

	private void notifyTestRunStarted(final int count) {
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testRunStarted(timestamp, null);
		}
	}

	private void notifyTestFailed() {
		String testName = fFailedTest;
		boolean assumptionFailed = false;
		if (testName.startsWith(JdtMessageIds.ASSUMPTION_FAILED_TEST_PREFIX)) {
			assumptionFailed = true;
			testName = testName
					.substring(JdtMessageIds.ASSUMPTION_FAILED_TEST_PREFIX
							.length());
		}
		for (int i = 0; i < fListeners.length; i++) {
			ITestRunListener listener = fListeners[i];
			listener.testFailed(timestamp, fFailureKind, fFailedTestId,
					testName, fFailedTrace.toString(),
					nullifyEmpty(fExpectedResult), nullifyEmpty(fActualResult),
					assumptionFailed);
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
	
	private static class Suite {
		private String testId;
		private int remainingChildren;
		
		public Suite(String testId, int remainingChildren) {
			this.testId = testId;
			this.remainingChildren = remainingChildren;
		}
		
		
	}
}