package org.jenkinsci.testinprogress.runner;

import static org.jenkinsci.testinprogress.utils.TestMessageUtils.*;

import java.util.concurrent.Future;

import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import de.oschoen.junit.runner.ProgressAllTests;

public class ProgressBatchSuiteTest extends AbstractProgressSuiteTest {

	@Test
	public void testProgressBatchSuite() throws Exception {
		// Given
		Class<?> suiteUsingProgressBatchSuiteRunner = ProgressAllTests.class;

		// When
		Future<JSONObject[]> result = runProgressSuite(suiteUsingProgressBatchSuiteRunner);

		// Then
		JSONObject[] messages = result.get();
		printTestMessages(messages);
		assertTestMessageMatches(messages, new JSONObject(
				"{messageId:'TESTC', fVersion:'v3'}"), JSONCompareMode.LENIENT);
		assertTestMessageMatches(messages, new JSONObject(
				"{messageId:'RUNTIME'}"), JSONCompareMode.LENIENT);
	}

}
