package org.jenkinsci.testinprogress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.testinprogress.messagesender.SimpleMessageSenderFactory;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import tests.CalcTestsSuite;
import tests.EmptyTest;
import tests.InitializationErrorTest;
import tests.ParallelSuiteTest;
import tests.RuleErrorTest;
import tests.SameTestsSuite;


public class JUnit4ProgressRunListenerTest {

	@Test
	public void testFirstMessageIsTestRunStart() {
		String[] messages = runTests(CalcTestsSuite.class);
		assertThat(messages[0], containsString("%TESTC  6 v2"));
	}
	
	@Test
	public void testLatestMessageIsTestRunEnd() {
		String[] messages = runTests(CalcTestsSuite.class);
		assertThat(messages[messages.length-1], containsString("%RUNTIME"));
	}
	
	/**
	 * TODO : If the same test is run several times in the suite, we use the same id ...
	 */
	@Test
	public void testSameTestMultipleTimes() {
		String[] messages = runTests(SameTestsSuite.class);
		List<String> matchingMessages = getTestMessagesMatching(messages, "%TSTTREE3,tests.CalcTestsSuite,true,2");
		assertEquals(2,matchingMessages.size());
	}
	
	@Test
	public void testInitializationErrorTest() {
		String[] messages = runTests(InitializationErrorTest.class);
		assertNotNull(getTestMessageMatching(messages, "%TSTTREE3,initializationError(tests.InitializationErrorTest),false,1"));
		assertNotNull(getTestMessageMatching(messages, "%ERROR  3,initializationError(tests.InitializationErrorTest)"));
	}
	
	@Test
	public void testRuleErrorTest() {
		String[] messages = runTests(RuleErrorTest.class);
		// error is associated with a suite, not a test
		assertNotNull(getTestMessageMatching(messages, "%TSTTREE2,tests.RuleErrorTest,true,1"));
		assertNotNull(getTestMessageMatching(messages, "%ERROR  2,tests.RuleErrorTest"));
	}
	
	@Test
	public void testEmptyTest() {
		String[] messages = runTests(EmptyTest.class);
		assertNotNull(getTestMessageMatching(messages, "%TSTTREE3,initializationError(tests.EmptyTest),false,1"));
		assertNotNull(getTestMessageMatching(messages, "%ERROR  3,initializationError(tests.EmptyTest)"));
	}
	
	@Test
	public void testParallelTests() {
		String[] messages = runTests(ParallelSuiteTest.class);
		printTestMessages(messages);
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
			if (message.equals(expectedMessage)) {
				result.add(message);
			}
		}
		return result;
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
	
	
}
