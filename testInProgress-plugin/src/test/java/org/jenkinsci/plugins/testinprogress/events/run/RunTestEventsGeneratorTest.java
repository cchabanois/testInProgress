package org.jenkinsci.plugins.testinprogress.events.run;

import org.jenkinsci.plugins.testinprogress.events.run.RunTestEventsGenerator;
import org.jenkinsci.plugins.testinprogress.events.run.IRunTestEventListener;
import org.jenkinsci.plugins.testinprogress.events.run.TestStartEvent;
import org.jenkinsci.plugins.testinprogress.events.run.TestTreeEvent;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class RunTestEventsGeneratorTest {
	private RunTestEventsGenerator eventsGenerator;
	private IRunTestEventListener listener = mock(IRunTestEventListener.class);

	@Before
	public void setUp() {
		this.eventsGenerator = new RunTestEventsGenerator(
				new IRunTestEventListener[] { listener });
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
