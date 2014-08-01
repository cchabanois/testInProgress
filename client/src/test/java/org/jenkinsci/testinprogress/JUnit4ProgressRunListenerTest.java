package org.jenkinsci.testinprogress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jenkinsci.testinprogress.messagesender.SimpleMessageSenderFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

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
		String[] messages = runTests(CalcTestsSuite.class);
		
		assertThat(messages[0], containsString("{\"testCount\":6,\"runId\":\"\",\"messageId\":\"TESTC\",\"fVersion\":\"v2\"}"));
	}
	
	@Test
	public void testLatestMessageIsTestRunEnd() {
		String[] messages = runTests(CalcTestsSuite.class);
		assertThat(messages[messages.length-1], containsString("RUNTIME"));
	}
	
	/**
	 * TODO : If the same test is run several times in the suite, we use the same id ...
	 */
	@Test
	public void testSameTestMultipleTimes() {
		String[] messages = runTests(SameTestsSuite.class);
		List<String> matchingMessages = getTestMessagesMatching(messages, "{\"parentId\":\"\",\"testName\":\"tests.CalcTestsSuite\",\"testId\":\"3\",\"testCount\":2,\"runId\":\"\",\"messageId\":\"TSTTREE\",\"isSuite\":true,\"parentName\":\"\"}");
		assertEquals(2,matchingMessages.size());
	}
	
	@Test
	public void testInitializationErrorTest() {
		String[] messages = runTests(InitializationErrorTest.class);
		
		assertDataPresent(messages, "{\"parentId\":\"\",\"testName\":\"initializationError(tests.InitializationErrorTest)\",\"testId\":\"3\",\"testCount\":1,\"messageId\":\"TSTTREE\",\"runId\":\"\",\"isSuite\":false,\"parentName\":\"\"}");
		Assert.assertTrue(verifyValuesPresentInAnyJson(messages, "{\"testName\":\"initializationError(tests.InitializationErrorTest)\",\"testId\":\"3\",\"messageId\":\"ERROR\"}"));
	}
	
	@Test
	public void testRuleErrorTest() {
		String[] messages = runTests(RuleErrorTest.class);
		// error is associated with a suite, not a test
		assertDataPresent(messages, "{\"parentId\":\"\",\"testName\":\"tests.RuleErrorTest\",\"testId\":\"2\",\"testCount\":1,\"messageId\":\"TSTTREE\",\"runId\":\"\",\"isSuite\":true,\"parentName\":\"\"}");
		Assert.assertTrue(verifyValuesPresentInAnyJson(messages, "{\"testName\":\"tests.RuleErrorTest\",\"testId\":\"2\",\"messageId\":\"ERROR\",\"runId\":\"\"}"));
	}
	
	@Test
	public void testEmptyTest() {
		String[] messages = runTests(EmptyTest.class);
		
		assertDataPresent(
				messages,
				"{\"parentId\":\"\",\"testName\":\"initializationError(tests.EmptyTest)\",\"testId\":\"3\",\"testCount\":1,\"messageId\":\"TSTTREE\",\"runId\":\"\",\"isSuite\":false,\"parentName\":\"\"}");
		Assert.assertTrue(verifyValuesPresentInAnyJson(
				messages,
				"{\"testName\":\"initializationError(tests.EmptyTest)\",\"testId\":\"3\",\"messageId\":\"ERROR\",\"runId\":\"\"}"));
	}
	
	@Test
	public void testParallelTests() {
		String[] messages = runTests(ParallelSuiteTest.class);
		printTestMessages(messages);
	}
	
	@Test
	public void testIgnoredTest() {
		String[] messages = runTests(IgnoredTest.class);
		assertDataPresent(messages, "{\"ignored\":true,\"testName\":\"testIgnore(tests.IgnoredTest)\",\"testId\":\"3\",\"messageId\":\"TESTS\",\"runId\":\"\"}");
		assertDataPresent(messages, "{\"ignored\":true,\"testName\":\"testIgnore(tests.IgnoredTest)\",\"testId\":\"3\",\"messageId\":\"TESTE\",\"runId\":\"\"}");		
	}
	
	@Test
	public void testAssumptionTest() {
		String[] messages = runTests(AssumptionNotVerifiedTest.class);
		
		Assert.assertTrue(verifyValuesPresentInAnyJson(messages, "{\"testName\":\"testAssumptionNotVerified(tests.AssumptionNotVerifiedTest)\",\"testId\":\"3\",\"assumptionFailed\":true,\"messageId\":\"FAILED\"}"));	
	}
	
	private String getTestMessageMatching(String[] messages, String regex) {
		List<String> testMatchings = getTestMessagesMatching(messages, regex);
		if (testMatchings.size() == 0) {
			return null;
		} else if (testMatchings.size() == 1) {
			return testMatchings.get(0);
		} else {
			fail("More than one message matching "+regex);
			return null;
		}
	}
	
	private List<String> getTestMessagesMatching(String[] messages, String expectedMessage)  {
		List<String> result = new ArrayList<String>();
		
		for (String message : messages) {
			
			if (message.contentEquals(expectedMessage)) {
				result.add(message);
			}
		}
		return result;
	}
	
	private void assertDataPresent(String[] messages, String regex){
		JSONArray jsonArray = new JSONArray(messages);
		JSONObject expectedJson = new JSONObject(regex);
		boolean matchFound = false;
		for(int i = 0; i<jsonArray.length(); i++){
			JSONObject jsonMessage = new JSONObject(jsonArray.getString(i));
			JSONCompareResult jsonResult = JSONCompare.compareJSON(expectedJson, jsonMessage, JSONCompareMode.NON_EXTENSIBLE);
			if(jsonResult.passed()){
				matchFound=true;
				break;
			}
		}		
		Assert.assertTrue(matchFound);
	}
	
	private void printTestMessages(String[] messages) {
		for (String message : messages) {
			System.out.println(message);
		}
	}
	
	private String[] runTests(Class<?>... classes) {
		JUnitCore core= new JUnitCore();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		core.addListener(new JUnit4ProgressRunListener(new SimpleMessageSenderFactory(pw)));
		core.run(classes);
		return sw.toString().split("\n");
	}
	
	private boolean verifyValuesPresentInAnyJson(String[] messages, String regex){
		JSONArray jsonArray = new JSONArray(messages);
		JSONObject expectedJson = new JSONObject(regex);
		boolean matchFound = false;
		for(int i = 0; i<jsonArray.length(); i++){
			JSONObject jsonMessage = new JSONObject(jsonArray.getString(i));
			if(jsonMessage.getString("messageId").contentEquals(expectedJson.getString("messageId"))){
				Iterator<String> keys = expectedJson.keys();
				while(keys.hasNext()){
					String key = keys.next();
					if(!jsonMessage.isNull(key)){
						if(jsonMessage.get(key).equals(expectedJson.get(key)))
							matchFound = true;
						else
							matchFound = false;
					}
				}
				if(matchFound)
					break;
			}
		}
		return matchFound;
	}
	
	
}
