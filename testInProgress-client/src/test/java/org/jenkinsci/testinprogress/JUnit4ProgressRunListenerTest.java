package org.jenkinsci.testinprogress;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jenkinsci.testinprogress.JUnit4ProgressRunListener;
import org.jenkinsci.testinprogress.messagesender.SimpleMessageSenderFactory;
import org.jenkinsci.testinprogress.tests.CalcTestsSuite;
import org.jenkinsci.testinprogress.tests.SameTestsSuite;
import org.junit.Test;
import org.junit.runner.JUnitCore;


public class JUnit4ProgressRunListenerTest {

	@Test
	public void testSuite() {
		String messages = runTests(CalcTestsSuite.class);
		assertThat(messages, containsString("%TESTC  6 v2"));
		assertThat(messages, containsString("%RUNTIME"));
	}
	
	@Test
	public void testSameTestMultipleTimes() {
		String messages = runTests(SameTestsSuite.class);
		System.out.println(messages);
	}
	
	private String runTests(Class<?>... classes) {
		JUnitCore core= new JUnitCore();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		core.addListener(new JUnit4ProgressRunListener(new SimpleMessageSenderFactory(pw)));
		core.run(classes);
		return sw.toString();
	}
	
	
}
