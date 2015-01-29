package org.jenkinsci.testinprogress.runner;

import static org.jenkinsci.testinprogress.utils.TestMessageUtils.assertTestMessageMatches;

import java.util.concurrent.Future;

import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import tests.ProgressCalcTestsSuite;

public class ProgressSuiteTest extends AbstractProgressSuiteTest {

	@Test
	public void testProgressSuite() throws Exception {
		// Given
		Class<?> classUsingProgressSuiteRunner = ProgressCalcTestsSuite.class;
		
		// When
		Future<JSONObject[]> result = runProgressSuite(classUsingProgressSuiteRunner);

		// Then
		JSONObject[] messages = result.get();
		assertTestMessageMatches(messages, new JSONObject(
				"{messageId:'TESTC', fVersion:'v3'}"), JSONCompareMode.LENIENT);
		assertTestMessageMatches(messages, new JSONObject(
				"{messageId:'RUNTIME'}"), JSONCompareMode.LENIENT);
	}



}
