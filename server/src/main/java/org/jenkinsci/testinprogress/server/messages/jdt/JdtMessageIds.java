package org.jenkinsci.testinprogress.server.messages.jdt;

/**
 * Message identifiers for messages sent by the RemoteTestRunner.
 *
 */
public class JdtMessageIds {
	/**
	 * The header length of a message, all messages have a fixed header length
	 */
	public static final int MSG_HEADER_LENGTH = 8;
	/**
	 * Notification that a test trace has started. The end of the trace is
	 * signaled by a TRACE_END message. In between the TRACE_START and TRACE_END
	 * the stack trace is submitted as multiple lines.
	 */
	public static final String TRACE_START = "%TRACES "; //$NON-NLS-1$
	/**
	 * Notification that a trace ends.
	 */
	public static final String TRACE_END = "%TRACEE "; //$NON-NLS-1$
	/**
	 * Notification that the expected result has started. The end of the
	 * expected result is signaled by a Trace_END.
	 */
	public static final String EXPECTED_START = "%EXPECTS"; //$NON-NLS-1$
	/**
	 * Notification that an expected result ends.
	 */
	public static final String EXPECTED_END = "%EXPECTE"; //$NON-NLS-1$
	/**
	 * Notification that the expected result has started. The end of the
	 * expected result is signaled by a Trace_END.
	 */
	public static final String ACTUAL_START = "%ACTUALS"; //$NON-NLS-1$
	/**
	 * Notification that an expected result ends.
	 */
	public static final String ACTUAL_END = "%ACTUALE"; //$NON-NLS-1$
	/**
	 * Notification that a test run has started. MessageIds.TEST_RUN_START +
	 * testCount.toString + " " + version
	 */
	public static final String TEST_RUN_START = "%TESTC "; //$NON-NLS-1$
	/**
	 * Notification that a test has started. MessageIds.TEST_START + testID +
	 * "," + testName
	 */
	public static final String TEST_START = "%TESTS "; //$NON-NLS-1$
	/**
	 * Notification that a test has started. TEST_END + testID + "," + testName
	 */
	public static final String TEST_END = "%TESTE "; //$NON-NLS-1$
	/**
	 * Notification that a test had a error. TEST_ERROR + testID + "," +
	 * testName. After the notification follows the stack trace.
	 */
	public static final String TEST_ERROR = "%ERROR "; //$NON-NLS-1$
	/**
	 * Notification that a test had a failure. TEST_FAILED + testID + "," +
	 * testName. After the notification follows the stack trace.
	 */
	public static final String TEST_FAILED = "%FAILED "; //$NON-NLS-1$
	/**
	 * Notification that a test run has ended. TEST_RUN_END +
	 * elapsedTime.toString().
	 */
	public static final String TEST_RUN_END = "%RUNTIME"; //$NON-NLS-1$
	/**
	 * Notification about a test inside the test suite. TEST_TREE + testId + ","
	 * + testName + "," + isSuite + "," + testcount isSuite = "true" or "false"
	 */
	public static final String TEST_TREE = "%TSTTREE"; //$NON-NLS-1$
	/**
	 * MessageFormat to encode test method identifiers: testMethod(testClass)
	 */
	public static final String TEST_IDENTIFIER_MESSAGE_FORMAT = "{0}({1})"; //$NON-NLS-1$
	/**
	 * Test identifier prefix for ignored tests.
	 */
	public static final String IGNORED_TEST_PREFIX = "@Ignore: "; //$NON-NLS-1$
	/**
	 * Test identifier prefix for tests with assumption failures.
	 */
	public static final String ASSUMPTION_FAILED_TEST_PREFIX = "@AssumptionFailure: "; //$NON-NLS-1$
}