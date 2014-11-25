package org.jenkinsci.testinprogress.server.messages.json.v2;

/**
 * Message identifiers for messages sent by the
 * RemoteTestRunner.
 *
 * @see RemoteTestRunner
 */
public class Jsonv2MessageIds {


	/**
	 * Notification that a test run has started.
	 * MessageIds.TEST_RUN_START + testCount.toString + " " + version
	 */
	public static final String TEST_RUN_START=  "TESTC"; //$NON-NLS-1$
	/**
	 * Notification that a test has started.
	 * MessageIds.TEST_START + testID + "," + testName
	 */
	public static final String TEST_START=  "TESTS";		 //$NON-NLS-1$
	/**
	 * Notification that a test has started.
	 * TEST_END + testID + "," + testName
	 */
	public static final String TEST_END=    "TESTE";		 //$NON-NLS-1$
	/**
	 * Notification that a test had a error.
	 * TEST_ERROR + testID + "," + testName.
	 * After the notification follows the stack trace.
	 */
	public static final String TEST_ERROR=  "ERROR";		 //$NON-NLS-1$
	/**
	 * Notification that a test had a failure.
	 * TEST_FAILED + testID + "," + testName.
	 * After the notification follows the stack trace.
	 */
	public static final String TEST_FAILED= "FAILED";	 //$NON-NLS-1$
	/**
	 * Notification that a test run has ended.
	 * TEST_RUN_END + elapsedTime.toString().
	 */
	public static final String TEST_RUN_END="RUNTIME";	 //$NON-NLS-1$
	/**
	 * Notification about a test inside the test suite.
	 * TEST_TREE + testId + "," + testName + "," + isSuite + "," + testcount
	 * isSuite = "true" or "false"
	 */
	public static final String TEST_TREE="TSTTREE"; //$NON-NLS-1$

}


