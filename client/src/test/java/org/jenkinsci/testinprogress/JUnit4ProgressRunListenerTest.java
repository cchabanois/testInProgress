package org.jenkinsci.testinprogress;

import static org.jenkinsci.testinprogress.utils.TestMessageUtils.assertTestMessageMatches;
import static org.jenkinsci.testinprogress.utils.TestMessageUtils.getTestMessagesMatching;
import static org.jenkinsci.testinprogress.utils.TestMessageUtils.printTestMessages;
import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.jenkinsci.testinprogress.messagesender.SimpleMessageSenderFactory;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.skyscreamer.jsonassert.JSONCompareMode;

import tests.AssumptionNotVerifiedTest;
import tests.CalcTestsSuite;
import tests.EmptyTest;
import tests.IgnoredTest;
import tests.InitializationErrorTest;
import tests.ParallelSuiteTest;
import tests.RuleErrorTest;
import tests.SameTestsSuite;

public class JUnit4ProgressRunListenerTest {

	@Test
	public void testFirstMessageIsTestRunStart() {
		// Given
		Class<?> suiteClass = CalcTestsSuite.class;
		
		//When
		JSONObject[] messages = runTests(suiteClass);
		
		// Then
		assertEquals("TESTC", messages[0].getString("messageId"));
	}
	
	@Test
	public void testLatestMessageIsTestRunEnd() {
		// Given
		Class<?> suiteClass = CalcTestsSuite.class;

		// When
		JSONObject[] messages = runTests(suiteClass);
		
		// Then
		assertEquals("RUNTIME", messages[messages.length-1].getString("messageId"));
	}
	
	/**
	 * TODO : If the same test is run several times in the suite, we use the same id ...
	 */
	@Test
	public void testSameTestMultipleTimes() {
		// Given
		Class<?> suiteClass = SameTestsSuite.class;
		JSONObject expectedMessage = new JSONObject();
		expectedMessage.put("testName", "tests.CalcTestsSuite");
		
		// When
		JSONObject[] messages = runTests(suiteClass);
		
		// Then
		List<JSONObject> matchingMessages = getTestMessagesMatching(messages, expectedMessage, JSONCompareMode.LENIENT);
		assertEquals(2,matchingMessages.size());
		assertEquals("3", matchingMessages.get(0).getString("testId"));
		assertEquals("3", matchingMessages.get(1).getString("testId"));
	}
	
	@Test
	public void testInitializationErrorTest() {
		// Given
		Class<?> testClass = InitializationErrorTest.class;
		JSONObject expectedMessage = new JSONObject();
		expectedMessage.put("testName", "initializationError(tests.InitializationErrorTest)");
		expectedMessage.put("messageId", "ERROR");
		
		// When
		JSONObject[] messages = runTests(testClass);
		
		// Then
		assertTestMessageMatches(messages, expectedMessage, JSONCompareMode.LENIENT);
	}
	
	@Test
	public void testRuleErrorTest() {
		// Given
		Class<?> testClass = RuleErrorTest.class;
		String suiteName = "tests.RuleErrorTest";
		JSONObject expectedMessage = new JSONObject();
		expectedMessage.put("testName", suiteName);
		expectedMessage.put("messageId", "ERROR");
		
		// When
		JSONObject[] messages = runTests(testClass);
		
		// Then
		printTestMessages(messages);
		// error is associated with a suite, not a test
		assertTestMessageMatches(messages, expectedMessage, JSONCompareMode.LENIENT);		
	}
	
	@Test
	public void testEmptyTest() {
		// Given
		Class<?> testClass = EmptyTest.class;

		// When
		JSONObject[] messages = runTests(testClass);

		// Then
		assertTestMessageMatches(
				messages,
				new JSONObject(
						"{testName:'initializationError(tests.EmptyTest)',messageId:'TSTTREE'}"),
				JSONCompareMode.LENIENT);
		assertTestMessageMatches(
				messages,
				new JSONObject(
						"{testName:'initializationError(tests.EmptyTest)',messageId:'TESTS'}"),
				JSONCompareMode.LENIENT);
		assertTestMessageMatches(
				messages,
				new JSONObject(
						"{testName:'initializationError(tests.EmptyTest)',messageId:'ERROR'}"),
				JSONCompareMode.LENIENT);
		assertTestMessageMatches(
				messages,
				new JSONObject(
						"{testName:'initializationError(tests.EmptyTest)',messageId:'TESTE'}"),
				JSONCompareMode.LENIENT);
	}
	
	@Test
	public void testParallelTests() {
		// Given
		Class<?> testClass = ParallelSuiteTest.class;
		
		// When
		JSONObject[] messages = runTests(testClass);
		
		// Then
		printTestMessages(messages);
	}
	
	@Test
	public void testIgnoredTest() {
		// Given
		Class<?> testClass = IgnoredTest.class;
		
		// When
		JSONObject[] messages = runTests(testClass);
		
		// Then
		assertTestMessageMatches(
				messages,
				new JSONObject(
						"{testName:'testIgnore(tests.IgnoredTest)',messageId:'TESTS',ignored:true}"),
				JSONCompareMode.LENIENT);
		assertTestMessageMatches(
				messages,
				new JSONObject(
						"{testName:'testIgnore(tests.IgnoredTest)',messageId:'TESTE',ignored:true}"),
				JSONCompareMode.LENIENT);
	}
	
	@Test
	public void testAssumptionTest() {
		// Given
		Class<?> testClass = AssumptionNotVerifiedTest.class;
		
		// When
		JSONObject[] messages = runTests(testClass);

		// Then
		assertTestMessageMatches(
				messages,
				new JSONObject(
						"{testName:'testAssumptionNotVerified(tests.AssumptionNotVerifiedTest)',messageId:'FAILED',assumptionFailed:true}"),
				JSONCompareMode.LENIENT);
	}
	
	private JSONObject[] runTests(Class<?>... classes) {
		JUnitCore core= new JUnitCore();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		core.addListener(new JUnit4ProgressRunListener(new SimpleMessageSenderFactory(pw)));
		core.run(classes);
		String[] messages = sw.toString().split("\n");
		JSONObject[] jsonObjects = new JSONObject[messages.length];
		for (int i = 0; i < messages.length; i++) {
			jsonObjects[i] = new JSONObject(messages[i]);
		}
		return jsonObjects;
	}
	
}
