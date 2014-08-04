package org.jenkinsci.testinprogress.runner;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.concurrent.Future;

import org.junit.Test;

import tests.ProgressCalcTestsSuite;

public class ProgressSuiteTest extends AbstractProgressSuiteTest {

	@Test
	public void testProgressSuite() throws Exception {
		// Given
		Class<?> classUsingProgressSuiteRunner = ProgressCalcTestsSuite.class;
		
		// When
		Future<String[]> result = runProgressSuite(classUsingProgressSuiteRunner);

		// Then
		String[] messages = result.get();
		
		assertThat(messages[0], containsString("{\"testCount\":6,\"runId\":\"\",\"messageId\":\"TESTC\",\"fVersion\":\"v2\"}"));
		assertThat(messages[messages.length-1], containsString("RUNTIME"));
	}



}
