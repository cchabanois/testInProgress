package org.jenkinsci.plugins.testinprogress.messages;

import java.io.StringReader;

import org.jenkinsci.plugins.testinprogress.messages.TestMessagesParser;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class TestMessagesParserTest {
	private ITestRunListener testRunListener = mock(ITestRunListener.class);

	@Test
	public void testTestMessagesParser() {
		// Given
		TestMessagesParser handler = new TestMessagesParser(false,
				new ITestRunListener[] { testRunListener });
		String allMessages = getAllMessages(false);

		// When
		handler.processTestMessages(new StringReader(allMessages));

		// Then
		verify(testRunListener).testRunStarted(anyLong(),eq(6));
		verify(testRunListener).testTreeEntry(anyLong(),eq("1,testproject.AllTests,true,2"));
		verify(testRunListener).testStarted(anyLong(), eq("3"),
				eq("testAddWillFail(testproject.CalcTest)"));
		verify(testRunListener).testStarted(anyLong(), eq("4"),
				eq("@Ignore: testIgnored(testproject.CalcTest)"));
	}

	@Test
	public void testTestMessagesParserWithTimestampField() {
		// Given
		TestMessagesParser handler = new TestMessagesParser(true,
				new ITestRunListener[] { testRunListener });
		String allMessages = getAllMessages(true);

		// When
		handler.processTestMessages(new StringReader(allMessages));

		// Then
		verify(testRunListener).testRunStarted(0,6);
		verify(testRunListener).testTreeEntry(0, "1,testproject.AllTests,true,2");
		verify(testRunListener).testStarted(500, "3",
				"testAddWillFail(testproject.CalcTest)");
		verify(testRunListener).testStarted(4500, "4",
				"@Ignore: testIgnored(testproject.CalcTest)");
	}

	private String getAllMessages(boolean timeStamp) {
		StringBuilder sb = new StringBuilder();
		if (timeStamp)
			sb.append("0 ");
		sb.append("%TESTC  6 v2\n");
		if (timeStamp)
			sb.append("0 ");
		sb.append("%TSTTREE1,testproject.AllTests,true,2\n");
		if (timeStamp)
			sb.append("500 ");
		sb.append("%TESTS  3,testAddWillFail(testproject.CalcTest)\n");
		if (timeStamp)
			sb.append("4500 ");
		sb.append("%TESTS  4,@Ignore: testIgnored(testproject.CalcTest)\n");

		return sb.toString();
	}

}
