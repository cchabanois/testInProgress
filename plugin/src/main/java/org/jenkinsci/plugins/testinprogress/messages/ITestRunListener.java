/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.jenkinsci.plugins.testinprogress.messages;

/**
 * This class has been copied from
 * org.eclipse.jdt.internal.junit.model.ITestRunListener2 and slightly modified
 */
public interface ITestRunListener {

	/**
	 * Status constant indicating that a test passed (constant value 0).
	 */
	public static final int STATUS_OK = 0;
	/**
	 * Status constant indicating that a test had an error an unanticipated
	 * exception (constant value 1).
	 */
	public static final int STATUS_ERROR = 1;
	/**
	 * Status constant indicating that a test failed an assertion (constant
	 * value 2).
	 */
	public static final int STATUS_FAILURE = 2;

	/**
	 * A test run has started.
	 * 
	 * @param testCount
	 *            the number of individual tests that will be run
	 */
	public void testRunStarted(long timestamp, int testCount);

	/**
	 * A test run has ended.
	 * 
	 * @param elapsedTime
	 *            the total elapsed time of the test run
	 */
	public void testRunEnded(long timestamp, long elapsedTime);

	/**
	 * An individual test has started.
	 * 
	 * @param testId
	 *            a unique Id identifying the test
	 * @param testName
	 *            the name of the test that started
	 */
	public void testStarted(long timestamp, String testId, String testName, boolean ignored);

	/**
	 * An individual test has ended.
	 * 
	 * @param testId
	 *            a unique Id identifying the test
	 * @param testName
	 *            the name of the test that ended
	 */
	public void testEnded(long timestamp, String testId, String testName, boolean ignored);

	/**
	 * The VM instance performing the tests has terminated.
	 */
	public void testRunTerminated();
	
	/**
	 * Information about a member of the test suite that is about to be run. The
	 * format of the string is:
	 * 
	 * <pre>
	 *  testId,testName,isSuite,testcount
	 * 
	 *  testId: a unique id for the test
	 *  testName: the name of the test
	 *  parentId: Id of the parent to which this test belong. In case it does not belong just send empty string.
	 *  parentName: Name of the parent to which this test belong. In case it does not belong just send empty string.
	 *  isSuite: true or false depending on whether the test is a suite
	 *  testCount: an integer indicating the number of tests
	 * 
	 *  Example: &quot;324968,testPass(junit.tests.MyTest),false,1&quot;
	 * </pre>
	 * 
	 * @param description
	 *            a string describing a tree entry
	 * 
	 * @see MessageIds#TEST_TREE
	 */
	public void testTreeEntry(long timestamp, String testId, String testName,
			String parentId, String parentName, boolean isSuite, int testCount);

	/**
	 * An individual test has failed with a stack trace.
	 * 
	 * @param status
	 *            the outcome of the test; one of {@link #STATUS_ERROR
	 *            STATUS_ERROR} or {@link #STATUS_FAILURE STATUS_FAILURE}
	 * @param testId
	 *            a unique Id identifying the test
	 * @param testName
	 *            the name of the test that failed
	 * @param trace
	 *            the stack trace
	 * @param expected
	 *            the expected value
	 * @param actual
	 *            the actual value
	 */
	public void testFailed(long timestamp, int status, String testId, String testName,
			String trace, String expected, String actual);

}
