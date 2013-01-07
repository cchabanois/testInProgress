/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.jenkinsci.testinprogress.ant;

import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;

/**
 * Content from this class has been copied from {@link JUnitTestRunner}.
 * 
 * 
 */
public class TestSuiteHelper {
	private static final String JUNIT_4_TEST_ADAPTER = "junit.framework.JUnit4TestAdapter";

	public static Test getSuite(JUnitTest junitTest, ClassLoader loader) {
		Test suite = null;
		try {
			Class testClass = null;
			if (loader == null) {
				testClass = Class.forName(junitTest.getName());
			} else {
				testClass = Class.forName(junitTest.getName(), true, loader);
			}

			String[] methods = getMethods(junitTest);
			final boolean testMethodsSpecified = (methods != null);

			// check for a static suite method first, even when using
			// JUnit 4
			Method suiteMethod = null;
			if (!testMethodsSpecified) {
				try {
					// check if there is a suite method
					suiteMethod = testClass.getMethod("suite", new Class[0]);
				} catch (NoSuchMethodException e) {
					// no appropriate suite method found. We don't report any
					// error here since it might be perfectly normal.
				}
			}

			if (suiteMethod != null) {
				// if there is a suite method available, then try
				// to extract the suite from it. If there is an error
				// here it will be caught below and reported.
				suite = (Test) suiteMethod.invoke(null, new Class[0]);

			} else {
				Class junit4TestAdapterClass = null;
				boolean useSingleMethodAdapter = false;

				if (junit.framework.TestCase.class.isAssignableFrom(testClass)) {
					// Do not use JUnit 4 API for running JUnit 3.x
					// tests - it is not able to run individual test
					// methods.
					//
					// Technical details:
					// org.junit.runner.Request.method(Class,
					// String).getRunner()
					// would return a runner which always executes all
					// test methods. The reason is that the Runner would be
					// an instance of class
					// org.junit.internal.runners.OldTestClassRunner
					// that does not implement interface Filterable - so it
					// is unable to filter out test methods not matching
					// the requested name.
				} else {
					// Check for JDK 5 first. Will *not* help on JDK 1.4
					// if only junit-4.0.jar in CP because in that case
					// linkage of whole task will already have failed! But
					// will help if CP has junit-3.8.2.jar:junit-4.0.jar.

					// In that case first C.fN will fail with CNFE and we
					// will avoid UnsupportedClassVersionError.

					try {
						Class.forName("java.lang.annotation.Annotation");
						if (loader == null) {
							junit4TestAdapterClass = Class
									.forName(JUNIT_4_TEST_ADAPTER);
							if (testMethodsSpecified) {
								/*
								 * We cannot try to load the JUnit4TestAdapter
								 * before trying to load JUnit4TestMethodAdapter
								 * because it might fail with
								 * NoClassDefFoundException, instead of plain
								 * ClassNotFoundException.
								 */
								junit4TestAdapterClass = Class
										.forName("org.apache.tools.ant.taskdefs.optional.junit.JUnit4TestMethodAdapter");
								useSingleMethodAdapter = true;
							}
						} else {
							junit4TestAdapterClass = Class.forName(
									JUNIT_4_TEST_ADAPTER, true, loader);
							if (testMethodsSpecified) {
								junit4TestAdapterClass = Class
										.forName(
												"org.apache.tools.ant.taskdefs.optional.junit.JUnit4TestMethodAdapter",
												true, loader);
								useSingleMethodAdapter = true;
							}
						}
					} catch (ClassNotFoundException e) {
						// OK, fall back to JUnit 3.
					}
				}
				boolean junit4 = junit4TestAdapterClass != null;

				if (junit4) {
					// Let's use it!
					Class[] formalParams;
					Object[] actualParams;
					if (useSingleMethodAdapter) {
						formalParams = new Class[] { Class.class,
								String[].class };
						actualParams = new Object[] { testClass, methods };
					} else {
						formalParams = new Class[] { Class.class };
						actualParams = new Object[] { testClass };
					}
					suite = (Test) junit4TestAdapterClass.getConstructor(
							formalParams).newInstance(actualParams);
				} else {
					// Use JUnit 3.

					// try to extract a test suite automatically this
					// will generate warnings if the class is no
					// suitable Test
					if (!testMethodsSpecified) {
						suite = new TestSuite(testClass);
					} else if (methods.length == 1) {
						suite = TestSuite.createTest(testClass, methods[0]);
					} else {
						TestSuite testSuite = new TestSuite(testClass.getName());
						for (int i = 0; i < methods.length; i++) {
							testSuite.addTest(TestSuite.createTest(testClass,
									methods[i]));
						}
						suite = testSuite;
					}
				}

			}
			return suite;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private static String[] getMethods(JUnitTest junitTest) {
		return null;
	}

}
