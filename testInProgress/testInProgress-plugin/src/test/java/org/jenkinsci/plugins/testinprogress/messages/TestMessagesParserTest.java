package org.jenkinsci.plugins.testinprogress.messages;

import java.io.StringReader;

import org.jenkinsci.plugins.testinprogress.messages.TestMessagesParser;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class TestMessagesParserTest {
	private ITestRunListener testRunListener = mock(ITestRunListener.class);
	
	@Test
	public void testTestMessagesHandler() {
		// Given
		TestMessagesParser handler = new TestMessagesParser(new ITestRunListener[] { testRunListener });
		String allMessages = getAllMessages();
		
		// When
		handler.processTestMessages(new StringReader(allMessages)) ;
		
		// Then
		verify(testRunListener).testRunStarted(6);
		verify(testRunListener).testTreeEntry("1,testproject.AllTests,true,2");
		verify(testRunListener).testStarted("3", "testAddWillFail(testproject.CalcTest)");
		verify(testRunListener).testStarted("4", "@Ignore: testIgnored(testproject.CalcTest)");
	}

	private String getAllMessages() {
		StringBuilder sb = new StringBuilder();
		sb.append("%TESTC  6 v2\n");
		sb.append("%TSTTREE1,testproject.AllTests,true,2\n");
		sb.append("%TESTS  3,testAddWillFail(testproject.CalcTest)\n");
		sb.append("%TESTS  4,@Ignore: testIgnored(testproject.CalcTest)\n");
		
		return sb.toString();
	}	
	
}
