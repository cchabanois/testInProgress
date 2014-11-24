package org.jenkinsci.testinprogress.server.messages.jdt;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.jenkinsci.testinprogress.server.messages.ITestRunListener;
import org.junit.Test;

public class JdtTestMessagesParserTest {
	private ITestRunListener testRunListener = mock(ITestRunListener.class);

	@Test
	public void testTestMessagesParser() {
		// Given
		JdtTestMessagesParser handler = new JdtTestMessagesParser(
				new ITestRunListener[] { testRunListener });
		String allMessages = getAllMessages(false);
		// When
		handler.processTestMessages(new StringReader(allMessages));
		// Then
		verify(testRunListener).testRunStarted(anyLong(), isNull(String.class));
		verify(testRunListener).testTreeEntry(anyLong(), eq("1"),
				eq("testproject.AllTests"), isNull(String.class), eq(true));
		verify(testRunListener).testStarted(anyLong(), eq("3"),
				eq("testAddWillFail(testproject.CalcTest)"), eq(false));
		verify(testRunListener).testStarted(anyLong(), eq("4"),
				eq("testIgnored(testproject.CalcTest)"), eq(true));
	}

	@Test
	public void testTestMessagesParserWithTimestampField() {
		// Given
		JdtTestMessagesParser handler = new JdtTestMessagesParser(
				new ITestRunListener[] { testRunListener });
		String allMessages = getAllMessages(true);
		// When
		handler.processTestMessages(new StringReader(allMessages));
		// Then
		verify(testRunListener).testRunStarted(0, null);
		verify(testRunListener).testTreeEntry(0, "1", "testproject.AllTests",
				null, true);
		verify(testRunListener).testStarted(500, "3",
				"testAddWillFail(testproject.CalcTest)", false);
		verify(testRunListener).testStarted(4500, "4",
				"testIgnored(testproject.CalcTest)", true);
	}

	@Test
	public void testMessageParserFromFile() {
		// Given
		JdtTestMessagesParser handler = new JdtTestMessagesParser(
				new ITestRunListener[] { testRunListener });
		InputStream is = getClass().getResourceAsStream(
				"ProgressCalcTestSuite.events");
		// When
		handler.processTestMessages(new InputStreamReader(is));
		// Then
		verify(testRunListener).testRunStarted(anyLong(), isNull(String.class));
		verify(testRunListener).testRunEnded(anyLong(), anyLong());
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