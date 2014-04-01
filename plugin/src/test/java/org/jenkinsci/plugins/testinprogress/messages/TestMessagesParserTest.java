package org.jenkinsci.plugins.testinprogress.messages;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.junit.Test;

public class TestMessagesParserTest {
	

	@Test
	public void testTestMessagesParser() {
		ITestRunListener testRunListener = mock(ITestRunListener.class);
		// Given
		TestMessagesParser handler = new TestMessagesParser(
				new ITestRunListener[] { testRunListener });
		String allMessages = getAllMessages(false);

		// When
		handler.processTestMessages(new StringReader(allMessages));

		// Then
		verify(testRunListener).testRunStarted(anyLong(), eq(6));
		verify(testRunListener).testTreeEntry(anyLong(),eq("1"),eq("testproject.AllTests"),eq(""),eq(""),eq(true),eq(2));
		verify(testRunListener).testStarted(anyLong(), eq("3"),
				eq("testAddWillFail(testproject.CalcTest)"),eq(false));
		verify(testRunListener).testStarted(anyLong(), eq("4"),
				eq("testIgnored(testproject.CalcTest)"),eq(true));
	}

	@Test
	public void testTestMessagesParserWithTimestampField() {
		ITestRunListener testRunListener = mock(ITestRunListener.class);
		// Given
		TestMessagesParser handler = new TestMessagesParser(
				new ITestRunListener[] { testRunListener });
		String allMessages = getAllMessages(true);

		// When
		handler.processTestMessages(new StringReader(allMessages));

		// Then
		verify(testRunListener).testRunStarted(0, 6);
		verify(testRunListener).testTreeEntry(0,"1","testproject.AllTests","","",true,2);
		verify(testRunListener).testStarted(500, "3","testAddWillFail(testproject.CalcTest)",false);
		verify(testRunListener).testStarted(4500, "4",
				"testIgnored(testproject.CalcTest)",true);
	}

	@Test
	public void testMessageParserFromFile() {
		ITestRunListener testRunListener = mock(ITestRunListener.class);
		// Given
		TestMessagesParser handler = new TestMessagesParser(
				new ITestRunListener[] { testRunListener });
		InputStream is = getClass().getResourceAsStream(
				"ProgressCalcTestSuite.events");

		// When
		handler.processTestMessages(new InputStreamReader(is));

		// Then
		verify(testRunListener).testRunStarted(anyLong(), anyInt());
		verify(testRunListener).testRunEnded(anyLong(), anyLong());
	}

	private String getAllMessages(boolean timeStamp) {
		StringBuilder sb = new StringBuilder();
		String timeSt="";
		if (timeStamp)
			timeSt="timeStamp:0,";
		sb.append("{"+timeSt+"messageId:TESTC,testCount:6,fVersion:v2}\n");
		sb.append("{"+timeSt+"messageId:TSTTREE,testId:1,testName:testproject.AllTests,isSuite:true,testCount:2}\n");
		if (timeStamp)
			timeSt="timeStamp:500,";
		sb.append("{"+timeSt+"messageId:TESTS,testId:3,testName:testAddWillFail(testproject.CalcTest)}\n");
		if (timeStamp)
			timeSt="timeStamp:4500,";
		sb.append("{"+timeSt+"messageId:TESTS,testId:4,testName:testIgnored(testproject.CalcTest),ignored:true}\n");

		return sb.toString();
	}

}
