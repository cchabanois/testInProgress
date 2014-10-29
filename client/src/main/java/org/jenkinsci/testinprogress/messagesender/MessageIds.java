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
package org.jenkinsci.testinprogress.messagesender;

/**
 * Message identifiers for messages sent by the RemoteTestRunner.
 *
 * @see RemoteTestRunner
 */
public class MessageIds {

	/**
	 * Notification that a test run has started.
	 */
	public static final String TEST_RUN_START = "TESTC";
	/**
	 * Notification that a test has started.
	 */
	public static final String TEST_START = "TESTS";
	/**
	 * Notification that a test has started.
	 */
	public static final String TEST_END = "TESTE";
	/**
	 * Notification that a test had a error.
	 */
	public static final String TEST_ERROR = "ERROR";
	/**
	 * Notification that a test had a failure.
	 */
	public static final String TEST_FAILED = "FAILED";
	/**
	 * Notification that a test run has ended.
	 */
	public static final String TEST_RUN_END = "RUNTIME";
	/**
	 * Notification about a test inside the test suite.
	 */
	public static final String TEST_TREE = "TSTTREE";

}
