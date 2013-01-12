package org.jenkinsci.plugins.testinprogress.events;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class TestEventsGeneratorTest {
	private EventsGenerator eventsGenerator;
	private ITestEventListener listener = mock(ITestEventListener.class);

	@Before
	public void setUp() {
		this.eventsGenerator = new EventsGenerator(
				new ITestEventListener[] { listener });
	}

	@Test
	public void testIgnoredTest() {
		// Given

		// When
		eventsGenerator.testStarted("4",
				"@Ignore: testIgnored(testproject.CalcTest)");

		// Then
		verify(listener).event(
				new TestStartEvent("4", "testIgnored(testproject.CalcTest)",
						true));
	}

	@Test
	public void testTreeEventTest() {
		// Given

		// When
		eventsGenerator.testTreeEntry("1,testproject.AllTests,true,2");

		// Then
		verify(listener).event(
				new TestTreeEvent("1", "testproject.AllTests", true, 2));
	}

}
