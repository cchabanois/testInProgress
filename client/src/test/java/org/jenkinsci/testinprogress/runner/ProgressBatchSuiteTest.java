package org.jenkinsci.testinprogress.runner;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.concurrent.Future;

import org.junit.Test;

import de.oschoen.junit.runner.ProgressAllTests;


public class ProgressBatchSuiteTest extends AbstractProgressSuiteTest {

	@Test
	public void testProgressBatchSuite() throws Exception {
		// Given
		Class<?> suiteUsingProgressBatchSuiteRunner =  ProgressAllTests.class;
		
		// When
		Future<String[]> result = runProgressSuite(suiteUsingProgressBatchSuiteRunner);

		// Then
		String[] messages = result.get();
		assertThat(messages[0], containsString("{\"testCount\":5,\"messageId\":\"TESTC\",\"fVersion\":\"v2\"}"));
		assertThat(messages[messages.length-1], containsString("RUNTIME"));
	}	
	
}
