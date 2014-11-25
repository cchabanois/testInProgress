package org.jenkinsci.testinprogress.server.messages;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.jenkinsci.testinprogress.server.messages.jdt.JdtTestMessagesParser;
import org.junit.Test;

public class AllVersionsTestMessagesParserTest {

	@Test
	public void testParseJsonV3Format() {
		// Given
		ITestRunListener testRunListener = mock(ITestRunListener.class);
		ITestMessagesParser handler = new AllVersionsTestMessagesParser(
				new ITestRunListener[] { testRunListener });
		InputStream is = getClass().getResourceAsStream(
				"ProgressCalcTestSuite.events");
		int numTests = 6;

		// When
		handler.processTestMessages(new InputStreamReader(is));

		// Then
		verify(testRunListener).testRunStarted(anyLong(), isNull(String.class));
		verify(testRunListener, times(numTests)).testTreeEntry(anyLong(),
				anyString(), anyString(), anyString(), eq(false));
		verify(testRunListener, times(numTests)).testStarted(anyLong(),
				anyString(), anyString(), anyBoolean());
		verify(testRunListener, times(numTests)).testEnded(anyLong(),
				anyString(), anyString(), anyBoolean());
		verify(testRunListener).testRunEnded(anyLong(), anyLong());
	}

	@Test
	public void testParseJsonV2Format() {
		// Given
		ITestRunListener testRunListener = mock(ITestRunListener.class);
		ITestMessagesParser handler = new AllVersionsTestMessagesParser(
				new ITestRunListener[] { testRunListener });
		InputStream is = getClass().getResourceAsStream(
				"json/v2/ProgressCalcTestSuite.events");
		int numTests = 6;

		// When
		handler.processTestMessages(new InputStreamReader(is));

		// Then
		verify(testRunListener).testRunStarted(anyLong(), isNull(String.class));
		verify(testRunListener, times(numTests)).testTreeEntry(anyLong(),
				anyString(), anyString(), anyString(), eq(false));
		verify(testRunListener, times(numTests)).testStarted(anyLong(),
				anyString(), anyString(), anyBoolean());
		verify(testRunListener, times(numTests)).testEnded(anyLong(),
				anyString(), anyString(), anyBoolean());
		verify(testRunListener).testRunEnded(anyLong(), anyLong());
	}	
	
	@Test
	public void testParseJdtFormat() {
		// Given
		ITestRunListener testRunListener = mock(ITestRunListener.class);
		ITestMessagesParser handler = new AllVersionsTestMessagesParser(
				new ITestRunListener[] { testRunListener });
		InputStream is = getClass().getResourceAsStream(
				"jdt/ProgressCalcTestSuite.events");
		// When
		handler.processTestMessages(new InputStreamReader(is));
		// Then
		verify(testRunListener).testRunStarted(anyLong(), isNull(String.class));
		verify(testRunListener).testRunEnded(anyLong(), anyLong());
	}

}
